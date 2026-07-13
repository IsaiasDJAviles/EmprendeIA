package com.emprendeia.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emprendeia.dto.FormularioForm;
import com.emprendeia.model.Estatus;
import com.emprendeia.model.Formulario;
import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.Usuario;
import com.emprendeia.repository.EstatusRepository;
import com.emprendeia.repository.FormularioRepository;

/**
 * Alta y actualización del cuestionario guiado (RF-06), 1:1 con {@link IdeaNegocio}.
 * La pertenencia de la idea al usuario en sesión se valida siempre a través de
 * {@link IdeaService#obtenerPropia}, nunca confiando en un parámetro de request.
 */
@Service
public class FormularioService {

    private static final String ESTATUS_ACTIVO = "ACTIVO";

    private final FormularioRepository formularioRepository;
    private final EstatusRepository estatusRepository;
    private final IdeaService ideaService;

    public FormularioService(FormularioRepository formularioRepository, EstatusRepository estatusRepository,
            IdeaService ideaService) {
        this.formularioRepository = formularioRepository;
        this.estatusRepository = estatusRepository;
        this.ideaService = ideaService;
    }

    public Optional<Formulario> obtenerPorIdea(Long ideaId, Usuario usuario) {
        IdeaNegocio idea = ideaService.obtenerPropia(ideaId, usuario);
        return formularioRepository.findByIdeaNegocio(idea);
    }

    @Transactional
    public Formulario guardar(Long ideaId, FormularioForm form, Usuario usuario) {
        IdeaNegocio idea = ideaService.obtenerPropia(ideaId, usuario);

        Formulario formulario = formularioRepository.findByIdeaNegocio(idea).orElseGet(() -> {
            Estatus activo = estatusRepository.findByNombreEstatus(ESTATUS_ACTIVO)
                    .orElseThrow(() -> new IllegalStateException(
                            "El catálogo estatus no tiene una fila con nombre_estatus = '" + ESTATUS_ACTIVO + "'."));
            return new Formulario(null, null, null, null, null, null, activo, idea);
        });

        formulario.setInversionInicial(form.getInversionInicial());
        formulario.setCostosFijos(form.getCostosFijos());
        formulario.setCostosVariables(form.getCostosVariables());
        formulario.setPrecioVenta(form.getPrecioVenta());
        formulario.setUnidadesEstimadas(form.getUnidadesEstimadas());
        formulario.setDestinoInversion(form.getDestinoInversion());

        return formularioRepository.save(formulario);
    }
}
