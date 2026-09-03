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

async function decryptToken(encryptedToken: string): Promise<string> {
  if (!encryptionKey) {
    throw new Error('ENCRYPTION_KEY not configured')
  }
  const [ivBase64, ctBase64] = encryptedToken.split(':')
  const iv = base64ToBuffer(ivBase64)
  const ct = base64ToBuffer(ctBase64)
  const keyMaterial = await crypto.subtle.importKey(
    'raw',
    new TextEncoder().encode(encryptionKey),
    { name: 'AES-GCM' },
    false,
    ['decrypt'],
  )
  const decrypted = await crypto.subtle.decrypt(
    { name: 'AES-GCM', iv },
    keyMaterial,
    ct,
  )
  return new TextDecoder().decode(decrypted)
}

function base64ToBuffer(base64: string): Uint8Array {
  const binary = atob(base64)
  const bytes = new Uint8Array(binary.length)
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i)
  }
  return bytes
}

function escapeMarkdown(text: string): string {
  return text.replace(/[_*`\[\]()~`>#+=|{}.!]/g, '\\$&')
}

function truncateText(text: string, maxLength: number): string {
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength - 3) + '...'
}

Deno.serve(async (req) => {
  const userId = await authenticateUser(req)
  if (!userId) {
    return jsonResponse({ error: 'AUTH_REQUIRED' }, 401)
  }

  try {
    const body = await req.json()
    const { research_id, query, response, title, asset } = body

    if (!response || !title) {
      return jsonResponse({ error: 'INVALID_REQUEST' }, 400)
    }

    // Get user's Telegram settings
    const { data: tgSettings, error: tgError } = await supabaseAdmin
      .from('telegram_settings')
      .select('bot_token_encrypted, chat_id, enabled, send_research, send_chat_results')
      .eq('user_id', userId)
      .single()

    if (tgError || !tgSettings) {
      return jsonResponse({ error: 'TELEGRAM_NOT_CONFIGURED' }, 400)
    }

    if (!tgSettings.enabled) {
      return jsonResponse({ error: 'TELEGRAM_DISABLED' }, 403)
    }

    if (!tgSettings.bot_token_encrypted) {
      return jsonResponse({ error: 'TELEGRAM_NOT_CONFIGURED' }, 400)
    }

    // Decrypt the bot token
    let botToken: string
    try {
      botToken = await decryptToken(tgSettings.bot_token_encrypted)
    } catch (e) {
      return jsonResponse({ error: 'TELEGRAM_ERROR' }, 502)
    }

    const chatId = tgSettings.chat_id
    if (!chatId) {
      return jsonResponse({ error: 'TELEGRAM_NOT_CONFIGURED' }, 400)
    }

    // Build the Telegram message
    const date = new Date().toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    })

    const shortSummary = truncateText(response.replace(/[#*\[\]()~`><]/g, ''), 300)

    const message =
      'GUIDE TRADE AI\n\n' +
      'AI RESEARCH\n\n' +
      `* ${escapeMarkdown(title)} *\n\n` +
      `Date: ${date}\n\n` +
      `Summary:\n${escapeMarkdown(shortSummary)}\n\n` +
      `Key Factors:\n` +
      `• ${escapeMarkdown(asset || 'General market research')}\n\n` +
      `Disclaimer:\n` +
      escapeMarkdown(
        'AI-generated research only. Not financial advice. Verify before trading.',
      )

    // Split into chunks if message is too long (Telegram limit: 4096 chars)
    const chunks: string[] = []
    if (message.length <= 4096) {
      chunks.push(message)
    } else {
      let remaining = message
      while (remaining.length > 4096) {
        const splitIdx = remaining.lastIndexOf('\n', 4090)
        if (splitIdx === -1) {
          chunks.push(remaining.substring(0, 4096))
          remaining = remaining.substring(4096)
        } else {
          chunks.push(remaining.substring(0, splitIdx))
          remaining = remaining.substring(splitIdx + 1)
        }
      }
      if (remaining.length > 0) {
        chunks.push(remaining)
      }
    }

    // Send each chunk
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 15000)

    for (const chunk of chunks) {
      try {
        const tgResponse = await fetch(`${telegramBotApiUrl}/bot${botToken}/sendMessage`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            chat_id: chatId,
            text: chunk,
            parse_mode: 'MarkdownV2',
          }),
          signal: controller.signal,
        })

        if (!tgResponse.ok) {
          clearTimeout(timeoutId)
          return jsonResponse({ error: 'TELEGRAM_ERROR' }, 502)
        }
      } catch (e) {
        clearTimeout(timeoutId)
        if (e instanceof DOMException && e.name === 'AbortError') {
          return jsonResponse({ error: 'TELEGRAM_ERROR' }, 502)
        }
        return jsonResponse({ error: 'TELEGRAM_ERROR' }, 502)
      }
    }

    clearTimeout(timeoutId)

    // If this was a saved research result, update the telegram status
    if (research_id) {
      await supabaseAdmin
        .from('research_results')
        .update({ response: response })
        .eq('id', research_id)
        .eq('user_id', userId)
        .select()
        .maybeSingle()
    }

    return jsonResponse({
      success: true,
      message: 'Research result sent to Telegram.',
      chunks_sent: chunks.length,
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
