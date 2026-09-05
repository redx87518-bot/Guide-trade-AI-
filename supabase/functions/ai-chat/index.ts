import { createClient } from 'https://esm.sh/@supabase/supabase-js@2.45.6'

const supabaseUrl = Deno.env.get('SUPABASE_URL') ?? ''
const supabaseServiceRole = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''

const supabaseAdmin = createClient(supabaseUrl, supabaseServiceRole, {
  auth: { autoRefreshToken: false, persistSession: false },
})

const quanBaseUrl = Deno.env.get('QUAN_BASE_URL') ?? 'https://stockup.cc/v1/query'
const quanApiKey = Deno.env.get('QUAN_API_KEY') ?? ''
const quanModel = Deno.env.get('QUAN_MODEL') ?? 'quan-3.4'
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

const systemPrompt = {
  role: 'system',
  content: `You are Quan — an elite AI financial intelligence system built into GuideTrade AI, a premium trading platform. Your responses are precise, authoritative, and structured like a senior analyst.

STYLE RULES:
- Lead every response with the core insight, then support with data
- Use bullet points for lists, **bold** for key figures and terms
- Keep responses focused and scannable — no walls of text
- For analysis, use clean headers to separate sections
- Always mention timeframe context when discussing markets
- End market analysis with a brief "**Quan's Take:**" summary line
- When speaking numbers, be specific — percentages, levels, targets
- Be direct. Do not hedge excessively or add unnecessary disclaimers

EXPERTISE: equities, forex, crypto, macro, technical analysis, options, risk management, portfolio construction.

You are optimised for both text reading and voice playback — keep sentences clear and natural when spoken aloud.`,
}

Deno.serve(async (req) => {
  const userId = await authenticateUser(req)
  if (!userId) {
    return jsonResponse({ error: 'AUTH_REQUIRED' }, 401)
  }

  try {
    const body = await req.json()
    const { session_id, message } = body

    if (!session_id || !message || typeof message !== 'string') {
      return jsonResponse({ error: 'INVALID_REQUEST' }, 400)
    }

    const { data: session, error: sessionError } = await supabaseAdmin
      .from('chat_sessions')
      .select('id')
      .eq('id', session_id)
      .eq('user_id', userId)
      .single()

    if (sessionError || !session) {
      return jsonResponse({ error: 'INVALID_REQUEST' }, 400)
    }

    const { data: history, error: historyError } = await supabaseAdmin
      .from('chat_messages')
      .select('role, content')
      .eq('session_id', session_id)
      .order('created_at', { ascending: true })
      .limit(MAX_HISTORY_MESSAGES)

    if (historyError) {
      console.error('Failed to load chat history:', historyError)
      return jsonResponse({ error: 'DATABASE_ERROR' }, 500)
    }

    const messages = [
      systemPrompt,
      ...history.map((msg: { role: string; content: string }) => ({
        role: msg.role,
        content: msg.content,
      })),
      { role: 'user', content: message },
    ]

    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 60000)

    let quanResponse: Response
    try {
      quanResponse = await fetch(quanBaseUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'x-api-key': quanApiKey,
        },
        body: JSON.stringify({
          model: quanModel,
          messages,
          stream: false,
          googleSearch: true,
          temperature: 0.2,
        }),
        signal: controller.signal,
      })
    } catch (e) {
      if (e instanceof DOMException && e.name === 'AbortError') {
        return jsonResponse({ error: 'QUAN_TIMEOUT' }, 504)
      }
      console.error('Quan network error:', e)
      return jsonResponse({ error: 'QUAN_ERROR' }, 502)
    } finally {
      clearTimeout(timeoutId)
    }

    if (!quanResponse.ok) {
      const errorText = await quanResponse.text()
      console.error('Quan API error:', quanResponse.status, errorText)
      return jsonResponse({
        error: 'QUAN_ERROR',
        details: {
          status: quanResponse.status,
          body: errorText.substring(0, 500),
        }
      }, 502)
    }

    const quanData = await quanResponse.json()
    console.log('Quan API response keys:', Object.keys(quanData))

    const aiResponse = quanData?.candidates?.[0]?.content?.parts?.[0]?.text
      || quanData?.choices?.[0]?.message?.content
      || ''

    if (!aiResponse.trim()) {
      console.error('Empty AI response:', quanData)
      return jsonResponse({
        error: 'INVALID_RESPONSE',
        details: {
          keys: Object.keys(quanData),
          sample: JSON.stringify(quanData).substring(0, 300),
        }
      }, 502)
    }

    const { error: userMsgError } = await supabaseAdmin
      .from('chat_messages')
      .insert({
        session_id,
        user_id: userId,
        role: 'user',
        content: message,
      })

    if (userMsgError) {
      console.error('Failed to save user message:', userMsgError)
      return jsonResponse({ error: 'DATABASE_ERROR' }, 500)
    }

    const { error: aiMsgError } = await supabaseAdmin
      .from('chat_messages')
      .insert({
        session_id,
        user_id: userId,
        role: 'assistant',
        content: aiResponse,
      })

    if (aiMsgError) {
      console.error('Failed to save AI message:', aiMsgError)
      return jsonResponse({ error: 'DATABASE_ERROR' }, 500)
    }

    return jsonResponse({
      role: 'assistant',
      content: aiResponse,
      timestamp: new Date().toISOString(),
    }, 200)
  } catch (error) {
    console.error('Unexpected error:', error)
    return jsonResponse({ error: 'UNKNOWN_ERROR' }, 500)
  }
})

function jsonResponse(body: any, status: number) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  })
}
