package com.emprendeia.report;

import java.util.Map;

import com.emprendeia.model.AnalisisGenerado;
import com.emprendeia.model.Formulario;
import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.ModuloFinanciero;
import com.emprendeia.model.TipoAnalisis;
import com.emprendeia.model.Usuario;

/**
 * Datos ya persistidos para armar el reporte ejecutivo de una idea, recolectados por
 * {@link com.emprendeia.service.ReporteService#recolectar}. {@code formulario} y
 * {@code moduloFinanciero} son {@code null} si el usuario todavía no los completó/calculó;
 * {@code analisisPorTipo} solo contiene las claves de los módulos de IA ya generados —
 * un {@link TipoAnalisis} ausente del mapa significa que esa sección está pendiente.
 * {@link PdfReportBuilder} decide, para cada hueco, si renderiza un aviso de "pendiente"
 * en vez de bloquear el resto del documento (ver justificación en el plan del módulo).
 */
public record DatosReporte(
        IdeaNegocio idea,
        Usuario usuario,
        Formulario formulario,
        Map<TipoAnalisis, AnalisisGenerado> analisisPorTipo,
        ModuloFinanciero moduloFinanciero) {

    public boolean completo() {
        return analisisPorTipo.size() == TipoAnalisis.values().length && moduloFinanciero != null;
    }
}
