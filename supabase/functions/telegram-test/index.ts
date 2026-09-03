import { createClient } from 'https://esm.sh/@supabase/supabase-js@2.45.6'

const supabaseUrl = Deno.env.get('SUPABASE_URL') ?? ''
const supabaseServiceRole = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''

const supabaseAdmin = createClient(supabaseUrl, supabaseServiceRole, {
  auth: { autoRefreshToken: false, persistSession: false },
})

const telegramBotApiUrl = Deno.env.get('TELEGRAM_BOT_API_URL') ?? 'https://api.telegram.org'
const encryptionKey = Deno.env.get('ENCRYPTION_KEY') ?? ''

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

async function encryptToken(token: string): Promise<string> {
  if (!encryptionKey) {
    throw new Error('ENCRYPTION_KEY not configured')
  }
  const encoder = new TextEncoder()
  const data = encoder.encode(token)
  const keyMaterial = await crypto.subtle.importKey(
    'raw',
    encoder.encode(encryptionKey),
    { name: 'AES-GCM' },
    false,
    ['encrypt'],
  )
  const iv = crypto.getRandomValues(new Uint8Array(12))
  const encrypted = await crypto.subtle.encrypt(
    { name: 'AES-GCM', iv },
    keyMaterial,
    data,
  )
  const ivBase64 = btoa(String.fromCharCode(...iv))
  const ctBase64 = btoa(String.fromCharCode(...new Uint8Array(encrypted)))
  return `${ivBase64}:${ctBase64}`
}

Deno.serve(async (req) => {
  const userId = await authenticateUser(req)
  if (!userId) {
    return jsonResponse({ error: 'AUTH_REQUIRED' }, 401)
  }

  try {
    const body = await req.json()
    const { bot_token, chat_id, action } = body

    if (action === 'test') {
      if (!bot_token || typeof bot_token !== 'string') {
        return jsonResponse({ error: 'INVALID_REQUEST' }, 400)
      }
      if (!chat_id || typeof chat_id !== 'string') {
        return jsonResponse({ error: 'INVALID_REQUEST' }, 400)
      }

      // Test the bot token with getMe
      const controller = new AbortController()
      const timeoutId = setTimeout(() => controller.abort(), 10000)

      let tgResponse: Response
      try {
        tgResponse = await fetch(`${telegramBotApiUrl}/bot${bot_token}/getMe`, {
          method: 'GET',
          signal: controller.signal,
        })
      } catch (e) {
        if (e instanceof DOMException && e.name === 'AbortError') {
          return jsonResponse({ error: 'TELEGRAM_ERROR' }, 502)
        }
        return jsonResponse({ error: 'TELEGRAM_ERROR' }, 502)
      } finally {
        clearTimeout(timeoutId)
      }

      if (!tgResponse.ok) {
        return jsonResponse({ error: 'TELEGRAM_ERROR' }, 502)
      }

      // Test sending a message
      const testMessage = 'Guide Trade AI Telegram connection is working.'
      const controller2 = new AbortController()
      const timeoutId2 = setTimeout(() => controller2.abort(), 10000)

      let sendResponse: Response
      try {
        sendResponse = await fetch(`${telegramBotApiUrl}/bot${bot_token}/sendMessage`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            chat_id: chat_id,
            text: testMessage,
          }),
          signal: controller2.signal,
        })
      } catch (e) {
        if (e instanceof DOMException && e.name === 'AbortError') {
          return jsonResponse({ error: 'TELEGRAM_ERROR' }, 502)
        }
        return jsonResponse({ error: 'TELEGRAM_ERROR' }, 502)
      } finally {
        clearTimeout(timeoutId2)
      }

      if (!sendResponse.ok) {
        return jsonResponse({ error: 'TELEGRAM_ERROR' }, 502)
      }

      // Encrypt and store the bot token
      try {
        const encryptedToken = await encryptToken(bot_token)
        const { error: upsertError } = await supabaseAdmin
          .from('telegram_settings')
          .upsert({
            user_id: userId,
            bot_token_encrypted: encryptedToken,
            chat_id: chat_id,
            enabled: true,
          })

        if (upsertError) {
          return jsonResponse({ error: 'DATABASE_ERROR' }, 500)
        }
      } catch (e) {
        return jsonResponse({ error: 'ENCRYPTION_FAILED' }, 500)
      }

      return jsonResponse({ success: true, message: 'Telegram connection confirmed and settings saved.' }, 200)
    }

    if (action === 'save') {
      if (!bot_token || typeof bot_token !== 'string') {
        return jsonResponse({ error: 'INVALID_REQUEST' }, 400)
      }
      if (!chat_id || typeof chat_id !== 'string') {
        return jsonResponse({ error: 'INVALID_REQUEST' }, 400)
      }

      try {
        const encryptedToken = await encryptToken(bot_token)
        const { error: upsertError } = await supabaseAdmin
          .from('telegram_settings')
          .upsert({
            user_id: userId,
            bot_token_encrypted: encryptedToken,
            chat_id: chat_id,
            enabled: true,
          })

        if (upsertError) {
          return jsonResponse({ error: 'DATABASE_ERROR' }, 500)
        }
        return jsonResponse({ success: true, message: 'Settings saved.' }, 200)
      } catch (e) {
        return jsonResponse({ error: 'ENCRYPTION_FAILED' }, 500)
      }
    }

    if (action === 'disable') {
      const { error: upsertError } = await supabaseAdmin
        .from('telegram_settings')
        .upsert({
          user_id: userId,
          enabled: false,
        })

      if (upsertError) {
        return jsonResponse({ error: 'DATABASE_ERROR' }, 500)
      }
      return jsonResponse({ success: true, message: 'Telegram notifications disabled.' }, 200)
    }

    return jsonResponse({ error: 'INVALID_REQUEST' }, 400)
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
