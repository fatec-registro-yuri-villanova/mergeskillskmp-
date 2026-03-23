# Orchestration Plan: Documentation Audit and Sync

## Context
We have made several technical fixes across `mergeskillskmp-` branches (`aula-02` to `aula-05`), including:
1. Changing IDs from UUID (String) to Integer (Int).
2. Updating Supabase Postgrest syntax from `.body` to `.data`.
3. Adding a fallback for `local.properties` in `Application.kt`.
4. Modularizing the Ktor server in `aula-05`.

The user wants to ensure the `living-docs` repo reflects these changes accurately for each lesson.

## Audit Scope
- `aula-01.mdx`: Project setup.
- `aula-02.mdx`: Networking & Supabase Client. (Already updated with a tip, but needs check for `.data` consistency).
- `aula-03.mdx`: Database & Schema. (Needs check for Integer ID references).
- `aula-04.mdx`: Models & Repositories. (Needs check for Integer IDs and correct Postgrest syntax).
- `aula-05.mdx`: Ktor Architecture. (Already updated for `configureRouting`, but needs general audit).

## Proposed Multi-Agent Workflow

### Phase 1: Planning (Current)
- `project-planner`: Documented this audit plan.

### Phase 2: Implementation (Audit & Update)

1. **Agent: `explorer-agent`**
   - **Task**: Read all `aula-XX.mdx` files in `living-docs` and identify discrepancies with the "correct" technical state (Int IDs, `.data`, etc.).
   
2. **Agent: `documentation-writer`**
   - **Task**: Update the MDX files in `living-docs` to fix identified issues.
   - **Action**: Ensure code snippets and explanations are pedagogically consistent.

3. **Agent: `test-engineer`**
   - **Task**: Verification.
   - **Action**: Run `npm run build` in `living-docs` to ensure no MDX breakages.

## Deliverables
- Fully synchronized and technically accurate documentation for Lessons 01-05.
- Successful documentation build.
