# Orchestration Plan: Debugging Empty Courses API Response

## Context
The user is on branch `aula-02` and executing Exercise 3. The `GET /api/courses` endpoint successfully compiles and runs but returns an empty JSON array `[]`, despite the user claiming there are courses registered in the Supabase remote database. 

## Probable Causes
1. **Row Level Security (RLS)**: The `courses` table in the Supabase Postgres database has RLS enabled but lacks a policy allowing public/anonymous read access (`SELECT`). The Supabase Postgrest API silently returns `[]` when RLS blocks the query.
2. **Empty Database**: The user might have inserted the data into a local instance or a different project, or the seed query was rolled back.
3. **Environment Variables**: The server might be connecting to the wrong Supabase project if `SUPABASE_URL` and `SUPABASE_KEY` are incorrectly set.

## Proposed Multi-Agent Workflow

### Phase 1: Planning (Current)
- `project-planner`: Documented this plan and probable causes.

### Phase 2: Implementation & Debugging (Parallel Execution)

1. **Agent: `database-architect`**
   - **Task**: Investigate the database's RLS status.
   - **Action**: Check if RLS is enabled on `public.courses`. If so, draft the SQL command to create a read-only policy for anonymous users.

2. **Agent: `debugger`**
   - **Task**: Verify environment variables and database connectivity.
   - **Action**: Add logging to `Application.kt` to ensure the `supabaseUrl` corresponds to the intended project and log any potential Postgrest exceptions.

3. **Agent: `backend-specialist`**
   - **Task**: Implement robust error handling.
   - **Action**: Wrap the `select()` call in a `try-catch` block to capture and print underlying SDK errors, rather than defaulting silently to `[]`. This improves observability.

4. **Agent: `test-engineer`**
   - **Task**: Verify the fix.
   - **Action**: Run the application and execute a cURL request to `/api/courses` to confirm data is returned.

## Deliverables
- A diagnostic script or SQL command to fix Supabase RLS policies.
- An updated `Application.kt` with better error logging for `supabase.postgrest`.
- A verified `/api/courses` endpoint that successfully streams the seeded database rows.
