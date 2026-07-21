package com.emprendeia.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emprendeia.dto.IdeaForm;
import com.emprendeia.exception.IdeaNoEncontradaException;
import com.emprendeia.model.Estado;
import com.emprendeia.model.Estatus;
import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.Usuario;
import com.emprendeia.repository.EstadoRepository;
import com.emprendeia.repository.EstatusRepository;
import com.emprendeia.repository.IdeaNegocioRepository;

@Service
public class IdeaService {

    /**
     * Requiere que el catálogo {@code estatus} tenga una fila con este valor
     * (precondición de datos: no hay script de seed en el repo todavía).
     */
    private static final String ESTATUS_ACTIVO = "ACTIVO";
    private static final String ESTATUS_INACTIVO = "INACTIVO";

    private final IdeaNegocioRepository ideaNegocioRepository;
    private final EstatusRepository estatusRepository;
    private final EstadoRepository estadoRepository;

    public IdeaService(IdeaNegocioRepository ideaNegocioRepository, EstatusRepository estatusRepository,
            EstadoRepository estadoRepository) {
        this.ideaNegocioRepository = ideaNegocioRepository;
        this.estatusRepository = estatusRepository;
        this.estadoRepository = estadoRepository;
    }

    @Transactional
    public IdeaNegocio crear(IdeaForm form, Usuario usuario) {
        Estatus activo = estatusRepository.findByNombreEstatus(ESTATUS_ACTIVO)
                .orElseThrow(() -> new IllegalStateException(
                        "El catálogo estatus no tiene una fila con nombre_estatus = '" + ESTATUS_ACTIVO + "'."));

        Estado estado = estadoRepository.findById(form.getIdEstado())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un estado con id " + form.getIdEstado() + "."));

        IdeaNegocio idea = new IdeaNegocio(
                form.getNombreNegocio(),
                form.getDescripcion(),
                form.getProblema(),
                form.getSectorMercado(),
                form.getClienteObjetivo(),
                form.getTipoOferta(),
                form.getNivelAvance(),
                LocalDate.now(),
                activo,
                estado,
                usuario);

        return ideaNegocioRepository.save(idea);
    }

    /**
     * Ideas activas del usuario (excluye las descartadas por {@link #eliminar}), filtradas por
     * coincidencia parcial de nombre si {@code busqueda} no está vacía, y ordenadas por fecha de
     * creación.
     */
    public List<IdeaNegocio> listarPorUsuario(Usuario usuario, String busqueda, boolean ordenAscendente) {
        String patron = (busqueda == null || busqueda.isBlank()) ? "" : busqueda.trim();
        Sort sort = Sort.by(ordenAscendente ? Sort.Direction.ASC : Sort.Direction.DESC, "fechaCreacion");
        return ideaNegocioRepository.buscarActivasPorUsuario(usuario, ESTATUS_ACTIVO, patron, sort);
    }

    public List<Estado> listarEstadosDisponibles() {
        return estadoRepository.findAllByOrderByNombreEstadoAsc();
    }

    /**
     * Devuelve la idea solo si pertenece al usuario en sesión. Usa el mismo mensaje
     * tanto si la idea no existe como si pertenece a otro usuario, para no revelar
     * por enumeración de IDs si una idea ajena existe (aislamiento de datos, RF-17.1).
     */
    public IdeaNegocio obtenerPropia(Long ideaId, Usuario usuario) {
        IdeaNegocio idea = ideaNegocioRepository.findById(ideaId)
                .orElseThrow(() -> new IdeaNoEncontradaException("La idea solicitada no existe o no te pertenece."));

        if (!idea.getUsuario().getId().equals(usuario.getId())) {
            throw new IdeaNoEncontradaException("La idea solicitada no existe o no te pertenece.");
        }

        return idea;
    }

    /**
     * Soft delete (RF-17.2): marca la idea como {@code INACTIVO} en vez de borrarla físicamente,
     * para que {@link #listarPorUsuario} deje de listarla sin perder el historial ni violar el
     * {@code ON DELETE RESTRICT}/{@code CASCADE} de las tablas hijas.
     */
    @Transactional
    public void eliminar(Long ideaId, Usuario usuario) {
        IdeaNegocio idea = obtenerPropia(ideaId, usuario);
        idea.setEstatus(estatusInactivo());
        ideaNegocioRepository.save(idea);
    }

    private Estatus estatusInactivo() {
        return estatusRepository.findByNombreEstatus(ESTATUS_INACTIVO)
                .orElseThrow(() -> new IllegalStateException(
                        "El catálogo estatus no tiene una fila con nombre_estatus = '" + ESTATUS_INACTIVO + "'."));
    }
}
