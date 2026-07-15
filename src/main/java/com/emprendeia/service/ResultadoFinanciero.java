package com.emprendeia.service;

import java.math.BigDecimal;

/**
 * Resultado completo del cálculo RF-13, incluyendo el margen de contribución.
 * Solo un subconjunto de estos campos se persiste en {@link com.emprendeia.model.ModuloFinanciero}
 * (esa tabla no tiene columna para margen de contribución); el resto es informativo,
 * recalculado en cada solicitud a partir del {@link com.emprendeia.model.Formulario}.
 */
public record ResultadoFinanciero(
        BigDecimal ingresosEstimados,
        BigDecimal costosTotales,
        BigDecimal costosVariables,
        BigDecimal utilidad,
        BigDecimal margenContribucionUnitario,
        BigDecimal margenContribucionPorcentual,
        BigDecimal puntoEquilibrio) {
}
