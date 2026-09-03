import { createClient } from 'https://esm.sh/@supabase/supabase-js@2.45.6'

const supabaseUrl = Deno.env.get('SUPABASE_URL') ?? ''
const supabaseServiceRole = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''

const supabaseAdmin = createClient(supabaseUrl, supabaseServiceRole, {
  auth: { autoRefreshToken: false, persistSession: false },
})

const elevenLabsApiKey = Deno.env.get('ELEVENLABS_API_KEY') ?? ''
const elevenLabsVoiceId = Deno.env.get('ELEVENLABS_VOICE_ID') ?? 'wBXNqKUATyqu0RtYt25i'
const elevenLabsModelId = Deno.env.get('ELEVENLABS_MODEL_ID') ?? 'eleven_turbo_v2'

const ELEVENLABS_BASE_URL = 'https://api.elevenlabs.io/v1'

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
    const { text } = body

    if (!text || typeof text !== 'string' || text.trim().length === 0) {
      return jsonResponse({ error: 'INVALID_REQUEST' }, 400)
    }

    // Check user's voice settings
    const { data: settings, error: settingsError } = await supabaseAdmin
      .from('user_settings')
      .select('voice_enabled')
      .eq('user_id', userId)
      .single()

    if (settingsError) {
      return jsonResponse({ error: 'DATABASE_ERROR' }, 500)
    }

    if (!settings.voice_enabled) {
      return jsonResponse({ error: 'VOICE_DISABLED' }, 403)
    }

    // Call ElevenLabs API with timeout
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 15000)

    let elevenLabsResponse: Response
    try {
      elevenLabsResponse = await fetch(
        `${ELEVENLABS_BASE_URL}/text-to-speech/${elevenLabsVoiceId}/stream`,
        {
          method: 'POST',
          headers: {
            'xi-api-key': elevenLabsApiKey,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            text: text,
            model_id: elevenLabsModelId,
            voice_settings: {
              stability: 0.5,
              similarity_boost: 0.5,
            },
          }),
          signal: controller.signal,
        },
      )
    } catch (e) {
      if (e instanceof DOMException && e.name === 'AbortError') {
        return jsonResponse({ error: 'ELEVENLABS_TIMEOUT' }, 504)
      }
      return jsonResponse({ error: 'ELEVENLABS_ERROR' }, 502)
    } finally {
      clearTimeout(timeoutId)
    }

    if (!elevenLabsResponse.ok) {
      const errorText = await elevenLabsResponse.text()
      if (elevenLabsResponse.status === 429) {
        return jsonResponse({ error: 'RATE_LIMITED' }, 429)
      }
      return jsonResponse({ error: 'ELEVENLABS_ERROR' }, 502)
    }

    // Return the audio as a base64-encoded string
    const audioBuffer = await elevenLabsResponse.arrayBuffer()
    const audioBase64 = arrayBufferToBase64(audioBuffer)

    return jsonResponse({
      audio: audioBase64,
      mime_type: 'audio/mpeg',
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

function arrayBufferToBase64(buffer: ArrayBuffer): string {
  const bytes = new Uint8Array(buffer)
  let binary = ''
  for (let i = 0; i < bytes.byteLength; i++) {
    binary += String.fromCharCode(bytes[i])
  }
  return btoa(binary)
}
