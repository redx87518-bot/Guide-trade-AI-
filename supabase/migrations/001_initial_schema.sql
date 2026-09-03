-- 001_initial_schema.sql
-- Guide Trade AI - Initial Database Schema
-- Creates all tables with UUIDs, timestamps, and proper constraints

-- Enable pgcrypto extension for UUID generation
create extension if not exists "pgcrypto" schema public;

-- Profiles table
create table public.profiles (
  id uuid references auth.users not null primary key,
  full_name text,
  avatar_url text,
  created_at timestamptz default now() not null,
  updated_at timestamptz default now() not null
);

-- Chat sessions table
create table public.chat_sessions (
  id uuid default gen_random_uuid() not null primary key,
  user_id uuid references auth.users not null,
  title text default 'New Chat' not null,
  created_at timestamptz default now() not null,
  updated_at timestamptz default now() not null
);

-- Chat messages table
create table public.chat_messages (
  id uuid default gen_random_uuid() not null primary key,
  session_id uuid references public.chat_sessions on delete cascade not null,
  user_id uuid references auth.users not null,
  role text check (role in ('user', 'assistant')) not null,
  content text not null,
  created_at timestamptz default now() not null
);

-- Research results table
create table public.research_results (
  id uuid default gen_random_uuid() not null primary key,
  user_id uuid references auth.users not null,
  session_id uuid references public.chat_sessions on delete set null,
  title text not null,
  query text not null,
  asset text,
  response text not null,
  created_at timestamptz default now() not null
);

-- User settings table
create table public.user_settings (
  user_id uuid references auth.users not null primary key,
  voice_enabled boolean default true not null,
  auto_speak boolean default false not null,
  theme text default 'dark' check (theme in ('dark', 'light', 'system')) not null,
  created_at timestamptz default now() not null,
  updated_at timestamptz default now() not null
);

-- Telegram settings table
create table public.telegram_settings (
  user_id uuid references auth.users not null primary key,
  bot_token_encrypted text,
  chat_id text,
  enabled boolean default false not null,
  send_research boolean default true not null,
  send_chat_results boolean default false not null,
  created_at timestamptz default now() not null,
  updated_at timestamptz default now() not null
);

-- Indexes for performance
create index if not exists idx_chat_sessions_user_id on public.chat_sessions(user_id);
create index if not exists idx_chat_sessions_updated_at on public.chat_sessions(updated_at desc);
create index if not exists idx_chat_messages_session_id on public.chat_messages(session_id);
create index if not exists idx_chat_messages_session_created on public.chat_messages(session_id, created_at);
create index if not exists idx_chat_messages_user_id on public.chat_messages(user_id);
create index if not exists idx_research_results_user_id on public.research_results(user_id);
create index if not exists idx_research_results_created_at on public.research_results(created_at desc);
create index if not exists idx_research_results_session_id on public.research_results(session_id);
