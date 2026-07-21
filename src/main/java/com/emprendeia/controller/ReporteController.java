package com.emprendeia.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.emprendeia.exception.IdeaNoEncontradaException;
import com.emprendeia.exception.ReporteNoGeneradoException;
import com.emprendeia.model.TipoAnalisis;
import com.emprendeia.report.DatosReporte;
import com.emprendeia.report.PdfReportBuilder;
import com.emprendeia.security.UsuarioPrincipal;
import com.emprendeia.service.ReporteService;

/**
 * Vista, generación y descarga del reporte ejecutivo (RF-15/RF-16). No hay columna para los
 * bytes del PDF (ver {@code reporte} en la BD), así que la descarga siempre reconstruye
 * {@link DatosReporte} en vivo y genera el PDF al vuelo en cada solicitud.
 */
@Controller
public class ReporteController {

    private final ReporteService reporteService;
    private final PdfReportBuilder pdfReportBuilder;

    public ReporteController(ReporteService reporteService, PdfReportBuilder pdfReportBuilder) {
        this.reporteService = reporteService;
        this.pdfReportBuilder = pdfReportBuilder;
    }

    @GetMapping("/ideas/{id}/reporte")
    public String mostrar(@PathVariable("id") Long ideaId, @AuthenticationPrincipal UsuarioPrincipal principal,
            Model model) {
        try {
            DatosReporte datos = reporteService.recolectar(ideaId, principal.getUsuario());

            model.addAttribute("ideaId", ideaId);
            model.addAttribute("idea", datos.idea());
            model.addAttribute("reporte", reporteService.obtenerActual(ideaId, principal.getUsuario()).orElse(null));
            model.addAttribute("secciones", secciones(datos));

            return "reporte";
        } catch (IdeaNoEncontradaException ex) {
            return "redirect:/dashboard?error";
        }
    }

    @PostMapping("/ideas/{id}/reporte/generar")
    public String generar(@PathVariable("id") Long ideaId, @AuthenticationPrincipal UsuarioPrincipal principal) {
        try {
            reporteService.generarOActualizar(ideaId, principal.getUsuario());
        } catch (IdeaNoEncontradaException ex) {
            return "redirect:/dashboard?error";
        }

        return "redirect:/ideas/" + ideaId + "/reporte";
    }

    @GetMapping("/ideas/{id}/reporte/descargar")
    public ResponseEntity<byte[]> descargar(@PathVariable("id") Long ideaId,
            @AuthenticationPrincipal UsuarioPrincipal principal) {
        var usuario = principal.getUsuario();

        try {
            reporteService.obtenerActual(ideaId, usuario)
                    .orElseThrow(() -> new ReporteNoGeneradoException("Genera el reporte antes de descargarlo."));

            DatosReporte datos = reporteService.recolectar(ideaId, usuario);
            byte[] pdf = pdfReportBuilder.construir(datos);
            reporteService.marcarDescargado(ideaId, usuario);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte-idea-" + ideaId + ".pdf\"")
                    .body(pdf);
        } catch (IdeaNoEncontradaException ex) {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/dashboard?error")).build();
        } catch (ReporteNoGeneradoException ex) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/ideas/" + ideaId + "/reporte?error=no-generado"))
                    .build();
        }
    }

    private static List<SeccionEstado> secciones(DatosReporte datos) {
        return List.of(
                new SeccionEstado("Finanzas", datos.moduloFinanciero() != null),
                new SeccionEstado("Diagnóstico", datos.analisisPorTipo().containsKey(TipoAnalisis.DIAGNOSTICO)),
                new SeccionEstado("Canvas", datos.analisisPorTipo().containsKey(TipoAnalisis.CANVAS)),
                new SeccionEstado("FODA", datos.analisisPorTipo().containsKey(TipoAnalisis.FODA)),
                new SeccionEstado("Interpretación financiera (IA)",
                        datos.analisisPorTipo().containsKey(TipoAnalisis.MERCADO)),
                new SeccionEstado("Marketing", datos.analisisPorTipo().containsKey(TipoAnalisis.MARKETING)));
    }

    public record SeccionEstado(String etiqueta, boolean disponible) {
    }
}
