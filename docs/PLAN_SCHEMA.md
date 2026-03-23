# Plan: Correção do Schema SQL e DTOs (Aula-03 e Aula-04)

## Contexto
O usuário forneceu o conteúdo correto do arquivo `initial_schema.sql`, o qual difere consideravelmente da versão que havíamos estruturado.
As principais mudanças do novo schema são:
- Chaves primárias e estrangeiras de `users` passaram de `UUID` para `integer` (serial).
- Novos campos na tabela `users`: `name`, `profile_picture`, `longest_streak`, `role`.
- Novos campos em `questions` (`order`), `lesson_progress` (`completed_at`, constraint UNIQUE), e `question_attempts` (`timestamp` e constraint UNIQUE).

## Objetivo
Atualizar o Schema SQL nas branches `aula-03` e `aula-04`, corrigir os Modelos Kotlin (`Models.kt`) na branch `aula-04` para refletir as novas tipagens, e por fim, alinhar as respectivas documentações no portal `living-docs`.

## Fases de Execução (Parallel Agents)

### 1. Database Architect (`database-architect`)
**Branch `aula-03` (Repositório `mergeskillskmp-`):**
*   Atualizar o arquivo `initial_schema.sql` substituindo-o pelo conteúdo exato fornecido pelo usuário.
*   Commit e Push ("fix: update initial_schema with auto-generated remote database schema").

### 2. Backend Specialist (`backend-specialist`)
**Branch `aula-04` (Repositório `mergeskillskmp-`):**
*   Garantir que o novo `initial_schema.sql` também esteja na branch `aula-04` (via git checkout ou merge).
*   Refatorar exaustivamente o pacote de domínio compartilhado (`shared/src/commonMain/kotlin/com/fatec/merge_skills_kmp/domain/models/Models.kt`), mudando todos os `val userId: String` para `val userId: Int`, adicionando os campos `name`, `profilePicture`, `role`, `longestStreak`, etc., em concordância estrita com o novo schema Postgres.
*   De igual forma, revisar e corrigir as rotas ou repositórios (como o `UserRepository` / `AuthDTOs` se necessário).
*   Commit e Push.

### 3. Documentation Writer (`documentation-writer`)
**Living-Docs (`living-docs`):**
*   **Atualizar `aula-03.mdx`**: Atualizar a visualização em bloco de código do `initial_schema.sql` e as observações (já que não estamos mais usando `UUID` para identificador primário nas tabelas do Supabase, remover essa "dica técnica").
*   **Atualizar `aula-04.mdx`**: Atualizar o `Models.kt` documentado na aula 04, demonstrando os novos tipos primitivos correspondentes. Atualizar o mapa mental (Int em vez de UUID, etc.).

### 4. Test Engineer (`test-engineer`)
*   Assegurar que o Kotlin (`shared`) compila as assinaturas modificadas.
*   Realizar Run no Docusaurus e validar que a base de conhecimento subiu com sucesso.

## Verification Plan
O Schema de Aula-03 conterá a versão oficial da Nuvem e o Kotlin de Aula-04 representará fielmente o mapeamento Objeto-Relacional do Supabase (IDs como Inteiros, etc.).
