import { createClient } from 'https://esm.sh/@supabase/supabase-js@2.45.6'

const supabaseUrl = Deno.env.get('SUPABASE_URL') ?? ''
const supabaseServiceRole = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''

const supabaseAdmin = createClient(supabaseUrl, supabaseServiceRole, {
  auth: { autoRefreshToken: false, persistSession: false },
})

const GUAVY_BASE_URL = Deno.env.get('GUAVY_BASE_URL') ?? 'https://guavy.com'
const GUAVY_API_KEY = Deno.env.get('GUAVY_API_KEY') ?? ''
const SIFTINGIO_BASE_URL = Deno.env.get('SIFTINGIO_BASE_URL') ?? 'https://sifting.io/api'
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

Deno.serve(async (req) => {
  const userId = await authenticateUser(req)
  if (!userId) {
    return jsonResponse({ error: 'AUTH_REQUIRED' }, 401)
  }

  try {
    const body = await req.json()
    const { provider, feature, market, symbol, timeframe, query } = body

    if (!provider || !feature) {
      return jsonResponse({ error: 'INVALID_REQUEST', details: 'provider and feature are required' }, 400)
    }

    console.log('Market intelligence request:', JSON.stringify({ provider, feature, market, symbol, timeframe, query }))

    let result: any = {}
    let error: string | null = null

    switch (provider) {
      case 'guavy':
        ({ result, error } = await handleGuavyRequest(feature, market, symbol, timeframe))
        break
      case 'siftingio':
        ({ result, error } = await handleSiftingIORequest(feature, market, symbol, timeframe))
        break
      case 'stockup':
        ({ result, error } = await handleStockupRequest(feature, market, symbol, timeframe, query))
        break
      default:
        return jsonResponse({ error: 'UNSUPPORTED_PROVIDER', details: provider }, 400)
    }

    if (error) {
      return jsonResponse({ error, provider, feature, market, symbol }, 502)
    }

    return jsonResponse({
      provider,
      feature,
      market,
      symbol,
      timeframe,
      result,
      timestamp: new Date().toISOString(),
    }, 200)
  } catch (e) {
    console.error('Market intelligence error:', e)
    return jsonResponse({ error: 'UNKNOWN_ERROR' }, 500)
  }
})

async function handleGuavyRequest(feature: string, market: string, symbol: string, timeframe: string) {
  if (!GUAVY_API_KEY) {
    return { result: null, error: 'GUAVY_NOT_CONFIGURED' }
  }

  try {
    const endpoint = resolveGuavyEndpoint(feature, market, symbol, timeframe)
    if (!endpoint) {
      return { result: null, error: 'UNSUPPORTED_GUAVY_FEATURE' }
    }

    const url = `${GUAVY_BASE_URL}${endpoint}`
    console.log('Guavy request:', url)

    const response = await fetch(url, {
      headers: {
        'Authorization': `Bearer ${GUAVY_API_KEY}`,
        'Content-Type': 'application/json',
      },
    })

    if (!response.ok) {
      const text = await response.text()
      console.error('Guavy error:', response.status, text)
      return { result: null, error: `GUAVY_ERROR_${response.status}` }
    }

    const data = await response.json()
    return { result: data, error: null }
  } catch (e) {
    console.error('Guavy request failed:', e)
    return { result: null, error: 'GUAVY_REQUEST_FAILED' }
  }
}

async function handleSiftingIORequest(feature: string, market: string, symbol: string, timeframe: string) {
  if (!SIFTINGIO_API_KEY) {
    return { result: null, error: 'SIFTINGIO_NOT_CONFIGURED' }
  }

  try {
    const endpoint = resolveSiftingIOEndpoint(feature, market, symbol, timeframe)
    if (!endpoint) {
      return { result: null, error: 'UNSUPPORTED_SIFTINGIO_FEATURE' }
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
      return { result: null, error: `SIFTINGIO_ERROR_${response.status}` }
    }

    const data = await response.json()
    return { result: data, error: null }
  } catch (e) {
    console.error('SiftingIO request failed:', e)
    return { result: null, error: 'SIFTINGIO_REQUEST_FAILED' }
  }
}

async function handleStockupRequest(feature: string, market: string, symbol: string, timeframe: string, query: string) {
  const quanBaseUrl = Deno.env.get('QUAN_BASE_URL') ?? 'https://stockup.cc/v1/query'
  const quanApiKey = Deno.env.get('QUAN_API_KEY') ?? ''
  const quanModel = Deno.env.get('QUAN_MODEL') ?? 'quan-3.4'

  if (!quanApiKey) {
    return { result: null, error: 'STOCKUP_NOT_CONFIGURED' }
  }

  try {
    const messages = [
      { role: 'system', content: 'You are Quan, a financial analyst.' },
      { role: 'user', content: query || `Analyze ${symbol} in ${market}` },
    ]

    const response = await fetch(quanBaseUrl, {
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
    })

    if (!response.ok) {
      return { result: null, error: 'STOCKUP_ERROR' }
    }

    const data = await response.json()
    const content = data?.candidates?.[0]?.content?.parts?.[0]?.text || data?.choices?.[0]?.message?.content || ''
    return { result: { content }, error: null }
  } catch (e) {
    return { result: null, error: 'STOCKUP_REQUEST_FAILED' }
  }
}

function resolveGuavyEndpoint(feature: string, market: string, symbol: string, timeframe: string): string | null {
  const marketLower = market.toLowerCase()
  const featureLower = feature.toLowerCase().replace(/\s+/g, '_')

  const endpointMap: Record<string, Record<string, string>> = {
    crypto: {
      instruments: '/api/v2/crypto/instruments/list-symbols',
      scorecard: `/api/v2/crypto/instruments/scorecard/${symbol}`,
      detail: `/api/v2/crypto/instruments/get-detail/${symbol}`,
      search_instruments: `/api/v2/crypto/instruments/search-instruments/${symbol}`,
      sentiment_history: `/api/v2/crypto/sentiment/get-sentiment-history/${symbol}`,
      recent_briefs: `/api/v2/crypto/newsroom/get-recent-briefs/${symbol}`,
      search_briefs: `/api/v2/crypto/newsroom/search-briefs/${symbol}`,
      instrument_analysis: `/api/v2/crypto/newsroom/get-instrument-analysis/${symbol}`,
      market_summary: '/api/v2/crypto/newsroom/get-market-summary',
      trend_history: `/api/v2/crypto/trades/get-trend-history/${symbol}/20`,
      recent_buys: '/api/v2/crypto/trades/get-recent-buys',
      recent_sells: '/api/v2/crypto/trades/get-recent-sells',
      current_action: `/api/v2/crypto/trades/get-current-action/${symbol}/default`,
      recommendations: '/api/v2/crypto/trades/get-recommendations',
      price_history: `/api/v2/crypto/technical-analysis/get-price-history/${symbol}`,
      technical_indicators: `/api/v2/crypto/technical-analysis/get-indicators/${symbol}`,
    },
    forex: {
      instruments: '/api/v2/forex/instruments/list-symbols',
      scorecard: `/api/v2/forex/instruments/scorecard/${symbol}`,
      sentiment_history: `/api/v2/forex/sentiment/get-sentiment-history/${symbol}`,
      recent_briefs: `/api/v2/forex/newsroom/get-recent-briefs/${symbol}`,
      search_briefs: `/api/v2/forex/newsroom/search-briefs/${symbol}`,
      market_summary: '/api/v2/forex/newsroom/get-market-summary',
      current_action: `/api/v2/forex/trades/get-current-action/${symbol}/default`,
      current_trend: `/api/v2/forex/trades/get-current-trend/${symbol}`,
      trend_segments: `/api/v2/forex/trades/get-trend-segments/${symbol}`,
      price_history: `/api/v2/forex/technical-analysis/get-price-history/${symbol}`,
      technical_indicators: `/api/v2/forex/technical-analysis/get-indicators/${symbol}`,
    },
    commodities: {
      instruments: '/api/v2/commodities/instruments/list-symbols',
      scorecard: `/api/v2/commodities/instruments/scorecard/${symbol}`,
      sentiment_history: `/api/v2/commodities/sentiment/get-sentiment-history/${symbol}`,
      recent_briefs: `/api/v2/commodities/newsroom/get-recent-briefs/${symbol}`,
      search_briefs: `/api/v2/commodities/newsroom/search-briefs/${symbol}`,
      market_summary: '/api/v2/commodities/newsroom/get-market-summary',
      current_action: `/api/v2/commodities/trades/get-current-action/${symbol}/default`,
      current_trend: `/api/v2/commodities/trades/get-current-trend/${symbol}`,
      trend_segments: `/api/v2/commodities/trades/get-trend-segments/${symbol}`,
      price_history: `/api/v2/commodities/technical-analysis/get-price-history/${symbol}`,
      technical_indicators: `/api/v2/commodities/technical-analysis/get-indicators/${symbol}`,
    },
    stocks: {
      instruments: '/api/v2/stocks/instruments/list-symbols',
      scorecard: `/api/v2/stocks/instruments/scorecard/${symbol}`,
      sentiment_history: `/api/v2/stocks/sentiment/get-sentiment-history/${symbol}`,
      recent_briefs: `/api/v2/stocks/newsroom/get-recent-briefs/${symbol}`,
      search_briefs: `/api/v2/stocks/newsroom/search-briefs/${symbol}`,
      market_summary: '/api/v2/stocks/newsroom/get-market-summary',
      current_action: `/api/v2/stocks/trades/get-current-action/${symbol}/default`,
      current_trend: `/api/v2/stocks/trades/get-current-trend/${symbol}`,
      trend_segments: `/api/v2/stocks/trades/get-trend-segments/${symbol}`,
      price_history: `/api/v2/stocks/technical-analysis/get-price-history/${symbol}`,
      technical_indicators: `/api/v2/stocks/technical-analysis/get-indicators/${symbol}`,
    },
  }

  return endpointMap[marketLower]?.[featureLower] ?? null
}

function resolveSiftingIOEndpoint(feature: string, market: string, symbol: string, timeframe: string): string | null {
  const venueMap: Record<string, string> = {
    crypto: 'crypto',
    forex: 'forex',
    stocks: 'stocks',
    commodities: 'commodities',
  }

  const venue = venueMap[market.toLowerCase()]
  if (!venue) return null

  const featureLower = feature.toLowerCase().replace(/\s+/g, '_')

  const endpointMap: Record<string, string> = {
    live_price: `/v1/last/price/${venue}/${symbol}`,
    live_trade: `/v1/last/trade/${venue}/${symbol}`,
    live_quote: `/v1/last/quote/${venue}/${symbol}`,
    snapshot: `/v1/snapshot/${venue}/${symbol}`,
    previous_close: `/v1/prev/close/${venue}/${symbol}`,
    historical_ohlcv: `/v1/hist/${venue}/${symbol}/bars`,
    technical_signal: `/v1/last/signals/${venue}/${symbol}`,
    signal_history: `/v1/hist/${venue}/${symbol}/signals`,
    rsi: `/v1/indicators/rsi/${venue}/${symbol}`,
    macd: `/v1/indicators/macd/${venue}/${symbol}`,
    stochastic: `/v1/indicators/stochastic/${venue}/${symbol}`,
    market_status: `/v1/market/status/${venue}`,
    market_hours: `/v1/market/hours/${venue}`,
    search_stocks: `/v1/stocks/search?q=${symbol}`,
    company_profile: `/v1/stocks/profile/${symbol}`,
    currency_converter: `/v1/currency/convert?from=${symbol}&to=USD`,
  }

  return endpointMap[featureLower] ?? null
}
