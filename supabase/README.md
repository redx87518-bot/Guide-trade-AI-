# Supabase Setup

## One-time dashboard setup
1. Open your project at https://supabase.com/dashboard/project/dnfutvafibliysnsetwm
2. Go to **Authentication → Providers → Email** and enable email auth.
3. Go to **Authentication → Email → SMTP**:
   - For testing, you can use the built-in Supabase mailer.
   - For production, connect SendGrid/Mailgun.
4. Go to **Authentication → URL Configuration**:
   - Set **Site URL** to your actual site URL.
   - Add `com.guidetradeai://login` to **Redirect URLs**.
5. Go to **Edge Functions → Settings** and confirm these env vars are set:
   - `QUAN_API_KEY`
   - `QUAN_MODEL`
   - `QUAN_BASE_URL`
   - `ELEVENLABS_API_KEY`
   - `ELEVENLABS_VOICE_ID`
   - `ELEVENLABS_MODEL_ID`
   - `TELEGRAM_BOT_API_URL`
   - `ENCRYPTION_KEY`
6. In **Database → Migrations**, apply migrations `001_initial_schema.sql`, `002_rls_policies.sql`, and `003_triggers.sql`.

## Local deploy
1. Copy `supabase/.env.example` to `supabase/.env` and fill in real values.
2. Install Supabase CLI if needed.
3. Run:
   ```bash
   bash scripts/deploy-supabase.sh
   ```

## Verify
- Sign up in the app.
- You should see the verification screen.
- Check your email for the Supabase verification link.
- After verifying, log in.
- Open a chat and send a message; it should call `ai-chat`.
 
