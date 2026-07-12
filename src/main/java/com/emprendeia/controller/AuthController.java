package com.emprendeia.controller;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.emprendeia.dto.RegistroForm;
import com.emprendeia.exception.CorreoYaRegistradoException;
import com.emprendeia.service.UsuarioService;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        if (!model.containsAttribute("registroForm")) {
            model.addAttribute("registroForm", new RegistroForm());
        }
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("registroForm") RegistroForm registroForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "registro";
        }

        try {
            usuarioService.registrar(registroForm);
        } catch (CorreoYaRegistradoException ex) {
            bindingResult.rejectValue("correo", "correo.duplicado", ex.getMessage());
            return "registro";
        }

        return "redirect:/login?registrado";
    }
}
