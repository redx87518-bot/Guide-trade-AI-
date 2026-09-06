import { createClient } from 'https://esm.sh/@supabase/supabase-js@2.45.6'

const supabaseUrl = Deno.env.get('SUPABASE_URL') ?? ''
const supabaseServiceRole = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''

const supabaseAdmin = createClient(supabaseUrl, supabaseServiceRole, {
  auth: { autoRefreshToken: false, persistSession: false },
})

const SIFTINGIO_BASE_URL = Deno.env.get('SIFTINGIO_BASE_URL') ?? 'https://api.sifting.io'
const SIFTINGIO_API_KEY = Deno.env.get('SIFTINGIO_API_KEY') ?? ''

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

function resolveSiftingIOEndpoint(feature: string, market: string, symbol: string, timeframe: string): { url: string; requiresGzip: boolean } | null {
  const featureLower = feature.toLowerCase().replace(/\s+/g, '_')
  const tf = timeframe.toLowerCase()

  const endpointMap: Record<string, { url: string; gzip: boolean }> = {
    // Live
    live_trade: { url: `/v1/last/trade/${market}/${symbol}`, gzip: false },
    live_quote: { url: `/v1/last/quote/${market}/${symbol}`, gzip: false },
    previous_close: { url: `/v1/last/close/${market}/${symbol}`, gzip: false },
    snapshot: { url: `/v1/snapshot/${market}`, gzip: true },

    // Signals
    technical_signal: { url: `/v1/last/signals/${market}/${symbol}?interval=${tf}`, gzip: false },
    signal_history: { url: `/v1/hist/${market}/${symbol}/signals?interval=${tf}`, gzip: false },
    full_analysis: { url: `/v1/last/signals/${market}/${symbol}?interval=${tf}`, gzip: false },

    // Historical bars
    historical_price: { url: `/v1/hist/${market}/${symbol}/bars?interval=${tf}`, gzip: true },
    historical_ohlcv: { url: `/v1/hist/${market}/${symbol}/bars?interval=${tf}`, gzip: true },

    // DEX
    dex_tvl: { url: `/v1/last/tvl/${market}/${symbol}`, gzip: false },
    dex_wallet: { url: `/v1/fnd/dex/wallet/${market}/${symbol}`, gzip: false },

    // Convert
    convert: { url: `/v1/convert/${symbol}/USD`, gzip: false },

    // Stocks fundamentals
    search_stocks: { url: `/v1/fnd/stocks/search?q=${symbol}`, gzip: false },
    company_profile: { url: `/v1/fnd/stocks/${symbol}/profile`, gzip: false },
    financials: { url: `/v1/fnd/stocks/${symbol}/financials`, gzip: true },
    ratios: { url: `/v1/fnd/stocks/${symbol}/ratios`, gzip: false },
    insiders: { url: `/v1/fnd/stocks/${symbol}/insiders`, gzip: false },
    ownership: { url: `/v1/fnd/stocks/${symbol}/ownership`, gzip: false },
    filings: { url: `/v1/fnd/stocks/${symbol}/filings`, gzip: false },

    // Markets
    market_status: { url: `/v1/fnd/markets/${market}/status`, gzip: false },
    market_hours: { url: `/v1/fnd/markets/${market}/hours`, gzip: false },
    market_calendar: { url: `/v1/fnd/markets/${market}/calendar`, gzip: false },
    economic_calendar: { url: `/v1/fnd/economic-calendar`, gzip: false },
  }

  const mapped = endpointMap[featureLower]
  if (!mapped) return null
  return mapped
}

Deno.serve(async (req) => {
  const userId = await authenticateUser(req)
  if (!userId) {
    return jsonResponse({ error: 'AUTH_REQUIRED' }, 401)
  }

  try {
    const body = await req.json()
    const { provider, feature, market, symbol, timeframe, query } = body

    if (provider !== 'siftingio') {
      return jsonResponse({ error: 'UNSUPPORTED_PROVIDER', details: provider }, 400)
    }

    if (!feature || !market || !symbol) {
      return jsonResponse({ error: 'INVALID_REQUEST', details: 'feature, market, and symbol are required' }, 400)
    }

    console.log('SiftingIO request:', JSON.stringify({ provider, feature, market, symbol, timeframe, query }))

    if (feature === 'list_symbols') {
      const url = `${SIFTINGIO_BASE_URL}/v1/${market}/symbols`
      console.log('SiftingIO symbol list:', url)

      const response = await fetch(url, {
        headers: {
          'x-api-key': SIFTINGIO_API_KEY,
          'Content-Type': 'application/json',
        },
      })

      if (!response.ok) {
        const text = await response.text()
        console.error('SiftingIO symbol list error:', response.status, text)
        return jsonResponse({ error: `SIFTINGIO_ERROR_${response.status}`, details: text.substring(0, 500) }, response.status)
      }

      const data = await response.json()
      return jsonResponse({ provider: 'SIFTINGIO', feature, market, symbol, timeframe, result: data }, 200)
    }

    const resolved = resolveSiftingIOEndpoint(feature, market, symbol, timeframe)
    if (!resolved) {
      return jsonResponse({ error: 'UNSUPPORTED_SIFTINGIO_FEATURE', details: feature }, 400)
    }

    const url = `${SIFTINGIO_BASE_URL}${resolved.url}`
    console.log('SiftingIO request:', url)

    const headers: Record<string, string> = {
      'x-api-key': SIFTINGIO_API_KEY,
      'Content-Type': 'application/json',
    }

    if (resolved.requiresGzip) {
      headers['Accept-Encoding'] = 'gzip'
    }

    const response = await fetch(url, {
      headers,
    })

    if (!response.ok) {
      const text = await response.text()
      console.error('SiftingIO error:', response.status, text)
      const errorBody = text ? JSON.parse(text) : {}
      return jsonResponse({
        error: errorBody.error || `SIFTINGIO_ERROR_${response.status}`,
        message: errorBody.message || text.substring(0, 500),
        status: response.status,
      }, response.status)
    }

    const data = await response.json()
    return jsonResponse({ provider: 'SIFTINGIO', feature, market, symbol, timeframe, result: data }, 200)
  } catch (e) {
    console.error('SiftingIO error:', e)
    return jsonResponse({ error: 'SIFTINGIO_REQUEST_FAILED', details: e.message }, 500)
  }
})
