package com.emprendeia.ia.gemini;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.emprendeia.ia.LlmClient;
import com.emprendeia.ia.LlmException;

/**
 * Implementación de {@link LlmClient} sobre la API de Google Gemini (generateContent).
 * Activa por defecto; se desactiva fijando {@code llm.provider} a otro valor (p. ej. "groq"
 * una vez exista {@code GroqClient} en su propio subpaquete).
 */
@Service
@ConditionalOnProperty(prefix = "llm", name = "provider", havingValue = "gemini", matchIfMissing = true)
public class GeminiClient implements LlmClient {

    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final GeminiProperties properties;
    private final RestClient restClient;

    public GeminiClient(GeminiProperties properties) {
        this.properties = properties;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) TIMEOUT.toMillis());
        requestFactory.setReadTimeout((int) TIMEOUT.toMillis());

        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public String generarAnalisis(String prompt) {
        GeminiRequest cuerpo = new GeminiRequest(
                List.of(new Content(List.of(new Part(prompt)))),
                new GenerationConfig("application/json"));

        GeminiResponse respuesta;
        try {
            respuesta = restClient.post()
                    .uri("/models/{model}:generateContent?key={apiKey}", properties.getModel(), properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cuerpo)
                    .retrieve()
                    .body(GeminiResponse.class);
        } catch (RestClientResponseException ex) {
            throw new LlmException(
                    "Gemini respondió con error HTTP " + ex.getStatusCode().value() + ": " + ex.getResponseBodyAsString(),
                    ex);
        } catch (ResourceAccessException ex) {
            throw new LlmException("No se pudo contactar a Gemini (timeout o error de red).", ex);
        }

        String texto = extraerTexto(respuesta);
        if (texto == null || texto.isBlank()) {
            throw new LlmException("Gemini devolvió una respuesta sin contenido utilizable.");
        }
        return texto;
    }

    private static String extraerTexto(GeminiResponse respuesta) {
        if (respuesta == null || respuesta.candidates() == null || respuesta.candidates().isEmpty()) {
            return null;
        }
        Content content = respuesta.candidates().get(0).content();
        if (content == null || content.parts() == null || content.parts().isEmpty()) {
            return null;
        }
        return content.parts().get(0).text();
    }

    private record GeminiRequest(List<Content> contents, GenerationConfig generationConfig) {
    }

    private record GenerationConfig(String responseMimeType) {
    }

    private record GeminiResponse(List<Candidate> candidates) {
    }

    private record Candidate(Content content) {
    }

    private record Content(List<Part> parts) {
    }

    private record Part(String text) {
    }
}
