package com.emprendeia.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emprendeia.exception.FinancieroInvalidoException;
import com.emprendeia.model.Estatus;
import com.emprendeia.model.Formulario;
import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.ModuloFinanciero;
import com.emprendeia.model.Usuario;
import com.emprendeia.repository.EstatusRepository;
import com.emprendeia.repository.ModuloFinancieroRepository;

/**
 * Cálculo financiero en Java puro (RF-13), sin LLM. La captura de datos de entrada
 * (RF-12) ya existe en {@link Formulario}/{@link FormularioService}; este servicio
 * solo lee esos valores y deriva ingresos, costos, utilidad, margen de contribución
 * y punto de equilibrio.
 * <p>
 * {@code modulo_financiero} no tiene columna para margen de contribución (ver DDL), así
 * que ese valor viaja en {@link ResultadoFinanciero} pero nunca se persiste; se recalcula
 * en cada solicitud a partir del {@link Formulario} vigente.
 */
@Service
public class FinancieroService {

    private static final String ESTATUS_ACTIVO = "ACTIVO";
    private static final int ESCALA_MONETARIA = 2;
    private static final int ESCALA_INTERMEDIA = 10;

    private final ModuloFinancieroRepository moduloFinancieroRepository;
    private final EstatusRepository estatusRepository;
    private final IdeaService ideaService;
    private final FormularioService formularioService;

    public FinancieroService(ModuloFinancieroRepository moduloFinancieroRepository, EstatusRepository estatusRepository,
            IdeaService ideaService, FormularioService formularioService) {
        this.moduloFinancieroRepository = moduloFinancieroRepository;
        this.estatusRepository = estatusRepository;
        this.ideaService = ideaService;
        this.formularioService = formularioService;
    }

    public Optional<ModuloFinanciero> obtenerActual(Long ideaId, Usuario usuario) {
        IdeaNegocio idea = ideaService.obtenerPropia(ideaId, usuario);
        return moduloFinancieroRepository.findByIdeaNegocio(idea);
    }

    /**
     * Recalcula RF-13 a partir del {@link Formulario} vigente de la idea, sin persistir
     * nada. Sirve tanto para mostrar el resultado en pantalla antes de guardar como para
     * el propio {@link #calcular}.
     *
     * @throws FinancieroInvalidoException si los datos no permiten un punto de equilibrio
     *         válido (unidades estimadas en cero, o el precio de venta no cubre el costo
     *         variable unitario)
     */
    public ResultadoFinanciero calcularResultado(Formulario formulario) {
        BigDecimal precioVenta = formulario.getPrecioVenta();
        BigDecimal costosFijos = formulario.getCostosFijos();
        BigDecimal costosVariables = formulario.getCostosVariables();
        int unidadesEstimadas = formulario.getUnidadesEstimadas();

        if (unidadesEstimadas <= 0) {
            throw new FinancieroInvalidoException(
                    "No es posible calcular el punto de equilibrio sin unidades estimadas mayores a cero.");
        }

        BigDecimal unidades = BigDecimal.valueOf(unidadesEstimadas);
        BigDecimal ingresosEstimados = precioVenta.multiply(unidades);
        BigDecimal costosTotales = costosFijos.add(costosVariables);
        BigDecimal utilidad = ingresosEstimados.subtract(costosTotales);

        BigDecimal costoVariableUnitario = costosVariables.divide(unidades, ESCALA_INTERMEDIA, RoundingMode.HALF_UP);
        BigDecimal margenContribucionUnitario = precioVenta.subtract(costoVariableUnitario);

        if (margenContribucionUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new FinancieroInvalidoException(
                    "El precio de venta no cubre el costo variable unitario; no es posible calcular "
                            + "un punto de equilibrio con estos datos.");
        }

        BigDecimal margenContribucionPorcentual = margenContribucionUnitario.divide(precioVenta, ESCALA_INTERMEDIA,
                RoundingMode.HALF_UP);
        BigDecimal puntoEquilibrio = costosFijos.divide(margenContribucionPorcentual, ESCALA_INTERMEDIA,
                RoundingMode.HALF_UP);

        return new ResultadoFinanciero(
                ingresosEstimados.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP),
                costosTotales.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP),
                costosVariables.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP),
                utilidad.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP),
                margenContribucionUnitario.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP),
                margenContribucionPorcentual.setScale(4, RoundingMode.HALF_UP),
                puntoEquilibrio.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP));
    }

    @Transactional
    public ModuloFinanciero calcular(Long ideaId, Usuario usuario) {
        IdeaNegocio idea = ideaService.obtenerPropia(ideaId, usuario);
        Formulario formulario = formularioService.obtenerPorIdea(ideaId, usuario)
                .orElseThrow(() -> new FinancieroInvalidoException(
                        "Completa primero el formulario guiado (inversión, costos, precio y unidades estimadas)."));

        ResultadoFinanciero resultado = calcularResultado(formulario);

        ModuloFinanciero modulo = moduloFinancieroRepository.findByIdeaNegocio(idea)
                .orElseGet(() -> new ModuloFinanciero(null, null, null, null, null, estatusActivo(), idea));

        modulo.setIngresosEstimados(resultado.ingresosEstimados());
        modulo.setCostosTotales(resultado.costosTotales());
        modulo.setCostosVariables(resultado.costosVariables());
        modulo.setUtilidad(resultado.utilidad());
        modulo.setPuntoEquilibrio(resultado.puntoEquilibrio());

        return moduloFinancieroRepository.save(modulo);
    }

    private Estatus estatusActivo() {
        return estatusRepository.findByNombreEstatus(ESTATUS_ACTIVO)
                .orElseThrow(() -> new IllegalStateException(
                        "El catálogo estatus no tiene una fila con nombre_estatus = '" + ESTATUS_ACTIVO + "'."));
    }
}
