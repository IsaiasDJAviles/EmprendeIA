package com.emprendeia.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FormularioForm {

    @NotNull
    @DecimalMin(value = "0", message = "no puede ser negativo")
    private BigDecimal inversionInicial;

    @NotNull
    @DecimalMin(value = "0", message = "no puede ser negativo")
    private BigDecimal costosFijos;

    @NotNull
    @DecimalMin(value = "0", message = "no puede ser negativo")
    private BigDecimal costosVariables;

    @NotNull
    @DecimalMin(value = "0", message = "no puede ser negativo")
    private BigDecimal precioVenta;

    @NotNull
    @Min(value = 0, message = "no puede ser negativo")
    private Integer unidadesEstimadas;

    @Size(max = 300)
    private String destinoInversion;

    public BigDecimal getInversionInicial() {
        return inversionInicial;
    }

    public void setInversionInicial(BigDecimal inversionInicial) {
        this.inversionInicial = inversionInicial;
    }

    public BigDecimal getCostosFijos() {
        return costosFijos;
    }

    public void setCostosFijos(BigDecimal costosFijos) {
        this.costosFijos = costosFijos;
    }

    public BigDecimal getCostosVariables() {
        return costosVariables;
    }

    public void setCostosVariables(BigDecimal costosVariables) {
        this.costosVariables = costosVariables;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Integer getUnidadesEstimadas() {
        return unidadesEstimadas;
    }

    public void setUnidadesEstimadas(Integer unidadesEstimadas) {
        this.unidadesEstimadas = unidadesEstimadas;
    }

    public String getDestinoInversion() {
        return destinoInversion;
    }

    public void setDestinoInversion(String destinoInversion) {
        this.destinoInversion = destinoInversion;
    }
}
