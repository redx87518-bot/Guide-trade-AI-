import { createClient } from 'https://esm.sh/@supabase/supabase-js@2.45.6'

const supabaseUrl = Deno.env.get('SUPABASE_URL') ?? ''
const supabaseServiceRole = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''

const supabaseAdmin = createClient(supabaseUrl, supabaseServiceRole, {
  auth: { autoRefreshToken: false, persistSession: false },
})

const AGENT_BASE_URL = Deno.env.get('AGENT_BASE_URL') ?? 'https://api.north7.ai/v1'
const AGENT_API_KEY = Deno.env.get('AGENT_API_KEY') ?? ''
const MAX_HISTORY_MESSAGES = 20

async function authenticateUser(req: Request): Promise<string | null> {
  const authHeader = req.headers.get('Authorization')
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return null
  }
  const token = authHeader.replace('Bearer ', '')
  const { data: { user }, error } = await supabaseAdmin.auth.getUser(token)
  if (error || !user) {
    return null
  }
  return user.id
}

function jsonResponse(body: any, status: number) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  })
}

async function getChatHistory(sessionId: string, limit: number = MAX_HISTORY_MESSAGES) {
  const { data: history, error } = await supabaseAdmin
    .from('chat_messages')
    .select('role, content')
    .eq('session_id', sessionId)
    .order('created_at', { ascending: true })
    .limit(limit)

  if (error) {
    console.error('Failed to load chat history:', error)
    return []
  }
  return history || []
}

async function saveMessage(sessionId: string, userId: string, role: string, content: string) {
  const { error } = await supabaseAdmin
    .from('chat_messages')
    .insert({
      session_id: sessionId,
      user_id: userId,
      role,
      content,
    })

  if (error) {
    console.error('Failed to save message:', error)
  }
}

async function getAgentResponse(messages: any[]): Promise<string> {
  if (!AGENT_API_KEY) {
    return 'GuideTrade Agent is not configured. Please contact support.'
  }

  try {
    const response = await fetch(`${AGENT_BASE_URL}/chat`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${AGENT_API_KEY}`,
      },
      body: JSON.stringify({
        messages,
        stream: false,
        temperature: 0.2,
      }),
    })

    if (!response.ok) {
      const errorText = await response.text()
      console.error('Agent API error:', response.status, errorText)
      return 'I apologize, but I encountered an error processing your request. Please try again.'
    }

    const data = await response.json()
    return data?.choices?.[0]?.message?.content || data?.content || 'I apologize, but I received an empty response. Please try again.'
  } catch (error) {
    console.error('Agent API network error:', error)
    return 'I apologize, but I encountered a network error. Please try again.'
  }
}

Deno.serve(async (req) => {
  const userId = await authenticateUser(req)
  if (!userId) {
    return jsonResponse({ error: 'AUTH_REQUIRED' }, 401)
  }

  try {
    const body = await req.json()
    const { action, session_id, message, query, symbol, market } = body

    console.log('Agent orchestrator request:', JSON.stringify({ action, session_id, message, query, symbol, market }))

    if (action === 'chat' || message) {
      const sessionId = session_id || `session_${Date.now()}`
      const userMessage = message || query || ''

      if (!userMessage.trim()) {
        return jsonResponse({ error: 'INVALID_REQUEST', details: 'message is required' }, 400)
      }

      const history = await getChatHistory(sessionId)
      const messages = [
        { role: 'system', content: 'You are GuideTrade Agent, an AI market intelligence assistant. Provide concise, actionable market insights, analysis, and educational information. Always end with a brief disclaimer that this is not financial advice.' },
        ...history.map((msg: { role: string; content: string }) => ({
          role: msg.role,
          content: msg.content,
        })),
        { role: 'user', content: userMessage },
      ]

      await saveMessage(sessionId, userId, 'user', userMessage)

      const aiResponse = await getAgentResponse(messages)

      await saveMessage(sessionId, userId, 'assistant', aiResponse)

      return jsonResponse({
        role: 'assistant',
        content: aiResponse,
        session_id: sessionId,
        timestamp: new Date().toISOString(),
      }, 200)
    }

    if (action === 'market-data' || symbol) {
      const sym = symbol || 'BTC'
      const marketParam = market || 'crypto'

      return jsonResponse({
        symbol: sym,
        market: marketParam,
        provider: 'GUIDETRADE',
        price: null,
        change: null,
        changePercent: null,
        signal: 'NEUTRAL',
        news: [],
        timestamp: new Date().toISOString(),
      }, 200)
    }

    if (action === 'analyze') {
      const analysisQuery = query || message || 'general market analysis'
      const analysisResponse = await getAgentResponse([
        { role: 'system', content: 'You are GuideTrade Agent. Provide market analysis and intelligence.' },
        { role: 'user', content: `Analyze: ${analysisQuery}` },
      ])

      return jsonResponse({
        query: analysisQuery,
        analysis: analysisResponse,
        timestamp: new Date().toISOString(),
      }, 200)
    }

    return jsonResponse({ error: 'INVALID_ACTION', details: 'Supported actions: chat, market-data, analyze' }, 400)
  } catch (error) {
    console.error('agent-orchestrator error:', error)
    return jsonResponse({ error: 'UNKNOWN_ERROR', details: error.message }, 500)
  }
})
