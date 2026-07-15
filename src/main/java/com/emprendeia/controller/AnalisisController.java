package com.emprendeia.controller;

import java.util.Locale;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import com.emprendeia.dto.CanvasForm;
import com.emprendeia.dto.DiagnosticoForm;
import com.emprendeia.dto.FodaForm;
import com.emprendeia.dto.MarketingForm;
import com.emprendeia.dto.MercadoForm;
import com.emprendeia.exception.AnalisisInvalidoException;
import com.emprendeia.exception.IdeaNoEncontradaException;
import com.emprendeia.ia.LlmException;
import com.emprendeia.model.AnalisisGenerado;
import com.emprendeia.model.TipoAnalisis;
import com.emprendeia.security.UsuarioPrincipal;
import com.emprendeia.service.AnalisisService;
import com.emprendeia.service.FinancieroService;

/**
 * Muestra y edita los 5 módulos de análisis (RF-07 a RF-11, RF-14). La ruta de visualización
 * y la de generación son genéricas por {@link TipoAnalisis}; el guardado de ediciones necesita
 * un método por módulo porque cada uno bindea un DTO estructurado distinto (ver {@code dto}).
 * <p>
 * Para {@link TipoAnalisis#MERCADO} también expone en el modelo el {@code ModuloFinanciero}
 * ya calculado (RF-13), como contexto de solo lectura junto a la interpretación editable.
 */
@Controller
public class AnalisisController {

    private final AnalisisService analisisService;
    private final FinancieroService financieroService;
    private final ObjectMapper objectMapper;

    public AnalisisController(AnalisisService analisisService, FinancieroService financieroService,
            ObjectMapper objectMapper) {
        this.analisisService = analisisService;
        this.financieroService = financieroService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/ideas/{id}/analisis/{tipo}")
    public String mostrar(@PathVariable("id") Long ideaId, @PathVariable("tipo") String tipoPath,
            @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        TipoAnalisis tipo = aTipo(tipoPath);
        if (tipo == null) {
            return "redirect:/dashboard?error";
        }

        try {
            Optional<AnalisisGenerado> actual = analisisService.obtenerActual(ideaId, tipo, principal.getUsuario());
            model.addAttribute("ideaId", ideaId);
            model.addAttribute("existe", actual.isPresent());

            return switch (tipo) {
                case DIAGNOSTICO -> {
                    model.addAttribute("form",
                            actual.map(a -> deserializar(a, DiagnosticoForm.class)).orElseGet(DiagnosticoForm::new));
                    yield "analisis/diagnostico";
                }
                case CANVAS -> {
                    model.addAttribute("form",
                            actual.map(a -> deserializar(a, CanvasForm.class)).orElseGet(CanvasForm::new));
                    yield "analisis/canvas";
                }
                case FODA -> {
                    model.addAttribute("form",
                            actual.map(a -> deserializar(a, FodaForm.class)).orElseGet(FodaForm::new));
                    yield "analisis/foda";
                }
                case MERCADO -> {
                    model.addAttribute("form",
                            actual.map(a -> deserializar(a, MercadoForm.class)).orElseGet(MercadoForm::new));
                    model.addAttribute("moduloFinanciero",
                            financieroService.obtenerActual(ideaId, principal.getUsuario()).orElse(null));
                    yield "analisis/mercado";
                }
                case MARKETING -> {
                    model.addAttribute("form",
                            actual.map(a -> deserializar(a, MarketingForm.class)).orElseGet(MarketingForm::new));
                    yield "analisis/marketing";
                }
            };
        } catch (IdeaNoEncontradaException ex) {
            return "redirect:/dashboard?error";
        }
    }

    @PostMapping("/ideas/{id}/analisis/{tipo}/generar")
    public String generar(@PathVariable("id") Long ideaId, @PathVariable("tipo") String tipoPath,
            @AuthenticationPrincipal UsuarioPrincipal principal) {
        TipoAnalisis tipo = aTipo(tipoPath);
        if (tipo == null) {
            return "redirect:/dashboard?error";
        }

        try {
            analisisService.generar(ideaId, tipo, principal.getUsuario());
        } catch (IdeaNoEncontradaException ex) {
            return "redirect:/dashboard?error";
        } catch (LlmException | AnalisisInvalidoException ex) {
            return "redirect:/ideas/" + ideaId + "/analisis/" + tipoPath + "?error";
        }

        return "redirect:/ideas/" + ideaId + "/analisis/" + tipoPath;
    }

    @PostMapping("/ideas/{id}/analisis/diagnostico")
    public String guardarDiagnostico(@PathVariable("id") Long ideaId,
            @Valid @ModelAttribute("form") DiagnosticoForm form, BindingResult bindingResult,
            @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        return guardar(ideaId, TipoAnalisis.DIAGNOSTICO, form, bindingResult, principal, model, "analisis/diagnostico");
    }

    @PostMapping("/ideas/{id}/analisis/canvas")
    public String guardarCanvas(@PathVariable("id") Long ideaId,
            @Valid @ModelAttribute("form") CanvasForm form, BindingResult bindingResult,
            @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        return guardar(ideaId, TipoAnalisis.CANVAS, form, bindingResult, principal, model, "analisis/canvas");
    }

    @PostMapping("/ideas/{id}/analisis/foda")
    public String guardarFoda(@PathVariable("id") Long ideaId,
            @Valid @ModelAttribute("form") FodaForm form, BindingResult bindingResult,
            @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        return guardar(ideaId, TipoAnalisis.FODA, form, bindingResult, principal, model, "analisis/foda");
    }

    @PostMapping("/ideas/{id}/analisis/mercado")
    public String guardarMercado(@PathVariable("id") Long ideaId,
            @Valid @ModelAttribute("form") MercadoForm form, BindingResult bindingResult,
            @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        return guardar(ideaId, TipoAnalisis.MERCADO, form, bindingResult, principal, model, "analisis/mercado");
    }

    @PostMapping("/ideas/{id}/analisis/marketing")
    public String guardarMarketing(@PathVariable("id") Long ideaId,
            @Valid @ModelAttribute("form") MarketingForm form, BindingResult bindingResult,
            @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        return guardar(ideaId, TipoAnalisis.MARKETING, form, bindingResult, principal, model, "analisis/marketing");
    }

    private String guardar(Long ideaId, TipoAnalisis tipo, Object form, BindingResult bindingResult,
            UsuarioPrincipal principal, Model model, String vista) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("ideaId", ideaId);
            model.addAttribute("existe", true);
            agregarModuloFinancieroSiAplica(ideaId, tipo, principal, model);
            return vista;
        }

        try {
            analisisService.guardarEdicion(ideaId, tipo, form, principal.getUsuario());
        } catch (IdeaNoEncontradaException ex) {
            return "redirect:/dashboard?error";
        } catch (AnalisisInvalidoException ex) {
            model.addAttribute("ideaId", ideaId);
            model.addAttribute("existe", true);
            model.addAttribute("errorValidacion", ex.getMessage());
            agregarModuloFinancieroSiAplica(ideaId, tipo, principal, model);
            return vista;
        }

        return "redirect:/ideas/" + ideaId + "/analisis/" + tipo.name().toLowerCase(Locale.ROOT);
    }

    private void agregarModuloFinancieroSiAplica(Long ideaId, TipoAnalisis tipo, UsuarioPrincipal principal,
            Model model) {
        if (tipo == TipoAnalisis.MERCADO) {
            model.addAttribute("moduloFinanciero",
                    financieroService.obtenerActual(ideaId, principal.getUsuario()).orElse(null));
        }
    }

    private static TipoAnalisis aTipo(String tipoPath) {
        try {
            return TipoAnalisis.valueOf(tipoPath.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private <T> T deserializar(AnalisisGenerado analisis, Class<T> tipo) {
        try {
            return objectMapper.readValue(analisis.getContenido(), tipo);
        } catch (JacksonException ex) {
            throw new IllegalStateException(
                    "El análisis " + analisis.getId() + " almacenado en BD no es JSON válido.", ex);
        }
    }
}
