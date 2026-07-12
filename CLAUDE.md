# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

EmprendeIA: a Spring Boot web app that acts as an AI assistant for generating and validating business
plans ("planes de negocio") via LLMs, for the Programa Delfín / BUAP. The Java application currently
in this repo (`emprendeia/`) is a freshly generated Spring Boot skeleton — `EmprendeiaApplication.java`
has no custom code yet beyond the default `@SpringBootApplication` bootstrap. The substantive design
artifact so far is the PostgreSQL schema in `../db/`, which encodes the data model the application will
be built against.

## Commands

Run all commands from `emprendeia/` (the Maven project root).

- Build: `./mvnw compile` (Windows: `mvnw.cmd compile`)
- Run the app: `./mvnw spring-boot:run`
- Run all tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=EmprendeiaApplicationTests`
- Run a single test method: `./mvnw test -Dtest=EmprendeiaApplicationTests#contextLoads`
- Package: `./mvnw package`

Requires Java 24 (`java.version` in `pom.xml`) and a PostgreSQL instance for anything beyond the
context-load test, once a datasource is configured.

## Architecture

### Module layout
- `emprendeia/` — the Spring Boot application (Maven, `com.emprendeia` base package). Uses Spring Web
  MVC, Spring Data JPA, Spring Security, Thymeleaf (+ `thymeleaf-extras-springsecurity6`), and
  Bean Validation. Driver: `org.postgresql:postgresql` (runtime scope only — no embedded/test DB
  dependency is declared, so tests that touch persistence need a real Postgres instance).
- `db/` — hand-authored SQL, external to the Maven build, treated as the source of truth for the
  schema (no Flyway/Liquibase wired in yet):
  - `EmprendeIA_BD.sql` — full DDL (tables, constraints, indexes).
  - `emprendeia_roles.sql` — PostgreSQL role/privilege setup, run after the DDL.

### Data model (from `db/EmprendeIA_BD.sql`)
Central entity is `idea_negocio` (a business idea), owned by a `usuario`, with:
- `formulario` — 1:1 questionnaire answers (investment, costs, price, units) feeding financial calcs.
- `analisis_generado` — 1:N LLM outputs, one row per module type, gated by
  `CHECK tipo_analisis IN ('DIAGNOSTICO','CANVAS','FODA','MERCADO','MARKETING')`. `contenido` stores
  LLM output as JSON serialized to text; schema validation happens in the service layer (Jackson), not
  the DB.
- `modulo_financiero` — 1:1 computed financial outputs (revenue, costs, break-even, profit).
- `reporte` — 1:1 final report, with its own status catalog (`estado_reporte`: borrador/consolidado/
  descargado) separate from soft-delete status.
- `socio` — 1:N business partners.
- `idea_negocio_giro` / `idea_negocio_tipo_capital` — M:N junctions to the `giro` (business sector) and
  `tipo_capital` (capital type) catalogs.
- Geographic catalog: `pais` → `estado` (1:N).

Two distinct "status" concepts exist in parallel — do not conflate them:
- `id_estatus` (FK to the `estatus` catalog) — transversal **soft delete** (active/inactive/deleted),
  present on every transactional table.
- `id_estado_reporte` on `reporte` — **business status** of the report itself, unrelated to soft delete.
- `id_estado` on `idea_negocio` — geographic state/province (FK to `estado`), also unrelated to the
  other two despite the similar name.

Deletion rules baked into the FKs: `ON DELETE CASCADE` from every child table down to `idea_negocio`;
`ON DELETE RESTRICT` from `idea_negocio` to `usuario`, and from any table to a catalog. In practice,
"deleting" a business idea should go through the soft-delete column (`id_estatus`), not a physical
`DELETE`, in line with what `emprendeia_app`'s grants (below) actually allow.

Passwords are stored as BCrypt hashes (`usuario.contrasena`, `VARCHAR(60)`) — hashing happens in the
application layer via Spring Security's `BCryptPasswordEncoder`, never in SQL.

### Database roles (from `db/emprendeia_roles.sql`)
Three-role model, intended to be layered on top of the schema in a fixed order (DDL → roles script):
- `emprendeia_admin` — schema owner; runs DDL/migrations. Not the app's runtime credential.
- `emprendeia_app` — the credential Spring Boot uses in production. Scoped DML only: SELECT/INSERT/
  UPDATE everywhere, plus DELETE restricted to the `idea_negocio` tree (never on `usuario` or catalogs).
- `emprendeia_readonly` — SELECT-only, for reporting/dashboards/backups.

Implication for `application.properties`: production config expects
`spring.jpa.hibernate.ddl-auto=validate` (Hibernate checks entities against the existing schema and
fails fast rather than trying to modify it — `emprendeia_app` has no DDL privilege anyway), and
datasource credentials sourced from environment variables (`DB_APP_USERNAME` / `DB_APP_PASSWORD`), not
hardcoded in `application.properties` or committed to the repo.

### Current state of `application.properties`
Only `spring.application.name=emprendeia` is set. Datasource, JPA, and security configuration have not
been added yet — when adding them, follow the role/credential model above rather than connecting as a
superuser or the DDL-owning role.
