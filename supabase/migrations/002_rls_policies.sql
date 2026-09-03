-- 002_rls_policies.sql
-- Guide Trade AI - Row Level Security Policies
-- Ensures users can only access their own data

-- Enable RLS on all tables
alter table public.profiles enable row level security;
alter table public.chat_sessions enable row level security;
alter table public.chat_messages enable row level security;
alter table public.research_results enable row level security;
alter table public.user_settings enable row level security;
alter table public.telegram_settings enable row level security;

-- =========================================================================
-- profiles
-- =========================================================================
create policy "Users can view their own profile" on public.profiles
  for select using (auth.uid() = id);

create policy "Users can create their own profile" on public.profiles
  for insert with check (auth.uid() = id);

create policy "Users can update their own profile" on public.profiles
  for update using (auth.uid() = id);

-- =========================================================================
-- chat_sessions
-- =========================================================================
create policy "Users can view their own chat sessions" on public.chat_sessions
  for select using (auth.uid() = user_id);

create policy "Users can create their own chat sessions" on public.chat_sessions
  for insert with check (auth.uid() = user_id);

create policy "Users can update their own chat sessions" on public.chat_sessions
  for update using (auth.uid() = user_id);

create policy "Users can delete their own chat sessions" on public.chat_sessions
  for delete using (auth.uid() = user_id);

-- =========================================================================
-- chat_messages
-- =========================================================================
create policy "Users can view messages in their own sessions" on public.chat_messages
  for select using (
    session_id in (
      select id from public.chat_sessions where user_id = auth.uid()
    )
  );

create policy "Users can create messages in their own sessions" on public.chat_messages
  for insert with check (
    user_id = auth.uid()
    and session_id in (
      select id from public.chat_sessions where user_id = auth.uid()
    )
  );

create policy "Users can delete messages in their own sessions" on public.chat_messages
  for delete using (
    session_id in (
      select id from public.chat_sessions where user_id = auth.uid()
    )
  );

-- =========================================================================
-- research_results
-- =========================================================================
create policy "Users can view their own research results" on public.research_results
  for select using (auth.uid() = user_id);

create policy "Users can create their own research results" on public.research_results
  for insert with check (auth.uid() = user_id);

create policy "Users can delete their own research results" on public.research_results
  for delete using (auth.uid() = user_id);

-- =========================================================================
-- user_settings
-- =========================================================================
create policy "Users can view their own settings" on public.user_settings
  for select using (auth.uid() = user_id);

create policy "Users can create their own settings" on public.user_settings
  for insert with check (auth.uid() = user_id);

create policy "Users can update their own settings" on public.user_settings
  for update using (auth.uid() = user_id);

-- =========================================================================
-- telegram_settings
-- =========================================================================
create policy "Users can view their own telegram settings" on public.telegram_settings
  for select using (auth.uid() = user_id);

create policy "Users can create their own telegram settings" on public.telegram_settings
  for insert with check (auth.uid() = user_id);

create policy "Users can update their own telegram settings" on public.telegram_settings
  for update using (auth.uid() = user_id);
