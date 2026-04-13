# Admin Seed Refactor and Optimization

## Overview
A solicitação visa refatorar e otimizar o endpoint `/admin/seed` presente no `AdminRoutes.kt`. O objetivo principal é tornar a persistência de informações "mais profissional, otimizada e consistente", lidando com o fato de que as informações estão sendo apagadas ao invés do banco ser resetado. A abordagem ideal para este cenário é utilizar o comando `upsert` nativo do PostgREST/Supabase em vez de processamentos iterativos locais.

## Project Type
**BACKEND**

## Success Criteria
- O código do `AdminRoutes.kt` e helpers deve estar conciso, limpo e profissional.
- A inserção deve ser resiliente a execuções repetidas sem gerar erros de unicidade ou chaves nulas (PostgREST constraint issue resolvido).
- Garantir que a lógica reflita perfeitamente upserts ou inserts otimizados, sem "apagar" dados que não deveriam ser apagados (Idempotência Otimizada).
- Baixo uso de banda/rede (otimização de transações online).

## Tech Stack
- **Kotlinx Serialization** & **Supabase-kt**: Comunicação otimizada nativa.
- **Supabase PostgREST**: Utilização de `upsert` via SDK.

## File Structure
- `server/src/main/kotlin/com/fatec/merge_skills_kmp/routes/AdminRoutes.kt` (Refatoração da rota)
- `shared/src/commonMain/kotlin/com/fatec/merge_skills_kmp/domain/models/*` (Modelos DTO compatíveis com API remota)

---

## 🛑 Socratic Gate (Open Questions for the User)

> **ANTES DE EXECUTAR (VIA `/create` ou aprovar a rota), POR FAVOR PRECISE:**
> 1. Você mencionou *"as informações estão sendo apagadas"*. O endpoint de Seed local atual não roda nenhum delete! Algumas relações no Supabase estão como *ON DELETE CASCADE* e apagando questões quando algo atualiza, ou você as apaga manualmente na Dashboard para testar? 
> 2. Se mudarmos do método atual (que checa 1 por 1 e só faz `insert` no que não existe) para o poderoso **Upsert** do DB (`client.from().upsert()`), qualquer alteração manual que você fez no Supabase será sobrescrita pelos dados do arquivo local JSON. Você deseja esse comportamento de **Sobrescrita Automática via Upsert**, ou prefere a **Inclusão Condicional (Sincronização Lenta)** que usamos no momento?

---

## Task Breakdown

### Task 1: Revisão da API Supabase KT para Suporte a Upsert (Refactor Seed)
- **Agent**: `backend-specialist`
- **Skills**: `api-patterns`
- **Description**: Trocar a estrutura de iteradores em `AdminRoutes.kt` (onde primeiro fazemos Fetch, filtramos e damos Insert parcial) por um envio Batch Nativo de Upsert que diminui queries na API.
- **INPUT**: `AdminRoutes.kt`
- **OUTPUT**: Queries de Upsert utilizando chave de onConflict (ex: `code`, `title` ou `id`).
- **VERIFY**: Verificar no log do Ktor se apenas as rotas de upsert estão subindo.

### Task 2: Controller/Service Pattern (Limpeza do Ktor)
- **Agent**: `backend-specialist`
- **Skills**: `clean-code`
- **Description**: A Rota `/admin/seed` tem muita lógica em linha (lendo file, desserializando, contendo funções inline como `insertQuestions`). Isso fere o Clean Code. Extrair para uma `SeedService.kt`.
- **INPUT**: O arquivo gigante do `AdminRoutes`.
- **OUTPUT**: Lógica contida e isolada no repositório de serviços, deixando o `AdminRoutes` apenas despachador.
- **VERIFY**: Redução do tamanho da Controller do Ktor.

---

## ✅ PHASE X: Verification

- [ ] Linter & Compilação: `./gradlew :server:build` não pode quebrar.
- [ ] Otimização Rede: Supabase logs registram poucos Hits no `/postgrest` (upsert direto batch).
- [ ] Resposta API: Testar Rota para verificar velocidade.
- [ ] Sem Deleções Inesperadas: O BD não terá perda de integridade acidental.
