# Guide Trade AI - Android

AI-powered trading market research assistant for Android.

## Features

- **AI Chat**: Interactive conversations with AI for market research
- **Voice Mode**: Tap the AI orb for voice input and spoken AI responses via ElevenLabs TTS
- **Research History**: Save, browse, and share research results
- **Telegram Integration**: Send research results to Telegram
- **Dark/Light/System Themes**: Full theme support
- **Secure Auth**: Supabase authentication with email/password

## Architecture

```
GuideTradeAI (Android) → Supabase Edge Functions → Quan API / ElevenLabs / Telegram API
                       ↑
                   Supabase DB (PostgreSQL)
```

### Layers

- **Domain**: Models (`User`, `ChatSession`, `ChatMessage`, `ResearchResult`, `UserSettings`, `TelegramSettings`), `Result<T>`
- **Data**: `SupabaseClient`, `AuthRepository`, `ChatRepository`, `ResearchRepository`, `SettingsRepository`, `TelegramRepository`
- **UI**: Compose screens, ViewModels, Navigation, Components
- **Voice**: `VoiceRecognizer` (Speech-to-Text), `AudioPlayer` (TTS playback)

### Edge Functions

| Function | Purpose | Auth Required |
|----------|---------|---------------|
| `ai-chat` | Processes messages via Quan API, stores conversation | Yes |
| `text-to-speech` | Converts text to speech via ElevenLabs | Yes |
| `telegram-test` | Tests and saves Telegram bot config with encryption | Yes |
| `telegram-send` | Sends research/chat results to Telegram | Yes |

## Setup

1. **Clone the repository**
2. **Open in Android Studio** (Giraffe or later, with JDK 17)
3. **Configure Supabase**:
   - Create a [Supabase project](https://supabase.com)
   - Set the URL and anon key in `Constants.kt` and `.env`
   - Apply the database schema and Row Level Security policies
   - Deploy edge functions to your Supabase project
4. **Configure ElevenLabs**:
   - Get an API key from [ElevenLabs](https://elevenlabs.io)
   - Add `ELEVENLABS_API_KEY` to your Supabase secrets
   - (Optional) Customize the voice ID in `Constants.kt`
5. **Configure Telegram**:
   - Create a bot via [@BotFather](https://t.me/BotFather)
   - Get the bot token and chat ID

## Environment Variables

```
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key
ELEVENLABS_API_KEY=your-elevenlabs-key
QUAN_API_KEY=your-quan-api-key
TELEGRAM_BOT_TOKEN=your-telegram-bot-token
```

## Testing

```bash
# Unit tests
./gradlew testDebugUnitTest

# Run on connected device
./gradlew connectedDebugAndroidTest
```

## Build Configuration

- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 14)
- **Language**: Kotlin 2.0.20
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Clean Architecture patterns
