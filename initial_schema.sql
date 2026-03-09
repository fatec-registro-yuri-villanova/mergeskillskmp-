-- initial_schema.sql
-- Auto-generated from existing remote database

CREATE TABLE public.courses (
    id serial PRIMARY KEY,
    title text NOT NULL,
    description text,
    icon text,
    color text,
    total_lessons integer DEFAULT 0,
    created_at timestamp with time zone DEFAULT now()
);

CREATE TABLE public.users (
    id serial PRIMARY KEY,
    username text NOT NULL,
    email text,
    name text,
    password text,
    profile_picture text,
    streak_count integer DEFAULT 0,
    last_activity_at timestamp with time zone,
    streak_freezes integer DEFAULT 0,
    longest_streak integer DEFAULT 0,
    role text DEFAULT 'user'::text NOT NULL,
    created_at timestamp with time zone DEFAULT now()
);

CREATE TABLE public.lessons (
    id serial PRIMARY KEY,
    course_id integer REFERENCES public.courses(id),
    title text NOT NULL,
    description text,
    "order" integer,
    created_at timestamp with time zone DEFAULT now()
);

CREATE TABLE public.questions (
    id serial PRIMARY KEY,
    lesson_id integer REFERENCES public.lessons(id),
    question text NOT NULL,
    code text,
    options jsonb DEFAULT '[]'::jsonb,
    correct_answer integer,
    "order" integer,
    created_at timestamp with time zone DEFAULT now()
);

CREATE TABLE public.lesson_progress (
    id serial PRIMARY KEY,
    user_id integer REFERENCES public.users(id),
    lesson_id integer REFERENCES public.lessons(id),
    is_completed boolean DEFAULT false,
    completed_at timestamp with time zone DEFAULT now(),
    UNIQUE(user_id, lesson_id)
);

CREATE TABLE public.question_attempts (
    id serial PRIMARY KEY,
    user_id integer REFERENCES public.users(id),
    question_id integer REFERENCES public.questions(id),
    selected_option integer,
    is_correct boolean DEFAULT false,
    timestamp timestamp with time zone DEFAULT now(),
    UNIQUE(user_id, question_id)
);
