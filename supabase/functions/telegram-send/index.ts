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
    const { research_id, query, response, title, asset, session_id, user_message } = body

    if (!response) {
      return jsonResponse({ error: 'INVALID_REQUEST' }, 400)
    }

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

    const date = new Date().toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    })

    let message = ''
    const isResearch = research_id !== undefined && research_id !== null
    const isChat = session_id !== undefined && session_id !== null

    if (isResearch && tgSettings.send_research) {
      const shortSummary = truncateText(response.replace(/[#*\[\]()~`><]/g, ''), 300)
      message =
        'GUIDE TRADE AI\n\n' +
        'AI RESEARCH\n\n' +
        `* ${escapeMarkdown(title || 'Research Result')} *\n\n` +
        `Date: ${date}\n\n` +
        `Summary:\n${escapeMarkdown(shortSummary)}\n\n` +
        `Key Factors:\n` +
        `• ${escapeMarkdown(asset || 'General market research')}\n\n` +
        escapeMarkdown('AI-generated research only. Not financial advice. Verify before trading.')
    } else if (isChat && tgSettings.send_chat_results) {
      const shortResponse = truncateText(response.replace(/[#*\[\]()~`><]/g, ''), 300)
      const shortQuery = truncateText((user_message || query || '').replace(/[#*\[\]()~`><]/g, ''), 200)
      message =
        'GUIDE TRADE AI\n\n' +
        'AI CHAT RESULT\n\n' +
        `Date: ${date}\n\n` +
        `You asked:\n${escapeMarkdown(shortQuery)}\n\n` +
        `Quan replied:\n${escapeMarkdown(shortResponse)}\n\n` +
        escapeMarkdown('AI-generated chat response. Not financial advice.')
    } else {
      return jsonResponse({ error: 'NOT_ENABLED' }, 403)
    }

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

    if (research_id) {
      await supabaseAdmin
        .from('research_results')
        .update({ response })
        .eq('id', research_id)
        .eq('user_id', userId)
        .select()
        .maybeSingle()
    }

    return jsonResponse({
      success: true,
      message: isResearch ? 'Research result sent to Telegram.' : 'Chat result sent to Telegram.',
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
