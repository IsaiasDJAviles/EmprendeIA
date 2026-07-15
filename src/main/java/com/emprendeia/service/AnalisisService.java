package com.emprendeia.service;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.emprendeia.exception.AnalisisInvalidoException;
import com.emprendeia.ia.LlmClient;
import com.emprendeia.ia.PromptBuilder;
import com.emprendeia.model.AnalisisGenerado;
import com.emprendeia.model.Estatus;
import com.emprendeia.model.Formulario;
import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.TipoAnalisis;
import com.emprendeia.model.Usuario;
import com.emprendeia.repository.AnalisisGeneradoRepository;
import com.emprendeia.repository.EstatusRepository;

/**
 * Orquesta la generación y edición de los 5 módulos de análisis (RF-07 a RF-11, RF-14):
 * arma el prompt vía {@link PromptBuilder}, invoca el {@link LlmClient} activo, valida que
 * el JSON devuelto tenga los campos esperados (RNF-05) y persiste en {@link AnalisisGenerado}.
 * <p>
 * {@link #generar} deliberadamente no es transaccional: envolver en una transacción la
 * llamada HTTP al LLM (hasta 30s, ver {@code GeminiClient}) mantendría una conexión de BD
 * ociosa todo ese tiempo sin necesidad. La escritura final ocurre en una sola llamada a
 * {@link AnalisisGeneradoRepository#save}, transaccional por sí misma. Si el LLM falla o el
 * JSON es inválido, la excepción sube antes de tocar el repositorio: nada se persiste a
 * medias y ni la idea ni el formulario se ven afectados, así que el usuario puede reintentar
 * sin perder nada (RNF-09).
 */
@Service
public class AnalisisService {

    private static final String ESTATUS_ACTIVO = "ACTIVO";

    private final AnalisisGeneradoRepository analisisGeneradoRepository;
    private final EstatusRepository estatusRepository;
    private final IdeaService ideaService;
    private final FormularioService formularioService;
    private final PromptBuilder promptBuilder;
    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    public AnalisisService(AnalisisGeneradoRepository analisisGeneradoRepository, EstatusRepository estatusRepository,
            IdeaService ideaService, FormularioService formularioService, PromptBuilder promptBuilder,
            LlmClient llmClient, ObjectMapper objectMapper) {
        this.analisisGeneradoRepository = analisisGeneradoRepository;
        this.estatusRepository = estatusRepository;
        this.ideaService = ideaService;
        this.formularioService = formularioService;
        this.promptBuilder = promptBuilder;
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
    }

    public Optional<AnalisisGenerado> obtenerActual(Long ideaId, TipoAnalisis tipo, Usuario usuario) {
        IdeaNegocio idea = ideaService.obtenerPropia(ideaId, usuario);
        return analisisGeneradoRepository.findByIdeaNegocioAndTipoAnalisis(idea, tipo);
    }

    public AnalisisGenerado generar(Long ideaId, TipoAnalisis tipo, Usuario usuario) {
        IdeaNegocio idea = ideaService.obtenerPropia(ideaId, usuario);
        Formulario formulario = formularioService.obtenerPorIdea(ideaId, usuario).orElse(null);

        String prompt = promptBuilder.construir(tipo, idea, formulario);
        String textoLlm = llmClient.generarAnalisis(prompt);

        JsonNode nodo;
        try {
            nodo = objectMapper.readTree(textoLlm);
        } catch (JacksonException ex) {
            throw new AnalisisInvalidoException("El proveedor de IA no devolvió un JSON válido.", ex);
        }

        return validarYGuardar(tipo, idea, prompt, nodo);
    }

    @Transactional
    public AnalisisGenerado guardarEdicion(Long ideaId, TipoAnalisis tipo, Object datosEditados, Usuario usuario) {
        IdeaNegocio idea = ideaService.obtenerPropia(ideaId, usuario);
        JsonNode nodo = objectMapper.valueToTree(datosEditados);

        String promptPrevio = analisisGeneradoRepository.findByIdeaNegocioAndTipoAnalisis(idea, tipo)
                .map(AnalisisGenerado::getPromptCompleto)
                .orElse(null);

        return validarYGuardar(tipo, idea, promptPrevio, nodo);
    }

    /**
     * Valida presencia de los campos esperados (RNF-05) y hace upsert por idea+tipo,
     * mismo patrón que {@link FormularioService#guardar}. Punto de convergencia único
     * entre generación por IA y edición manual: ambos flujos terminan en el mismo JSON
     * validado y en la misma regla de persistencia.
     */
    private AnalisisGenerado validarYGuardar(TipoAnalisis tipo, IdeaNegocio idea, String promptCompleto,
            JsonNode nodo) {
        for (String campo : promptBuilder.camposEsperados(tipo)) {
            JsonNode valor = nodo.get(campo);
            boolean vacio = valor == null || valor.isNull()
                    || (valor.isTextual() && valor.asText().isBlank())
                    || (valor.isArray() && valor.isEmpty());
            if (vacio) {
                throw new AnalisisInvalidoException("La respuesta no incluye el campo obligatorio \"" + campo + "\".");
            }
        }

        String contenido;
        try {
            contenido = objectMapper.writeValueAsString(nodo);
        } catch (JacksonException ex) {
            throw new AnalisisInvalidoException("No fue posible serializar el análisis.", ex);
        }

        AnalisisGenerado analisis = analisisGeneradoRepository.findByIdeaNegocioAndTipoAnalisis(idea, tipo)
                .orElseGet(() -> new AnalisisGenerado(tipo, null, null, null, estatusActivo(), idea));

        analisis.setContenido(contenido);
        analisis.setPromptCompleto(promptCompleto);
        analisis.setFechaGeneracion(OffsetDateTime.now());

        return analisisGeneradoRepository.save(analisis);
    }

    private Estatus estatusActivo() {
        return estatusRepository.findByNombreEstatus(ESTATUS_ACTIVO)
                .orElseThrow(() -> new IllegalStateException(
                        "El catálogo estatus no tiene una fila con nombre_estatus = '" + ESTATUS_ACTIVO + "'."));
    }
}
