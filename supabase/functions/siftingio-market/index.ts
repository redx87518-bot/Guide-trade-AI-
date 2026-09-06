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

function resolveSiftingIOEndpoint(feature: string, market: string, symbol: string, timeframe: string): string | null {
  const venueMap: Record<string, string> = {
    crypto: 'crypto',
    forex: 'forex',
    stocks: 'stocks',
    commodities: 'commodities',
    dex: 'dex',
  }

  const venue = venueMap[market.toLowerCase()]
  if (!venue) return null

  const featureLower = feature.toLowerCase().replace(/\s+/g, '_')
  const tf = timeframe.toLowerCase()

  const endpointMap: Record<string, string> = {
    technical_signal: `/v1/last/signals/${venue}/${symbol}`,
    signal_history: `/v1/hist/${venue}/${symbol}/signals`,
    full_analysis: `/v1/last/signals/${venue}/${symbol}`,
    live_price: `/v1/last/price/${venue}/${symbol}`,
    latest_price: `/v1/last/price/${venue}/${symbol}`,
    live_trade: `/v1/last/trade/${venue}/${symbol}`,
    live_quote: `/v1/last/quote/${venue}/${symbol}`,
    snapshot: `/v1/snapshot/${venue}/${symbol}`,
    previous_close: `/v1/prev/close/${venue}/${symbol}`,
    historical_price: `/v1/hist/${venue}/${symbol}/bars?interval=${tf}`,
    historical_ohlcv: `/v1/hist/${venue}/${symbol}/bars?interval=${tf}`,
    rsi: `/v1/indicators/rsi/${venue}/${symbol}?interval=${tf}`,
    macd: `/v1/indicators/macd/${venue}/${symbol}?interval=${tf}`,
    stochastic: `/v1/indicators/stochastic/${venue}/${symbol}?interval=${tf}`,
    market_status: `/v1/fnd/markets/${venue}/status`,
    market_hours: `/v1/fnd/markets/${venue}/hours`,
    market_calendar: `/v1/fnd/markets/${venue}/calendar`,
    economic_calendar: `/v1/fnd/economic-calendar`,
    search_stocks: `/v1/fnd/stocks/search?q=${symbol}`,
    company_profile: `/v1/fnd/stocks/${symbol}/profile`,
    financials: `/v1/fnd/stocks/${symbol}/financials`,
    ratios: `/v1/fnd/stocks/${symbol}/ratios`,
    insiders: `/v1/fnd/stocks/${symbol}/insiders`,
    news: `/v1/fnd/news?symbol=${symbol}`,
    filings: `/v1/fnd/stocks/${symbol}/filings`,
  }

  return endpointMap[featureLower] ?? null
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
      const venueMap: Record<string, string> = {
        crypto: 'crypto',
        forex: 'forex',
        stocks: 'stocks',
        commodities: 'commodities',
        dex: 'dex',
      }
      const venue = venueMap[market.toLowerCase()]
      if (!venue) {
        return jsonResponse({ error: 'UNSUPPORTED_MARKET', details: market }, 400)
      }

      const url = `${SIFTINGIO_BASE_URL}/v1/${venue}/symbols`
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

    const endpoint = resolveSiftingIOEndpoint(feature, market, symbol, timeframe)
    if (!endpoint) {
      return jsonResponse({ error: 'UNSUPPORTED_SIFTINGIO_FEATURE', details: feature }, 400)
    }

    const url = `${SIFTINGIO_BASE_URL}${endpoint}`
    console.log('SiftingIO request:', url)

    const response = await fetch(url, {
      headers: {
        'x-api-key': SIFTINGIO_API_KEY,
        'Content-Type': 'application/json',
      },
    })

    if (!response.ok) {
      const text = await response.text()
      console.error('SiftingIO error:', response.status, text)
      return jsonResponse({ error: `SIFTINGIO_ERROR_${response.status}`, details: text.substring(0, 500) }, response.status)
    }

    const data = await response.json()
    return jsonResponse({ provider: 'SIFTINGIO', feature, market, symbol, timeframe, result: data }, 200)
  } catch (e) {
    console.error('SiftingIO error:', e)
    return jsonResponse({ error: 'SIFTINGIO_REQUEST_FAILED', details: e.message }, 500)
  }
})
