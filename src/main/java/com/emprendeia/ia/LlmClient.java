package com.emprendeia.ia;

/**
 * Puerto hacia un proveedor de LLM. La implementación activa se selecciona vía
 * {@code llm.provider} (ver implementaciones en subpaquetes, p. ej. {@code com.emprendeia.ia.gemini}),
 * sin que el resto de la aplicación conozca el proveedor concreto.
 */
public interface LlmClient {

    /**
     * @param prompt prompt completo, ya parametrizado (ver {@link PromptBuilder})
     * @return el texto generado por el modelo
     * @throws LlmException si la llamada falla o la respuesta no es utilizable
     */
    String generarAnalisis(String prompt);
}
