package com.emprendeia.controller;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.emprendeia.dto.FormularioForm;
import com.emprendeia.dto.IdeaForm;
import com.emprendeia.exception.IdeaNoEncontradaException;
import com.emprendeia.model.Formulario;
import com.emprendeia.security.UsuarioPrincipal;
import com.emprendeia.service.FormularioService;
import com.emprendeia.service.IdeaService;

@Controller
public class IdeaController {

    private final IdeaService ideaService;
    private final FormularioService formularioService;

    public IdeaController(IdeaService ideaService, FormularioService formularioService) {
        this.ideaService = ideaService;
        this.formularioService = formularioService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        model.addAttribute("ideas", ideaService.listarPorUsuario(principal.getUsuario()));
        return "dashboard";
    }

    @GetMapping("/ideas/nueva")
    public String mostrarFormularioIdea(Model model) {
        if (!model.containsAttribute("ideaForm")) {
            model.addAttribute("ideaForm", new IdeaForm());
        }
        model.addAttribute("estados", ideaService.listarEstadosDisponibles());
        return "nueva-idea";
    }

    @PostMapping("/ideas/nueva")
    public String crearIdea(@Valid @ModelAttribute("ideaForm") IdeaForm ideaForm, BindingResult bindingResult,
            @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("estados", ideaService.listarEstadosDisponibles());
            return "nueva-idea";
        }

        ideaService.crear(ideaForm, principal.getUsuario());

        return "redirect:/dashboard";
    }

    @GetMapping("/ideas/{id}/formulario")
    public String mostrarFormularioGuiado(@PathVariable("id") Long ideaId,
            @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        try {
            if (!model.containsAttribute("formularioForm")) {
                model.addAttribute("formularioForm", formularioService.obtenerPorIdea(ideaId, principal.getUsuario())
                        .map(IdeaController::aFormularioForm)
                        .orElseGet(FormularioForm::new));
            }
            model.addAttribute("ideaId", ideaId);
            return "formulario-guiado";
        } catch (IdeaNoEncontradaException ex) {
            return "redirect:/dashboard?error=idea-no-encontrada";
        }
    }

    @PostMapping("/ideas/{id}/formulario")
    public String guardarFormularioGuiado(@PathVariable("id") Long ideaId,
            @Valid @ModelAttribute("formularioForm") FormularioForm formularioForm, BindingResult bindingResult,
            @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("ideaId", ideaId);
            return "formulario-guiado";
        }

        try {
            formularioService.guardar(ideaId, formularioForm, principal.getUsuario());
        } catch (IdeaNoEncontradaException ex) {
            return "redirect:/dashboard?error=idea-no-encontrada";
        }

        return "redirect:/dashboard";
    }

    private static FormularioForm aFormularioForm(Formulario formulario) {
        FormularioForm form = new FormularioForm();
        form.setInversionInicial(formulario.getInversionInicial());
        form.setCostosFijos(formulario.getCostosFijos());
        form.setCostosVariables(formulario.getCostosVariables());
        form.setPrecioVenta(formulario.getPrecioVenta());
        form.setUnidadesEstimadas(formulario.getUnidadesEstimadas());
        form.setDestinoInversion(formulario.getDestinoInversion());
        return form;
    }
}
