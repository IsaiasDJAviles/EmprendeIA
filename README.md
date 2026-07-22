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

## Arquitectura

Documentación
├──EmprendeIA_SRS.pdf
├──Manual_Tecnico_EmprendeIA.pdf
├──Manual_Usuario_EmprendeIA.pdf
com.emprendeia
├── EmprendeiaApplication        // arranque Spring Boot
├── config/                      // SecurityConfig
├── controller/                  // 5 controladores MVC
├── dto/                         // formularios y payloads validados
├── model/                       // entidades JPA (15 tablas)
├── repository/                  // repositorios Spring Data
├── service/                     // lógica de negocio + transacciones
├── security/                    // UserDetailsService + Principal
├── ia/                          // LlmClient, PromptBuilder, LlmException
│   └── gemini/                  // GeminiClient, GeminiProperties
├── report/                      // PdfReportBuilder, DatosReporte
└── exception/                   // excepciones de dominio

src/main/resources
├── application.properties       // configuración externalizada
├── static/css, static/js        // estilos y scripts del cliente
└── templates/                   // vistas Thymeleaf
