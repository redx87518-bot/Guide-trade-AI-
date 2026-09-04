#!/bin/bash
set -euo pipefail

if [ ! -f supabase/.env ]; then
  echo "Missing supabase/.env. Copy supabase/.env.example to supabase/.env and fill it in."
  exit 1
fi

set -a
source supabase/.env
set +a

if [ -z "${SUPABASE_URL:-}" ] || [ -z "${SUPABASE_SERVICE_ROLE_KEY:-}" ]; then
  echo "SUPABASE_URL or SUPABASE_SERVICE_ROLE_KEY is not set in supabase/.env"
  exit 1
fi

export SUPABASE_URL
export SUPABASE_SERVICE_ROLE_KEY

if [ -z "${QUAN_API_KEY:-}" ] || [ -z "${QUAN_MODEL:-}" ] || [ -z "${ELEVENLABS_API_KEY:-}" ] || [ -z "${ELEVENLABS_VOICE_ID:-}" ] || [ -z "${ENCRYPTION_KEY:-}" ]; then
  echo "One or more required env vars are missing in supabase/.env"
  exit 1
fi

echo "Deploying Supabase edge functions..."
supabase functions deploy ai-chat --file supabase/functions/ai-chat/index.ts
supabase functions deploy telegram-send --file supabase/functions/telegram-send/index.ts
supabase functions deploy telegram-test --file supabase/functions/telegram-test/index.ts
supabase functions deploy text-to-speech --file supabase/functions/text-to-speech/index.ts

echo "Setting edge function secrets..."
supabase secrets set \
  QUAN_API_KEY="${QUAN_API_KEY}" \
  QUAN_MODEL="${QUAN_MODEL}" \
  QUAN_BASE_URL="${QUAN_BASE_URL:-https://stockup.cc/v1/query}" \
  ELEVENLABS_API_KEY="${ELEVENLABS_API_KEY}" \
  ELEVENLABS_VOICE_ID="${ELEVENLABS_VOICE_ID}" \
  ELEVENLABS_MODEL_ID="${ELEVENLABS_MODEL_ID:-eleven_turbo_v2}" \
  TELEGRAM_BOT_API_URL="${TELEGRAM_BOT_API_URL:-https://api.telegram.org}" \
  ENCRYPTION_KEY="${ENCRYPTION_KEY}"

echo "Done."
