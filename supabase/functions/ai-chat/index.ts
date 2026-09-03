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

    // Verify the session belongs to this user
    const { data: session, error: sessionError } = await supabaseAdmin
      .from('chat_sessions')
      .select('id')
      .eq('id', session_id)
      .eq('user_id', userId)
      .single()

    if (sessionError || !session) {
      return jsonResponse({ error: 'INVALID_REQUEST' }, 400)
    }

    // Retrieve conversation history (limited context)
    const { data: history, error: historyError } = await supabaseAdmin
      .from('chat_messages')
      .select('role, content')
      .eq('session_id', session_id)
      .order('created_at', { ascending: true })
      .limit(MAX_HISTORY_MESSAGES)

    if (historyError) {
      return jsonResponse({ error: 'DATABASE_ERROR' }, 500)
    }

    // Build OpenAI-compatible messages for Quan API
    const messages = history.map((msg: { role: string; content: string }) => ({
      role: msg.role,
      content: msg.content,
    }))
    messages.push({ role: 'user', content: message })

    // Call Quan API with timeout
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 30000)

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
        }),
        signal: controller.signal,
      })
    } catch (e) {
      if (e instanceof DOMException && e.name === 'AbortError') {
        return jsonResponse({ error: 'QUAN_TIMEOUT' }, 504)
      }
      return jsonResponse({ error: 'QUAN_ERROR' }, 502)
    } finally {
      clearTimeout(timeoutId)
    }

    if (!quanResponse.ok) {
      const errorText = await quanResponse.text()
      if (quanResponse.status === 429) {
        return jsonResponse({ error: 'RATE_LIMITED' }, 429)
      }
      if (quanResponse.status >= 500) {
        return jsonResponse({ error: 'QUAN_ERROR' }, 502)
      }
      return jsonResponse({ error: 'QUAN_ERROR' }, 502)
    }

    const quanData = await quanResponse.json()

    if (!quanData.choices || !quanData.choices[0] || !quanData.choices[0].message) {
      return jsonResponse({ error: 'INVALID_RESPONSE' }, 502)
    }

    const aiResponse: string = quanData.choices[0].message.content

    // Store user message
    const { error: userMsgError } = await supabaseAdmin
      .from('chat_messages')
      .insert({
        session_id,
        user_id: userId,
        role: 'user',
        content: message,
      })

    if (userMsgError) {
      return jsonResponse({ error: 'DATABASE_ERROR' }, 500)
    }

    // Store AI response
    const { error: aiMsgError } = await supabaseAdmin
      .from('chat_messages')
      .insert({
        session_id,
        user_id: userId,
        role: 'assistant',
        content: aiResponse,
      })

    if (aiMsgError) {
      return jsonResponse({ error: 'DATABASE_ERROR' }, 500)
    }

    return jsonResponse({
      role: 'assistant',
      content: aiResponse,
      timestamp: new Date().toISOString(),
      usage: quanData.usage ?? null,
    }, 200)
  } catch (error) {
    return jsonResponse({ error: 'UNKNOWN_ERROR' }, 500)
  }
})

function jsonResponse(body: any, status: number) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  })
}
