package com.emprendeia.controller;

import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.emprendeia.exception.AnalisisInvalidoException;
import com.emprendeia.exception.FinancieroInvalidoException;
import com.emprendeia.exception.IdeaNoEncontradaException;
import com.emprendeia.ia.LlmException;
import com.emprendeia.model.Formulario;
import com.emprendeia.model.ModuloFinanciero;
import com.emprendeia.model.TipoAnalisis;
import com.emprendeia.security.UsuarioPrincipal;
import com.emprendeia.service.AnalisisService;
import com.emprendeia.service.FinancieroService;
import com.emprendeia.service.FormularioService;
import com.emprendeia.service.ResultadoFinanciero;

/**
 * Cálculo financiero (RF-13) e interpretación de viabilidad. "Calcular" hace dos pasos
 * secuenciales: {@link FinancieroService#calcular} (Java puro, siempre primero) y, si eso
 * tuvo éxito, {@link AnalisisService#generar} sobre {@link TipoAnalisis#MERCADO} para la
 * interpretación del LLM. No se componen como dependencia de un servicio sobre el otro
 * (evita un ciclo: AnalisisService ya depende de ModuloFinanciero) — la orquestación de los
 * dos pasos vive aquí.
 */
@Controller
public class FinancieroController {

    private final FinancieroService financieroService;
    private final FormularioService formularioService;
    private final AnalisisService analisisService;

    public FinancieroController(FinancieroService financieroService, FormularioService formularioService,
            AnalisisService analisisService) {
        this.financieroService = financieroService;
        this.formularioService = formularioService;
        this.analisisService = analisisService;
    }

    @GetMapping("/ideas/{id}/finanzas")
    public String mostrar(@PathVariable("id") Long ideaId, @AuthenticationPrincipal UsuarioPrincipal principal,
            Model model) {
        try {
            Optional<Formulario> formulario = formularioService.obtenerPorIdea(ideaId, principal.getUsuario());
            Optional<ModuloFinanciero> modulo = financieroService.obtenerActual(ideaId, principal.getUsuario());

            model.addAttribute("ideaId", ideaId);
            model.addAttribute("formularioCompleto", formulario.isPresent());
            model.addAttribute("modulo", modulo.orElse(null));

            ResultadoFinanciero resultado = null;
            String margenError = null;
            if (formulario.isPresent()) {
                try {
                    resultado = financieroService.calcularResultado(formulario.get());
                } catch (FinancieroInvalidoException ex) {
                    margenError = ex.getMessage();
                }
            }
            model.addAttribute("resultado", resultado);
            model.addAttribute("margenError", margenError);

            return "finanzas";
        } catch (IdeaNoEncontradaException ex) {
            return "redirect:/dashboard?error";
        }
    }

    @PostMapping("/ideas/{id}/finanzas/calcular")
    public String calcular(@PathVariable("id") Long ideaId, @AuthenticationPrincipal UsuarioPrincipal principal) {
        try {
            financieroService.calcular(ideaId, principal.getUsuario());
        } catch (IdeaNoEncontradaException ex) {
            return "redirect:/dashboard?error";
        } catch (FinancieroInvalidoException ex) {
            return "redirect:/ideas/" + ideaId + "/finanzas?error=datos";
        }

        try {
            analisisService.generar(ideaId, TipoAnalisis.MERCADO, principal.getUsuario());
        } catch (LlmException | AnalisisInvalidoException ex) {
            return "redirect:/ideas/" + ideaId + "/finanzas?error=ia";
        }

        return "redirect:/ideas/" + ideaId + "/finanzas";
    }
}
