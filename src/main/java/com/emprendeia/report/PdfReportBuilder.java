package com.emprendeia.report;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import com.emprendeia.dto.CanvasForm;
import com.emprendeia.dto.DiagnosticoForm;
import com.emprendeia.dto.FodaForm;
import com.emprendeia.dto.MarketingForm;
import com.emprendeia.dto.MercadoForm;
import com.emprendeia.model.AnalisisGenerado;
import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.ModuloFinanciero;
import com.emprendeia.model.TipoAnalisis;
import com.emprendeia.model.Usuario;

/**
 * Arma el PDF del reporte ejecutivo a partir de {@link DatosReporte} ya recolectado por
 * {@link com.emprendeia.service.ReporteService}. Prioriza contenido correcto y legible sobre
 * diseño elaborado (fuentes base Helvetica, sin TTF embebidas, sin maquetación compleja) — ver
 * justificación en el plan del módulo. Cada sección faltante (análisis no generado, finanzas no
 * calculadas) se renderiza como un aviso de "pendiente" en vez de bloquear el resto del documento.
 */
@Component
public class PdfReportBuilder {

    private static final Font FUENTE_TITULO = new Font(Font.HELVETICA, 22, Font.BOLD);
    private static final Font FUENTE_SUBTITULO_PORTADA = new Font(Font.HELVETICA, 14, Font.NORMAL);
    private static final Font FUENTE_META_PORTADA = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.DARK_GRAY);
    private static final Color COLOR_ACENTO = new Color(0xC0, 0x20, 0x10);
    private static final Font FUENTE_SECCION = new Font(Font.HELVETICA, 16, Font.BOLD, COLOR_ACENTO);
    private static final Font FUENTE_ETIQUETA = new Font(Font.HELVETICA, 11, Font.BOLD);
    private static final Font FUENTE_TEXTO = new Font(Font.HELVETICA, 11, Font.NORMAL);
    private static final Font FUENTE_PENDIENTE = new Font(Font.HELVETICA, 11, Font.ITALIC, Color.GRAY);
    private static final Font FUENTE_TABLA_ENCABEZADO = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
    private static final Font FUENTE_TABLA_CELDA = new Font(Font.HELVETICA, 11, Font.NORMAL);
    private static final Font FUENTE_AVISO = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.DARK_GRAY);

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX"));
    private static final NumberFormat FORMATO_MONEDA = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

    private static final String AVISO_FINANCIERO = "Aviso: los cálculos financieros de este reporte son "
            + "estimaciones generadas a partir de la información proporcionada por el usuario. No constituyen "
            + "asesoría financiera profesional; se recomienda validarlos con un especialista antes de tomar "
            + "decisiones de inversión (RF-13.1).";

    private final ObjectMapper objectMapper;

    public PdfReportBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public byte[] construir(DatosReporte datos) {
        Document document = new Document(PageSize.LETTER, 54, 54, 60, 54);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, salida);
            document.addTitle(datos.idea().getNombreNegocio() + " - Reporte Ejecutivo");
            document.addCreator("EmprendeIA");
            document.open();

            agregarPortada(document, datos);

            document.newPage();
            agregarSeccionTexto(document, "1. Diagnóstico inicial", datos.analisisPorTipo().get(TipoAnalisis.DIAGNOSTICO),
                    DiagnosticoForm.class, this::contenidoDiagnostico);

            document.newPage();
            agregarSeccionTexto(document, "2. Modelo Canvas", datos.analisisPorTipo().get(TipoAnalisis.CANVAS),
                    CanvasForm.class, this::contenidoCanvas);

            document.newPage();
            agregarSeccionTexto(document, "3. Análisis FODA", datos.analisisPorTipo().get(TipoAnalisis.FODA),
                    FodaForm.class, this::contenidoFoda);

            document.newPage();
            agregarSeccionTexto(document, "4. Plan de marketing", datos.analisisPorTipo().get(TipoAnalisis.MARKETING),
                    MarketingForm.class, this::contenidoMarketing);

            document.newPage();
            agregarSeccionFinanciera(document, datos.moduloFinanciero(), datos.analisisPorTipo().get(TipoAnalisis.MERCADO));

            agregarCierre(document);

            document.close();
        } catch (DocumentException ex) {
            throw new IllegalStateException("No fue posible generar el PDF del reporte.", ex);
        }

        return salida.toByteArray();
    }

    private void agregarPortada(Document document, DatosReporte datos) throws DocumentException {
        IdeaNegocio idea = datos.idea();
        Usuario usuario = datos.usuario();

        for (int i = 0; i < 6; i++) {
            document.add(new Paragraph(" "));
        }

        Paragraph titulo = new Paragraph(idea.getNombreNegocio(), FUENTE_TITULO);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("Reporte Ejecutivo", FUENTE_SUBTITULO_PORTADA);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingBefore(10f);
        document.add(subtitulo);

        String nombreUsuario = usuario.getNombre() + " " + usuario.getApellidoPaterno();
        Paragraph generadoPor = new Paragraph("Generado por " + nombreUsuario, FUENTE_META_PORTADA);
        generadoPor.setAlignment(Element.ALIGN_CENTER);
        generadoPor.setSpacingBefore(30f);
        document.add(generadoPor);

        Paragraph fecha = new Paragraph(LocalDate.now().format(FORMATO_FECHA), FUENTE_META_PORTADA);
        fecha.setAlignment(Element.ALIGN_CENTER);
        fecha.setSpacingBefore(4f);
        document.add(fecha);
    }

    private <T> void agregarSeccionTexto(Document document, String titulo, AnalisisGenerado analisis, Class<T> tipo,
            ContenidoRenderer<T> renderer) throws DocumentException {
        agregarTituloSeccion(document, titulo);

        if (analisis == null) {
            agregarPendiente(document, "Pendiente: aún no se generó este módulo.");
            return;
        }

        T contenido = deserializar(analisis, tipo);
        renderer.render(document, contenido);
    }

    private void contenidoDiagnostico(Document document, DiagnosticoForm form) {
        try {
            agregarEtiquetaValor(document, "Descripción general", form.getDescripcionGeneral());
            agregarEtiquetaValor(document, "Problema que resuelve", form.getProblemaQueResuelve());
            agregarEtiquetaValor(document, "Cliente objetivo", form.getClienteObjetivo());
            agregarEtiquetaValor(document, "Nivel de claridad de la propuesta", form.getNivelClaridadPropuesta());
            agregarLista(document, "Principales riesgos", form.getPrincipalesRiesgos());
            agregarLista(document, "Recomendaciones de mejora", form.getRecomendacionesMejora());
        } catch (DocumentException ex) {
            throw new IllegalStateException("No fue posible generar el PDF del reporte.", ex);
        }
    }

    private void contenidoCanvas(Document document, CanvasForm form) {
        try {
            agregarLista(document, "Segmentos de clientes", form.getSegmentosClientes());
            agregarEtiquetaValor(document, "Propuesta de valor", form.getPropuestaValor());
            agregarLista(document, "Canales", form.getCanales());
            agregarEtiquetaValor(document, "Relación con clientes", form.getRelacionClientes());
            agregarLista(document, "Fuentes de ingresos", form.getFuentesIngresos());
            agregarLista(document, "Recursos clave", form.getRecursosClave());
            agregarLista(document, "Actividades clave", form.getActividadesClave());
            agregarLista(document, "Socios clave", form.getSociosClave());
            agregarLista(document, "Estructura de costos", form.getEstructuraCostos());
        } catch (DocumentException ex) {
            throw new IllegalStateException("No fue posible generar el PDF del reporte.", ex);
        }
    }

    private void contenidoFoda(Document document, FodaForm form) {
        try {
            agregarLista(document, "Fortalezas", form.getFortalezas());
            agregarLista(document, "Oportunidades", form.getOportunidades());
            agregarLista(document, "Debilidades", form.getDebilidades());
            agregarLista(document, "Amenazas", form.getAmenazas());
            agregarLista(document, "Recomendaciones estratégicas", form.getRecomendacionesEstrategicas());
        } catch (DocumentException ex) {
            throw new IllegalStateException("No fue posible generar el PDF del reporte.", ex);
        }
    }

    private void contenidoMarketing(Document document, MarketingForm form) {
        try {
            agregarEtiquetaValor(document, "Público objetivo", form.getPublicoObjetivo());
            agregarEtiquetaValor(document, "Mensaje principal", form.getMensajePrincipal());
            agregarLista(document, "Canales de promoción", form.getCanalesPromocion());
            agregarLista(document, "Estrategias digitales", form.getEstrategiasDigitales());
            agregarLista(document, "Promociones iniciales", form.getPromocionesIniciales());
            agregarLista(document, "Calendario básico", form.getCalendarioBasico());
            agregarLista(document, "Indicadores para medir resultados", form.getIndicadoresResultados());
        } catch (DocumentException ex) {
            throw new IllegalStateException("No fue posible generar el PDF del reporte.", ex);
        }
    }

    private void agregarSeccionFinanciera(Document document, ModuloFinanciero modulo, AnalisisGenerado analisisMercado)
            throws DocumentException {
        agregarTituloSeccion(document, "5. Análisis financiero");

        if (modulo == null) {
            agregarPendiente(document, "Pendiente: aún no se han calculado las finanzas de esta idea.");
            return;
        }

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(16f);
        tabla.setWidths(new float[] { 1.4f, 1f });

        agregarCeldaEncabezado(tabla, "Concepto");
        agregarCeldaEncabezado(tabla, "Valor");

        agregarFilaFinanciera(tabla, "Ingresos estimados", modulo.getIngresosEstimados());
        agregarFilaFinanciera(tabla, "Costos totales", modulo.getCostosTotales());
        agregarFilaFinanciera(tabla, "Costos variables", modulo.getCostosVariables());
        agregarFilaFinanciera(tabla, "Utilidad", modulo.getUtilidad());
        agregarFilaFinanciera(tabla, "Punto de equilibrio", modulo.getPuntoEquilibrio());

        document.add(tabla);

        if (analisisMercado == null) {
            agregarPendiente(document, "Pendiente: aún no se generó la interpretación financiera con IA.");
            return;
        }

        MercadoForm interpretacion = deserializar(analisisMercado, MercadoForm.class);
        agregarEtiquetaValor(document, "Interpretación general", interpretacion.getInterpretacionGeneral());
        agregarLista(document, "Riesgos financieros", interpretacion.getRiesgosFinancieros());
        agregarLista(document, "Recomendaciones de viabilidad", interpretacion.getRecomendacionesViabilidad());
    }

    private void agregarCierre(Document document) throws DocumentException {
        document.newPage();
        Paragraph aviso = new Paragraph(AVISO_FINANCIERO, FUENTE_AVISO);
        aviso.setSpacingBefore(20f);
        document.add(aviso);
    }

    private void agregarTituloSeccion(Document document, String titulo) throws DocumentException {
        Paragraph parrafo = new Paragraph(titulo, FUENTE_SECCION);
        parrafo.setSpacingAfter(14f);
        document.add(parrafo);
    }

    private void agregarPendiente(Document document, String mensaje) throws DocumentException {
        document.add(new Paragraph(mensaje, FUENTE_PENDIENTE));
    }

    private void agregarEtiquetaValor(Document document, String etiqueta, String valor) throws DocumentException {
        Paragraph parrafoEtiqueta = new Paragraph(etiqueta, FUENTE_ETIQUETA);
        parrafoEtiqueta.setSpacingBefore(8f);
        document.add(parrafoEtiqueta);

        Paragraph parrafoValor = new Paragraph(valor, FUENTE_TEXTO);
        parrafoValor.setSpacingAfter(4f);
        document.add(parrafoValor);
    }

    private void agregarLista(Document document, String etiqueta, List<String> valores) throws DocumentException {
        Paragraph parrafoEtiqueta = new Paragraph(etiqueta, FUENTE_ETIQUETA);
        parrafoEtiqueta.setSpacingBefore(8f);
        document.add(parrafoEtiqueta);

        if (valores == null || valores.isEmpty()) {
            document.add(new Paragraph("—", FUENTE_TEXTO));
            return;
        }

        com.lowagie.text.List lista = new com.lowagie.text.List(false, 12f);
        for (String valor : valores) {
            lista.add(new ListItem(valor, FUENTE_TEXTO));
        }
        document.add(lista);
    }

    private void agregarCeldaEncabezado(PdfPTable tabla, String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FUENTE_TABLA_ENCABEZADO));
        celda.setBackgroundColor(COLOR_ACENTO);
        celda.setPadding(6f);
        tabla.addCell(celda);
    }

    private void agregarFilaFinanciera(PdfPTable tabla, String concepto, java.math.BigDecimal valor) {
        PdfPCell celdaConcepto = new PdfPCell(new Phrase(concepto, FUENTE_TABLA_CELDA));
        celdaConcepto.setPadding(6f);
        tabla.addCell(celdaConcepto);

        String texto = valor == null ? "—" : FORMATO_MONEDA.format(valor);
        PdfPCell celdaValor = new PdfPCell(new Phrase(texto, FUENTE_TABLA_CELDA));
        celdaValor.setPadding(6f);
        celdaValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabla.addCell(celdaValor);
    }

    private <T> T deserializar(AnalisisGenerado analisis, Class<T> tipo) {
        try {
            return objectMapper.readValue(analisis.getContenido(), tipo);
        } catch (JacksonException ex) {
            throw new IllegalStateException(
                    "El análisis " + analisis.getId() + " almacenado en BD no es JSON válido.", ex);
        }
    }

    @FunctionalInterface
    private interface ContenidoRenderer<T> {
        void render(Document document, T contenido);
    }
}
