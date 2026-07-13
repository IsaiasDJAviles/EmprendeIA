package com.emprendeia.ia;

import org.springframework.stereotype.Component;

import com.emprendeia.model.Formulario;
import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.TipoAnalisis;

/**
 * Arma los 5 prompts parametrizados (uno por {@link TipoAnalisis}) a partir de una
 * {@link IdeaNegocio} y, cuando aplica, su {@link Formulario}.
 * <p>
 * Los textos base son los del documento guía del proyecto. {@link TipoAnalisis#MERCADO}
 * no tiene, en ese documento, un prompt de "análisis de mercado": el 5to prompt del
 * documento es de "interpretación financiera" (usa {@link Formulario}, no la idea). Por
 * decisión del equipo, ese prompt ocupa el slot MERCADO del enum existente en vez de
 * agregar un valor nuevo al CHECK de {@code analisis_generado}.
 */
@Component
public class PromptBuilder {

    private static final String PLACEHOLDER_NEGOCIO = "{informacion_del_negocio}";
    private static final String PLACEHOLDER_FINANCIERO = "{datos_financieros}";

    private static final String BASE_DIAGNOSTICO = """
            Actúa como consultor empresarial. Analiza la siguiente idea de negocio y genera un diagnóstico inicial claro, realista y estructurado.

            Incluye:
            1. Descripción general de la idea.
            2. Problema que resuelve.
            3. Cliente objetivo.
            4. Nivel de claridad de la propuesta.
            5. Principales riesgos.
            6. Recomendaciones de mejora.

            Información del usuario:
            {informacion_del_negocio}""";

    private static final String BASE_CANVAS = """
            Actúa como especialista en modelos de negocio. Con base en la siguiente información, genera un modelo Canvas estructurado.

            Incluye:
            1. Segmentos de clientes.
            2. Propuesta de valor.
            3. Canales.
            4. Relación con clientes.
            5. Fuentes de ingresos.
            6. Recursos clave.
            7. Actividades clave.
            8. Socios clave.
            9. Estructura de costos.

            Información del negocio:
            {informacion_del_negocio}""";

    private static final String BASE_FODA = """
            Actúa como analista empresarial. Realiza un análisis FODA de la siguiente idea de negocio.

            Incluye:
            1. Fortalezas.
            2. Oportunidades.
            3. Debilidades.
            4. Amenazas.
            5. Recomendaciones estratégicas.

            Información del negocio:
            {informacion_del_negocio}""";

    private static final String BASE_MARKETING = """
            Actúa como especialista en marketing para pequeñas empresas. Diseña un plan de marketing inicial para la siguiente idea de negocio.

            Incluye:
            1. Público objetivo.
            2. Mensaje principal.
            3. Canales de promoción.
            4. Estrategias digitales.
            5. Promociones iniciales.
            6. Calendario básico de acciones.
            7. Indicadores para medir resultados.

            Información del negocio:
            {informacion_del_negocio}""";

    private static final String BASE_FINANCIERO = """
            Actúa como asesor financiero para emprendedores. Interpreta los siguientes datos financieros de manera clara y sencilla.

            Incluye:
            1. Ingresos estimados.
            2. Costos principales.
            3. Utilidad aproximada.
            4. Punto de equilibrio.
            5. Riesgos financieros.
            6. Recomendaciones para mejorar la viabilidad.

            Datos financieros:
            {datos_financieros}""";

    private static final String INSTRUCCION_JSON = """


            Responde ÚNICAMENTE con un JSON válido (sin texto adicional, sin bloques de código markdown), \
            con exactamente esta estructura de campos:
            %s""";

    private static final String SHAPE_DIAGNOSTICO = """
            {
              "descripcionGeneral": "string",
              "problemaQueResuelve": "string",
              "clienteObjetivo": "string",
              "nivelClaridadPropuesta": "string",
              "principalesRiesgos": ["string"],
              "recomendacionesMejora": ["string"]
            }""";

    private static final String SHAPE_CANVAS = """
            {
              "segmentosClientes": ["string"],
              "propuestaValor": "string",
              "canales": ["string"],
              "relacionClientes": "string",
              "fuentesIngresos": ["string"],
              "recursosClave": ["string"],
              "actividadesClave": ["string"],
              "sociosClave": ["string"],
              "estructuraCostos": ["string"]
            }""";

    private static final String SHAPE_FODA = """
            {
              "fortalezas": ["string"],
              "oportunidades": ["string"],
              "debilidades": ["string"],
              "amenazas": ["string"],
              "recomendacionesEstrategicas": ["string"]
            }""";

    private static final String SHAPE_MARKETING = """
            {
              "publicoObjetivo": "string",
              "mensajePrincipal": "string",
              "canalesPromocion": ["string"],
              "estrategiasDigitales": ["string"],
              "promocionesIniciales": ["string"],
              "calendarioBasico": ["string"],
              "indicadoresResultados": ["string"]
            }""";

    private static final String SHAPE_FINANCIERO = """
            {
              "ingresosEstimados": "string",
              "costosPrincipales": "string",
              "utilidadAproximada": "string",
              "puntoEquilibrio": "string",
              "riesgosFinancieros": ["string"],
              "recomendacionesViabilidad": ["string"]
            }""";

    public String construir(TipoAnalisis tipo, IdeaNegocio idea, Formulario formulario) {
        return switch (tipo) {
            case DIAGNOSTICO -> BASE_DIAGNOSTICO.replace(PLACEHOLDER_NEGOCIO, informacionNegocio(idea))
                    + INSTRUCCION_JSON.formatted(SHAPE_DIAGNOSTICO);
            case CANVAS -> BASE_CANVAS.replace(PLACEHOLDER_NEGOCIO, informacionNegocio(idea))
                    + INSTRUCCION_JSON.formatted(SHAPE_CANVAS);
            case FODA -> BASE_FODA.replace(PLACEHOLDER_NEGOCIO, informacionNegocio(idea))
                    + INSTRUCCION_JSON.formatted(SHAPE_FODA);
            case MARKETING -> BASE_MARKETING.replace(PLACEHOLDER_NEGOCIO, informacionNegocio(idea))
                    + INSTRUCCION_JSON.formatted(SHAPE_MARKETING);
            case MERCADO -> BASE_FINANCIERO.replace(PLACEHOLDER_FINANCIERO, datosFinancieros(formulario))
                    + INSTRUCCION_JSON.formatted(SHAPE_FINANCIERO);
        };
    }

    private static String informacionNegocio(IdeaNegocio idea) {
        return "Nombre del negocio: " + idea.getNombreNegocio()
                + "\nDescripción: " + valorODefecto(idea.getDescripcion())
                + "\nProblema que resuelve: " + valorODefecto(idea.getProblema())
                + "\nSector o mercado: " + valorODefecto(idea.getSectorMercado())
                + "\nCliente objetivo: " + valorODefecto(idea.getClienteObjetivo())
                + "\nTipo de oferta: " + valorODefecto(idea.getTipoOferta())
                + "\nNivel de avance: " + valorODefecto(idea.getNivelAvance());
    }

    private static String datosFinancieros(Formulario formulario) {
        if (formulario == null) {
            return "El usuario aún no ha completado el formulario financiero de esta idea.";
        }
        return "Inversión inicial: " + formulario.getInversionInicial()
                + "\nCostos fijos: " + formulario.getCostosFijos()
                + "\nCostos variables: " + formulario.getCostosVariables()
                + "\nPrecio de venta: " + formulario.getPrecioVenta()
                + "\nUnidades estimadas: " + formulario.getUnidadesEstimadas()
                + "\nDestino de la inversión: " + valorODefecto(formulario.getDestinoInversion());
    }

    private static String valorODefecto(String valor) {
        return (valor == null || valor.isBlank()) ? "No especificado" : valor;
    }
}
