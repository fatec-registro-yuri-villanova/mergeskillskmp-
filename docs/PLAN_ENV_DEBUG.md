# Orchestration Plan: Debugging Ktor Environment Variables

## Context
The user executed `./gradlew server:run` expecting Ktor to start with Supabase credentials injected, but it failed with:
`java.lang.IllegalStateException: SupabaseClient is null. Check Environment Variables.`

This indicates that `Application.kt` could not retrieve `System.getProperty("SUPABASE_URL")` despite running via Gradle.

## Probable Causes
1. **Missing `local.properties` file**: The file might not exist in the exact directory `rootProject.file("local.properties")` expects, or the variable names inside are mismatched (e.g., `supabaseUrl` instead of `SUPABASE_URL`).
2. **Gradle `jvmArgs` vs `systemProp`**: In Ktor plugin setups, `applicationDefaultJvmArgs` might not correctly pass system properties to the running Netty process depending on the Ktor Gradle Plugin version.
3. **Environment vs System properties**: `System.getProperty` reads JVM properties (`-D`), whereas the code might be expecting `System.getenv` (Environment Variables) in some layers.

## Proposed Multi-Agent Workflow

### Phase 1: Planning (Current)
- `project-planner`: Documented this plan and probable causes.

### Phase 2: Implementation & Debugging (Parallel Execution)

1. **Agent: `explorer-agent`**
   - **Task**: Inspect the presence and variable names of `local.properties` (safely, without exposing the raw secret in public logs). Verify `server/build.gradle.kts` injection behavior.
   
2. **Agent: `backend-specialist`**
   - **Task**: Fix the property loading mechanism. 
   - **Action**: Modify `Application.kt` or `build.gradle.kts` to robustly read the properties. E.g., load the `local.properties` file directly inside `Application.kt` if the Gradle injection proves unreliable.

3. **Agent: `test-engineer`**
   - **Task**: Verify the fix locally.
   - **Action**: Run the server and check if the SupabaseClient initializes successfully.

## Deliverables
- Reliable Ktor Supabase environment initialization.
- Server properly returning data on `/api/courses`.
