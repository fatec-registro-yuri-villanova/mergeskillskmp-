# Plan: Restauração da Progressão Incremental LDM (Aula-03 e Aula-04)

## Contexto

As branches `aula-03` e `aula-04` do repositório `mergeskillskmp-` estão idênticas após o hard-sync anterior, contendo tanto o Schema SQL quanto as Entidades/DTOs e Repositórios.

Segundo o **LDM_LESSON_PLAN.md**:
*   **Semana 3**: Banco de Dados e Schema (Apenas SQL).
*   **Semana 4**: Modelagem de Dados (Entidades) (DTOs e Interfaces de Repositório).

## Objetivo
Isolar o conteúdo da `aula-03` apenas no SQL, e popular corretamente a `aula-04` com os DTOs do Kotlin. Em seguida, atualizar a documentação no `living-docs/docs/ldm/aula-04.mdx`.

## Fases de Execução (Parallel Agents)

### 1. Backend Specialist (`backend-specialist`)
**Branch `aula-03`:**
*   **Remover** `Models.kt`, `AuthDTOs.kt`, `ContentDTOs.kt`, `CourseRepository.kt`, `UserRepository.kt` que foram mesclados prematuramente.
*   **Manter** APENAS o `initial_schema.sql` (incremento sobre aula-02).
*   Realizar commit e push.

**Branch `aula-04`:**
*   Garantir a presença dos arquivos removidos da `aula-03`:
    *   `shared/src/commonMain/kotlin/com/fatec/merge_skills_kmp/domain/models/Models.kt`
    *   `shared/src/commonMain/kotlin/com/fatec/merge_skills_kmp/domain/models/AuthDTOs.kt`
    *   `shared/src/commonMain/kotlin/com/fatec/merge_skills_kmp/domain/models/ContentDTOs.kt`
    *   `shared/src/commonMain/kotlin/com/fatec/merge_skills_kmp/domain/repository/CourseRepository.kt`
    *   `shared/src/commonMain/kotlin/com/fatec/merge_skills_kmp/domain/repository/UserRepository.kt`
*   Realizar commit e push.

### 2. Documentation Writer (`documentation-writer`)
**Living-Docs (`living-docs` repo):**
*   **Refatorar `aula-03.mdx`**: Remover menções aos `Models.kt` e `@Serializable`, focando **apenas** na modelagem do banco de dados em SQL (as 6 tabelas), JSONB, Postgres, Tipos de Dados.
*   **Criar (ou refatorar) `aula-04.mdx`**: Documentar a tradução do Schema SQL para DTOs em Kotlin. Explicar como usar o `@Serializable`, e a arquitetura em `domain/models/` e as abstrações em `domain/repository/`.

### 3. Test Engineer (`test-engineer`)
*   Executar o scanner de Lint para validar a estrutura final do diretório.
*   Garantir que os builds tanto em JVM (server) quanto em shared compilam adequadamente após o remanejamento.
*   Zelar pelo build do Docusaurus (site estático das documentações).

## Verification Plan
1.  O diff `aula-02...aula-03` exibirá apenas `initial_schema.sql`.
2.  O diff `aula-03...aula-04` exibirá a adição dos pacotes do `domain` (`models/` e `repository/`).
3.  O site no repositório `living-docs` compilará sem erros.
