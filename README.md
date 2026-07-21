# EmprendeIA

Asistente de IA para generar y validar planes de negocio: diagnóstico, modelo Canvas, FODA,
módulo financiero, interpretación de mercado, plan de marketing y reporte ejecutivo en PDF,
generados a partir de la idea de negocio del usuario. Proyecto para el Programa Delfín / BUAP.

**Demo:** https://emprendeia.onrender.com

## Stack

- Java 24 · Spring Boot 4.1 (Web MVC, Data JPA, Security, Thymeleaf, Validation)
- PostgreSQL
- Gemini API (generación de contenido)
- OpenPDF (reporte ejecutivo)
- Docker (despliegue)

## Requisitos

- Java 24
- PostgreSQL
- Una API key de Gemini ([Google AI Studio](https://aistudio.google.com/apikey))

## Correr en local

1. Crea la base de datos y corre, en orden, `db/EmprendeIA_BD.sql` y `db/emprendeia_roles.sql`.
2. Exporta estas variables de entorno:

   | Variable | Descripción |
   |---|---|
   | `DB_HOST` / `DB_PORT` / `DB_NAME` | Conexión a Postgres (default `localhost:5432/emprendeia`) |
   | `DB_APP_USERNAME` / `DB_APP_PASSWORD` | Credenciales del rol `emprendeia_app` |
   | `GEMINI_API_KEY` | API key de Gemini |

3. `./mvnw spring-boot:run`

## Despliegue

Incluye `Dockerfile` (build multi-etapa) listo para Render u otro proveedor que soporte Docker.
Mismas variables de entorno que en local, más `DB_SSLMODE=require`.
