-- 003_triggers.sql
-- Guide Trade AI - Database Triggers

-- =========================================================================
-- Auto-create profile and default settings after signup
-- =========================================================================
create function public.handle_new_user()
returns trigger
language plpgsql
security definer
as $$
begin
  insert into public.profiles (id, full_name, avatar_url, created_at, updated_at)
  values (
    new.id,
    new.raw_user_meta_data->>'full_name',
    new.raw_user_meta_data->>'avatar_url',
    now(),
    now()
  );

  insert into public.user_settings (user_id, voice_enabled, auto_speak, theme, created_at, updated_at)
  values (new.id, true, false, 'dark', now(), now());

  return new;
end;
$$;

create trigger on_auth_user_created
  after insert on auth.users
  for each row
  execute function public.handle_new_user();

-- =========================================================================
-- Update updated_at timestamps on various tables
-- =========================================================================
create function public.handle_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

create trigger handle_profiles_updated_at
  before update on public.profiles
  for each row
  execute function public.handle_updated_at();

create trigger handle_chat_sessions_updated_at
  before update on public.chat_sessions
  for each row
  execute function public.handle_updated_at();

create trigger handle_user_settings_updated_at
  before update on public.user_settings
  for each row
  execute function public.handle_updated_at();

create trigger handle_telegram_settings_updated_at
  before update on public.telegram_settings
  for each row
  execute function public.handle_updated_at();

-- =========================================================================
-- Update chat_sessions updated_at when messages or research are inserted
-- =========================================================================
create function public.update_session_timestamp()
returns trigger
language plpgsql
as $$
begin
  update public.chat_sessions
  set updated_at = now()
  where id = new.session_id;
  return new;
end;
$$;

create trigger handle_session_timestamp
  after insert on public.chat_messages
  for each row
  execute function public.update_session_timestamp();

create trigger handle_session_timestamp_on_research
  after insert on public.research_results
  for each row
  when (new.session_id is not null)
  execute function public.update_session_timestamp();
