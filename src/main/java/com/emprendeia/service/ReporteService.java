package com.emprendeia.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emprendeia.exception.ReporteNoGeneradoException;
import com.emprendeia.model.AnalisisGenerado;
import com.emprendeia.model.EstadoReporte;
import com.emprendeia.model.Estatus;
import com.emprendeia.model.Formulario;
import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.ModuloFinanciero;
import com.emprendeia.model.Reporte;
import com.emprendeia.model.Usuario;
import com.emprendeia.report.DatosReporte;
import com.emprendeia.repository.AnalisisGeneradoRepository;
import com.emprendeia.repository.EstadoReporteRepository;
import com.emprendeia.repository.EstatusRepository;
import com.emprendeia.repository.ModuloFinancieroRepository;
import com.emprendeia.repository.ReporteRepository;

/**
 * Recolecta los datos del reporte ejecutivo (RF-15/RF-16) y administra el estado de negocio de
 * {@link Reporte} (borrador/consolidado/descargado, catálogo {@code estado_reporte} — no confundir
 * con {@code id_estatus}, el soft delete). No genera el PDF en sí; eso lo hace
 * {@link com.emprendeia.report.PdfReportBuilder} a partir de {@link #recolectar}.
 * <p>
 * A diferencia de {@link AnalisisService#generar}, no hay ninguna llamada saliente aquí (los
 * módulos de IA ya están persistidos), así que {@link #generarOActualizar} y
 * {@link #marcarDescargado} sí son transaccionales de punta a punta — mismo razonamiento que
 * {@link FinancieroService#calcular}.
 * <p>
 * Un análisis faltante, un {@link Formulario} sin llenar o un {@link ModuloFinanciero} sin
 * calcular no bloquean la generación del reporte: se marca como {@code borrador} en vez de
 * {@code consolidado}, y las secciones correspondientes quedan "pendiente" en el PDF. Esto evita
 * que un módulo de IA fallando en vivo (timeout, JSON inválido) bloquee todo el reporte durante
 * una demo.
 */
@Service
public class ReporteService {

    private static final String ESTATUS_ACTIVO = "ACTIVO";
    private static final String ESTADO_BORRADOR = "borrador";
    private static final String ESTADO_CONSOLIDADO = "consolidado";
    private static final String ESTADO_DESCARGADO = "descargado";

    private final ReporteRepository reporteRepository;
    private final EstadoReporteRepository estadoReporteRepository;
    private final EstatusRepository estatusRepository;
    private final IdeaService ideaService;
    private final FormularioService formularioService;
    private final AnalisisGeneradoRepository analisisGeneradoRepository;
    private final ModuloFinancieroRepository moduloFinancieroRepository;

    public ReporteService(ReporteRepository reporteRepository, EstadoReporteRepository estadoReporteRepository,
            EstatusRepository estatusRepository, IdeaService ideaService, FormularioService formularioService,
            AnalisisGeneradoRepository analisisGeneradoRepository,
            ModuloFinancieroRepository moduloFinancieroRepository) {
        this.reporteRepository = reporteRepository;
        this.estadoReporteRepository = estadoReporteRepository;
        this.estatusRepository = estatusRepository;
        this.ideaService = ideaService;
        this.formularioService = formularioService;
        this.analisisGeneradoRepository = analisisGeneradoRepository;
        this.moduloFinancieroRepository = moduloFinancieroRepository;
    }

    public DatosReporte recolectar(Long ideaId, Usuario usuario) {
        IdeaNegocio idea = ideaService.obtenerPropia(ideaId, usuario);

        Formulario formulario = formularioService.obtenerPorIdea(ideaId, usuario).orElse(null);

        List<AnalisisGenerado> analisis = analisisGeneradoRepository.findByIdeaNegocio(idea);
        var analisisPorTipo = analisis.stream()
                .collect(Collectors.toMap(AnalisisGenerado::getTipoAnalisis, Function.identity()));

        ModuloFinanciero moduloFinanciero = moduloFinancieroRepository.findByIdeaNegocio(idea).orElse(null);

        return new DatosReporte(idea, usuario, formulario, analisisPorTipo, moduloFinanciero);
    }

    public Optional<Reporte> obtenerActual(Long ideaId, Usuario usuario) {
        IdeaNegocio idea = ideaService.obtenerPropia(ideaId, usuario);
        return reporteRepository.findByIdeaNegocio(idea);
    }

    @Transactional
    public Reporte generarOActualizar(Long ideaId, Usuario usuario) {
        DatosReporte datos = recolectar(ideaId, usuario);
        IdeaNegocio idea = datos.idea();

        Reporte reporte = reporteRepository.findByIdeaNegocio(idea)
                .orElseGet(() -> new Reporte(estadoPorNombre(ESTADO_BORRADOR), LocalDate.now(), estatusActivo(), idea));

        reporte.setEstadoReporte(estadoPorNombre(datos.completo() ? ESTADO_CONSOLIDADO : ESTADO_BORRADOR));
        reporte.setFecha(LocalDate.now());

        return reporteRepository.save(reporte);
    }

    @Transactional
    public Reporte marcarDescargado(Long ideaId, Usuario usuario) {
        IdeaNegocio idea = ideaService.obtenerPropia(ideaId, usuario);
        Reporte reporte = reporteRepository.findByIdeaNegocio(idea)
                .orElseThrow(() -> new ReporteNoGeneradoException("Genera el reporte antes de descargarlo."));

        reporte.setEstadoReporte(estadoPorNombre(ESTADO_DESCARGADO));
        reporte.setFecha(LocalDate.now());

        return reporteRepository.save(reporte);
    }

    private EstadoReporte estadoPorNombre(String nombre) {
        return estadoReporteRepository.findByEstadoReporte(nombre)
                .orElseThrow(() -> new IllegalStateException(
                        "El catálogo estado_reporte no tiene una fila con estado_reporte = '" + nombre + "'."));
    }

    private Estatus estatusActivo() {
        return estatusRepository.findByNombreEstatus(ESTATUS_ACTIVO)
                .orElseThrow(() -> new IllegalStateException(
                        "El catálogo estatus no tiene una fila con nombre_estatus = '" + ESTATUS_ACTIVO + "'."));
    }
}
