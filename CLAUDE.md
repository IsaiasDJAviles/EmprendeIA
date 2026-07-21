# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

EmprendeIA: a Spring Boot web app that acts as an AI assistant for generating and validating business
plans ("planes de negocio") via LLMs, for the Programa Delfín / BUAP. The application has moved past
skeleton stage: authentication, the core domain model, the LLM integration, and the financial module are
implemented and wired end to end. The report generator (RF-15/RF-16, PDF/DOCX export) is the main
remaining gap — see "What is not built yet" below before assuming a feature exists.

## Commands

Run all commands from `emprendeia/` (the Maven project root).

- Build: `./mvnw compile` (Windows: `mvnw.cmd compile`)
- Run the app: `./mvnw spring-boot:run`
- Run all tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=EmprendeiaApplicationTests`
- Run a single test method: `./mvnw test -Dtest=EmprendeiaApplicationTests#contextLoads`
- Package: `./mvnw package`

Requires Java 24 (`java.version` in `pom.xml`) and a running PostgreSQL instance (port 5433 in local dev,
per the DBA setup in `db/`) — the datasource is no longer optional, `application.properties` now points
at it unconditionally (see below).

## Architecture

### Module layout
- `emprendeia/` — the Spring Boot application (Maven, `com.emprendeia` base package, Spring Boot
  4.1.0). Dependencies actually declared in `pom.xml`: `spring-boot-starter-data-jpa`,
  `spring-boot-starter-security`, `spring-boot-starter-thymeleaf` (+
  `thymeleaf-extras-springsecurity6` for `sec:authorize` in templates), `spring-boot-starter-validation`,
  `spring-boot-starter-webmvc`, `spring-boot-starter-json`, and `org.postgresql:postgresql` (runtime
  scope). Test-scoped starters mirror each of these
  (`spring-boot-starter-data-jpa-test`, `-security-test`, `-thymeleaf-test`, `-validation-test`,
  `-webmvc-test`) — no embedded/in-memory DB dependency is declared, so persistence tests need a real
  Postgres instance.
- `db/` — hand-authored SQL, external to the Maven build, source of truth for the schema (no
  Flyway/Liquibase wired in):
  - `EmprendeIA_BD.sql` — full DDL (tables, constraints, indexes).
  - `emprendeia_roles.sql` — PostgreSQL role/privilege setup, run after the DDL.

### Package layout (`src/main/java/com/emprendeia/`)
- `model/` — JPA entities, one class per table (`IdeaNegocio`, `Usuario`, `Formulario`,
  `AnalisisGenerado`, `ModuloFinanciero`, `Socio`, catalog entities, join entities for the two M:N
  relationships, etc.). `GenerationType.IDENTITY` on every `@Id` (matches `SERIAL` PKs in the DDL).
  Javadoc on fields calls out the three non-conflatable "status" concepts (see below) at the point of
  declaration — read it before adding a new status-like column.
- `repository/` — Spring Data JPA interfaces, one per entity. Query methods are declarative
  (`findByUsuario`, `findByIdeaNegocioAndTipoAnalisis`, etc.); `UsuarioRepository.findByCorreo` is the
  one exception with a hand-written `@Query` (`JOIN FETCH u.estatus`) — required because
  `UsuarioPrincipal.isEnabled()` reads `usuario.getEstatus()` after the Hibernate session that loaded it
  has closed, so it cannot be a lazy proxy.
- `security/` — `UsuarioPrincipal` (implements `UserDetails`, wraps `Usuario`, single hardcoded
  `ROLE_USER` authority, `isEnabled()` checks `estatus.nombreEstatus == "ACTIVO"`) and
  `UsuarioDetailsService` (implements `UserDetailsService`, delegates to
  `UsuarioRepository.findByCorreo`).
- `config/` — `SecurityConfig`: form login (`/login`, params `correo`/`contrasena`, success redirect to
  `/dashboard`), public routes (`/`, `/registro`, `/login`, `/error`, static assets), everything else
  requires authentication. `BCryptPasswordEncoder` bean; `DaoAuthenticationProvider` wired to
  `UsuarioDetailsService`.
- `controller/` — Spring MVC `@Controller` classes returning Thymeleaf view names (not `@RestController`
  — this is a server-rendered app, not a JSON API). Implemented so far: `AuthController` (`/`,
  `/login`, `/registro`), `FinancieroController` (`/ideas/{id}/finanzas`, GET + POST `/calcular`).
  Others referenced in the SRS traceability matrix (`IdeaController`, `AnalisisController`,
  `ReporteController`) follow the same pattern once built — check `controller/` directly rather than
  assuming from the SRS, since the SRS is a requirements document, not a build log.
- `service/` — business logic, orchestrates repositories and the LLM client:
  - `AnalisisService` — generates/edits the five AI analysis modules (RF-07 to RF-11, RF-14).
    `generar()` is deliberately **not** `@Transactional`: wrapping the LLM HTTP call (up to 30s, see
    `GeminiClient`) in a transaction would hold a DB connection idle for no reason. It builds the prompt
    via `PromptBuilder`, calls the active `LlmClient`, parses the response into a `JsonNode`, validates
    required fields against `PromptBuilder.camposEsperados(tipo)`, and only then persists via a single
    transactional `save`. If the LLM call fails or returns invalid JSON, the exception propagates before
    anything is written — no partial state, matching RNF-09 (safe retry). For
    `TipoAnalisis.MERCADO` specifically, the prompt is built from the already-computed
    `ModuloFinanciero` (not raw form data) — if that doesn't exist yet, it fails fast with
    `AnalisisInvalidoException` before ever calling the LLM. `guardarEdicion()` and `generar()` converge
    on the same `validarYGuardar()` private method — manual edits and LLM output go through identical
    validation and persistence.
  - `FinancieroService` — RF-13, pure Java, no LLM. `calcularResultado(Formulario)` derives revenue,
    total/variable costs, profit, contribution margin (unit and percentage), and break-even point from
    `Formulario` values, using `BigDecimal` throughout (`RoundingMode.HALF_UP`, monetary values scaled to
    2 decimals). Throws `FinancieroInvalidoException` if `unidadesEstimadas <= 0` or if
    `precioVenta` doesn't cover unit variable cost (non-positive contribution margin). Contribution
    margin is **not** persisted — `modulo_financiero` has no column for it (see DDL); it's recomputed
    on every request from the live `Formulario`. `calcular()` is `@Transactional`, upserts
    `ModuloFinanciero` by idea (find-or-create pattern also used elsewhere), and persists only the
    subset of `ResultadoFinanciero` that has a column.
  - `ResultadoFinanciero` — a `record` carrying the full financial calculation result, including the
    non-persisted contribution margin fields.
  - `IdeaService`, `FormularioService`, `UsuarioService` — referenced by the above (ownership checks via
    `obtenerPropia(id, usuario)`, upsert patterns for `Formulario`/`Usuario`). Read directly before
    extending; don't assume method signatures from this file.
- `ia/` — the LLM integration boundary:
  - `LlmClient` — the port. One method, `generarAnalisis(String prompt) -> String`, throwing
    `LlmException` on failure. Nothing outside this package should know which provider is active.
  - `PromptBuilder` — builds the parametrized prompt per `TipoAnalisis`, and exposes
    `camposEsperados(tipo)` (the field names `AnalisisService` validates against after parsing the LLM
    response).
  - `ia/gemini/` — `GeminiClient` (implements `LlmClient`, `@ConditionalOnProperty(prefix = "llm", name
    = "provider", havingValue = "gemini", matchIfMissing = true)` — active by default, calls
    `generateContent` over `RestClient` with a 30s connect/read timeout, requests
    `application/json` response MIME type). `GeminiProperties` — `@ConfigurationProperties(prefix =
    "llm.gemini")`, `@Validated` with `@NotBlank` on `apiKey`/`model`/`baseUrl`, so a missing API key
    fails fast at startup rather than on first request. A `GroqClient` under a sibling `ia/groq/`
    package, activated by flipping `llm.provider=groq`, is the designed (not yet built) fallback path —
    do not add Groq-specific branching anywhere outside a new `ia/groq/` package.
- `dto/` — form-backing objects for Thymeleaf/`@ModelAttribute` binding (e.g. `RegistroForm`), validated
  with Bean Validation annotations.
- `exception/` — domain exceptions (`CorreoYaRegistradoException`, `IdeaNoEncontradaException`,
  `FinancieroInvalidoException`, `AnalisisInvalidoException`, `LlmException`). Controllers catch these
  and redirect with an error query param rather than letting them surface as 500s — follow that pattern
  for new controllers rather than introducing a global `@ControllerAdvice` unless asked to.

### What is not built yet
- **Report generation (RF-15/RF-16)**: no `ReporteController`, `ReporteService`, or PDF/DOCX generation
  code exists in the codebase as of this writing, despite `reporte` having a full table and status
  catalog in the DB schema. This is the last block of the eight-block sequence
  (Entities → Repositories → Security → CRUD Services → LLM Client → Analysis Services → Financial
  Module → **Report Generator**) and the current priority.
- **`IdeaController` / `AnalisisController`**: named in the SRS traceability matrix but not yet found
  under `controller/`. `IdeaService`/`AnalisisService` exist at the service layer, so the controller
  layer for idea CRUD and for triggering the five AI modules individually (outside the financiero-mercado
  coupling already wired in `FinancieroController`) is likely the next gap after reports. Verify with
  `ls src/main/java/com/emprendeia/controller/` rather than trusting this note as the codebase evolves.
- **Groq fallback**: `LlmClient` is abstracted for it and `GeminiClient`'s `@ConditionalOnProperty`
  leaves the door open, but no `ia/groq/` package exists yet.

Before starting work in any of these areas, list the relevant package directly — this section describes
a snapshot, not a guarantee.

### Data model (from `db/EmprendeIA_BD.sql`)
Central entity is `idea_negocio` (a business idea), owned by a `usuario`, with:
- `formulario` — 1:1 questionnaire answers (investment, costs, price, units) feeding financial calcs.
- `analisis_generado` — 1:N LLM outputs, one row per module type, gated by
  `CHECK tipo_analisis IN ('DIAGNOSTICO','CANVAS','FODA','MERCADO','MARKETING')`. `contenido` stores
  LLM output as JSON serialized to text; schema validation happens in the service layer
  (`AnalisisService` + Jackson `ObjectMapper`), not the DB.
- `modulo_financiero` — 1:1 computed financial outputs (revenue, costs, break-even, profit). No column
  for contribution margin — see `FinancieroService` above.
- `reporte` — 1:1 final report, with its own status catalog (`estado_reporte`: borrador/consolidado/
  descargado) separate from soft-delete status. Table exists; no application code reads/writes it yet.
- `socio` — 1:N business partners.
- `idea_negocio_giro` / `idea_negocio_tipo_capital` — M:N junctions to the `giro` (business sector) and
  `tipo_capital` (capital type) catalogs.
- Geographic catalog: `pais` → `estado` (1:N).

Two distinct "status" concepts exist in parallel — do not conflate them:
- `id_estatus` (FK to the `estatus` catalog) — transversal **soft delete** (active/inactive/deleted),
  present on every transactional table. `EstatusRepository.findByNombreEstatus("ACTIVO")` is the standard
  lookup used across services when creating a new row (see `FinancieroService.estatusActivo()`,
  `AnalisisService.estatusActivo()`).
- `id_estado_reporte` on `reporte` — **business status** of the report itself, unrelated to soft delete.
- `id_estado` on `idea_negocio` — geographic state/province (FK to `estado`), also unrelated to the
  other two despite the similar name.

Deletion rules baked into the FKs: `ON DELETE CASCADE` from every child table down to `idea_negocio`;
`ON DELETE RESTRICT` from `idea_negocio` to `usuario`, and from any table to a catalog. In practice,
"deleting" a business idea should go through the soft-delete column (`id_estatus`), not a physical
`DELETE`, in line with what `emprendeia_app`'s grants (below) actually allow.

Passwords are stored as BCrypt hashes (`usuario.contrasena`, `VARCHAR(60)`) — hashing happens in the
application layer via Spring Security's `BCryptPasswordEncoder`, never in SQL. Confirmed wired end to
end: `SecurityConfig` registers the encoder, `AuthController`/`UsuarioService` use it on registration,
`DaoAuthenticationProvider` uses it on login.

### Database roles (from `db/emprendeia_roles.sql`)
Three-role model, layered on top of the schema in a fixed order (DDL → roles script):
- `emprendeia_admin` — schema owner; runs DDL/migrations. Not the app's runtime credential.
- `emprendeia_app` — the credential Spring Boot uses in production. Scoped DML only: SELECT/INSERT/
  UPDATE everywhere, plus DELETE restricted to the `idea_negocio` tree (never on `usuario` or catalogs).
- `emprendeia_readonly` — SELECT-only, for reporting/dashboards/backups.

This is now live in `application.properties`, not just a design intention:
`spring.jpa.hibernate.ddl-auto=validate` (Hibernate checks entities against the existing schema and
fails fast rather than trying to modify it — `emprendeia_app` has no DDL privilege anyway), and
datasource credentials sourced from environment variables (`DB_APP_USERNAME` / `DB_APP_PASSWORD`), never
hardcoded or committed.

### Current state of `application.properties`
No longer just `spring.application.name`. Configured and active:
- `server.servlet.session.timeout=${SESSION_TIMEOUT:30m}` (RF-02).
- `spring.datasource.url` — `jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:emprendeia}?sslmode=${DB_SSLMODE:prefer}`.
  Note the **default port in this URL is 5432**, the PostgreSQL standard — it is overridden to 5433 via
  the `DB_PORT` environment variable in local dev per the memory notes on this project; don't assume the
  property file's default matches the actual local instance.
- `spring.datasource.username` / `password` — from `DB_APP_USERNAME` / `DB_APP_PASSWORD`, no defaults
  (fails to start without them, by design).
- `spring.jpa.hibernate.ddl-auto=validate`.
- `llm.provider=gemini`, `llm.gemini.api-key=${GEMINI_API_KEY}`, `llm.gemini.model=gemini-flash-latest`,
  `llm.gemini.base-url=https://generativelanguage.googleapis.com/v1beta`. The Gemini API key is expected
  as an environment variable, consistent with the credential-handling pattern above.

### Frontend
Thymeleaf templates under `src/main/resources/templates/`, static assets under
`src/main/resources/static/`. `thymeleaf-extras-springsecurity6` is on the classpath, so templates can
use `sec:authorize` / `sec:authentication` directly. A shared stylesheet
(`static/css/styles.css`) defines the visual language for the app itself (distinct from the
sage-green/near-black palette used in the SRS and technical-reference *documents*, which is a separate,
document-only convention — don't conflate the two when asked to style a view).

## Working conventions

- **Ownership checks**: services that operate on a specific `IdeaNegocio` take the authenticated
  `Usuario` and resolve via an `obtenerPropia(id, usuario)`-style method that throws
  `IdeaNoEncontradaException` if the idea doesn't belong to that user or doesn't exist. Controllers catch
  this and redirect to `/dashboard?error` rather than leaking a 404/403 distinction. Follow this pattern
  for any new controller/service pair touching idea-scoped data.
- **Find-or-create / upsert**: for 1:1 child tables (`ModuloFinanciero`, `AnalisisGenerado` by type),
  services use `repository.findByX(...).orElseGet(() -> new Entity(...))` then mutate and `save`, rather
  than separate create/update code paths. Reuse this pattern rather than introducing a new one.
- **`@Transactional` placement**: only wrap actual persistence in a transaction. Calls that go out to the
  LLM (`AnalisisService.generar`) or that call another service which itself makes an outbound call stay
  untransactional at the outer layer; the transactional boundary sits on the final `save` alone. Read
  `AnalisisService`'s javadoc before changing this — it explains the reasoning, not just the rule.
  Compare with `FinancieroService.calcular()`, which *is* `@Transactional` end to end because it involves
  no outbound call.
  