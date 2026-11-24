# POC Quality Gate — Catálogo de Acessórios de Impressoras (Java)

API Spring Boot que simula o backend de um site de **catálogo de acessórios de impressoras**
(toners, cartuchos, cilindros, papel, cabos e kits de manutenção), usada para rodar o
**Quality Gate do SonarQube** num projeto com a mesma stack do time:
**Java 25 + Spring Boot 4.1 + Maven + JUnit 5 + JaCoCo**.

## O que a API faz

| Endpoint | O que retorna |
|---|---|
| `GET /api/acessorios` | Catálogo inteiro |
| `GET /api/acessorios?termo=toner` | Busca pelo nome |
| `GET /api/acessorios?categoria=CARTUCHO` | Filtra por categoria (TONER, CARTUCHO, CILINDRO, PAPEL, CABO, MANUTENCAO) |
| `GET /api/acessorios?impressora=Epson EcoTank L3250` | Acessórios compatíveis com a impressora |
| `GET /api/acessorios?precoMax=100&emEstoque=true` | Até R$ 100 e com estoque |
| `GET /api/acessorios/TON-58A` | Detalhe do produto + disponibilidade (ESGOTADO, ULTIMAS_UNIDADES, DISPONIVEL) |

## Estrutura

| Pasta / arquivo | Para que serve |
|---|---|
| `src/main/java/...` | Código **com problemas de propósito** |
| `src/test/java/...` | Poucos testes → cobertura baixa |
| `solucao/src/...` | Versão corrigida do código e dos testes (inclui testes da API com MockMvc) |
| `pom.xml` | Dependências, JaCoCo e scanner do SonarQube para Maven |
| `docker-compose.yml` | Containers do Maven: `analise` (gate) e `api` (sobe o site) |
| `run_poc.ps1` | Confere o SonarQube, roda testes + análise e falha se o gate reprovar |

Problemas plantados no código:

| # | Problema | Regra | Onde |
|---|---|---|---|
| 1 | Senha fixa no código | java:S2068 | `CatalogoService` |
| 2 | String comparada com `==` | java:S4973 | `CatalogoService.buscar` |
| 3 | `else if` com condição repetida | java:S1862 | `CatalogoService.disponibilidade` |
| 4 | Hash MD5 | java:S4790 | `CatalogoService.versaoCatalogo` |
| 5 | `catch (Exception e)` genérico | java:S2221 | `CatalogoController.buscar` |
| 6 | Cobertura baixa (controller sem testes) | – | `src/test` |

O problema 2 é um bug real: a busca por impressora falha quando o texto chega da requisição,
mas os testes da versão com defeitos não pegam isso. O Sonar pega.

---

## Passo 0 – Pré-requisitos

- Docker Desktop ligado.
- **SonarQube da POC anterior no ar.** Na pasta da POC em Python, rode `docker compose up -d`
  e confira em http://localhost:9000.

## Passo 1 – Criar o projeto no SonarQube

1. **Create a local project**:
   - Display name: `POC Catalogo Impressoras`
   - Project key: `POC-Catalogo-Impressoras` (igual ao `pom.xml`)
   - Main branch: `main`
2. New code: **Follows the instance's default** → **Create project**.
3. Analysis method: **Locally** → **Generate** o token (`squ_...`).
4. Crie o arquivo `.env` nesta pasta (use o `.env.example` como modelo):
   ```
   SONAR_TOKEN=squ_seu_token
   ```
5. Em **Policies → Quality Gates → POC TESTE QualityGate**, na seção **Projects**,
   marque `POC Catalogo Impressoras`.

## Passo 2 (opcional) – Ver o "site" funcionando

```powershell
docker compose up api
```
Quando aparecer `Started CatalogoApplication`, abra http://localhost:8080/api/acessorios.
Para parar: Ctrl+C.

## Passo 3 – Rodar e ver o gate REPROVAR 🔴

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\run_poc.ps1
```
A primeira execução baixa as dependências do Maven e pode levar alguns minutos.
Esperado: testes passando, cobertura baixa, issues encontradas e **QUALITY GATE REPROVADO**.

## Passo 4 – Corrigir e ver o gate APROVAR 🟢

```powershell
# backup da versão com problemas, para repetir a demo depois
Copy-Item src original_src -Recurse

Copy-Item solucao\src\main\java\br\com\virtus\catalogo\*.java src\main\java\br\com\virtus\catalogo\ -Force
Copy-Item solucao\src\test\java\br\com\virtus\catalogo\*.java src\test\java\br\com\virtus\catalogo\ -Force
.\run_poc.ps1
```
Esperado: mais testes (serviço + API), cobertura alta e **QUALITY GATE APROVADO**.

Para voltar à versão com problemas:
```powershell
Remove-Item src -Recurse -Force
Copy-Item original_src src -Recurse
```

---

## Problemas comuns

| Sintoma | Causa provável / solução |
|---|---|
| "SonarQube não está pronto" | Suba o SonarQube na pasta da POC anterior (`docker compose up -d`) |
| `Not authorized` / 401 | Token errado ou de outro projeto. Gere um token para `POC-Catalogo-Impressoras` |
| `manifest unknown` para a imagem `maven:3.9-eclipse-temurin-25` | Troque a imagem no `docker-compose.yml` por `maven:3-eclipse-temurin-25` |
| Download do Maven falha por SSL/proxy | Rede corporativa: configure o proxy no Docker Desktop ou rode fora da VPN |
| Gate reprova na versão corrigida | Abra **Issues** no SonarQube e veja qual regra apareceu (a versão do Sonar pode ter regras novas) |

## Próximas melhorias planejadas

- Levar para o GitHub Actions e validar o gate em pull request.
- Resumo do resultado do gate na página da pipeline.
- Publicar o relatório dos testes como artefato.
