-- Initial Schema for Merge Skills
-- Week 03 LDM Class

-- 1. Profiles (linked to Supabase Auth)
CREATE TABLE IF NOT EXISTS profiles (
    id UUID PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    full_name TEXT,
    avatar_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 2. Courses
CREATE TABLE IF NOT EXISTS courses (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    image_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 3. Lessons
CREATE TABLE IF NOT EXISTS lessons (
    id SERIAL PRIMARY KEY,
    course_id INT REFERENCES courses(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    content TEXT, -- Markdown or JSON content
    "order" INT DEFAULT 0,
    video_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 4. Questions (for Quizzes/Simulados)
CREATE TABLE IF NOT EXISTS questions (
    id SERIAL PRIMARY KEY,
    lesson_id INT REFERENCES lessons(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    options JSONB NOT NULL, -- Array of strings
    correct_option_index INT NOT NULL,
    explanation TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 5. User Progress (Attempts/Grades)
CREATE TABLE IF NOT EXISTS user_progress (
    id SERIAL PRIMARY KEY,
    user_id UUID REFERENCES profiles(id) ON DELETE CASCADE,
    lesson_id INT REFERENCES lessons(id) ON DELETE CASCADE,
    score FLOAT DEFAULT 0,
    completed BOOLEAN DEFAULT FALSE,
    last_attempt TIMESTAMPTZ DEFAULT NOW()
);
