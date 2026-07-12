package com.emprendeia.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Cardinalidad 1:1 con {@link IdeaNegocio} (FK única {@code id_idea_negocio} en el DDL).
 */
@Entity
@Table(name = "modulo_financiero", uniqueConstraints = {
        @UniqueConstraint(name = "uq_modfin_idea_negocio", columnNames = "id_idea_negocio")
})
public class ModuloFinanciero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modulo_financiero")
    private Long id;

    @Column(name = "ingresos_estimados", nullable = false, precision = 14, scale = 2)
    private BigDecimal ingresosEstimados;

    @Column(name = "costos_totales", nullable = false, precision = 14, scale = 2)
    private BigDecimal costosTotales;

    @Column(name = "costos_variables", nullable = false, precision = 14, scale = 2)
    private BigDecimal costosVariables;

    /**
     * Puede ser negativa (pérdida estimada); no lleva CHECK &gt;= 0 en el DDL,
     * a diferencia de los demás montos de este módulo.
     */
    @Column(name = "utilidad", nullable = false, precision = 14, scale = 2)
    private BigDecimal utilidad;

    @Column(name = "punto_equilibrio", nullable = false, precision = 14, scale = 2)
    private BigDecimal puntoEquilibrio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estatus", nullable = false)
    private Estatus estatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_idea_negocio", nullable = false, unique = true)
    private IdeaNegocio ideaNegocio;

    protected ModuloFinanciero() {
    }

    public ModuloFinanciero(BigDecimal ingresosEstimados, BigDecimal costosTotales, BigDecimal costosVariables,
            BigDecimal utilidad, BigDecimal puntoEquilibrio, Estatus estatus, IdeaNegocio ideaNegocio) {
        this.ingresosEstimados = ingresosEstimados;
        this.costosTotales = costosTotales;
        this.costosVariables = costosVariables;
        this.utilidad = utilidad;
        this.puntoEquilibrio = puntoEquilibrio;
        this.estatus = estatus;
        this.ideaNegocio = ideaNegocio;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getIngresosEstimados() {
        return ingresosEstimados;
    }

    public void setIngresosEstimados(BigDecimal ingresosEstimados) {
        this.ingresosEstimados = ingresosEstimados;
    }

    public BigDecimal getCostosTotales() {
        return costosTotales;
    }

    public void setCostosTotales(BigDecimal costosTotales) {
        this.costosTotales = costosTotales;
    }

    public BigDecimal getCostosVariables() {
        return costosVariables;
    }

    public void setCostosVariables(BigDecimal costosVariables) {
        this.costosVariables = costosVariables;
    }

    public BigDecimal getUtilidad() {
        return utilidad;
    }

    public void setUtilidad(BigDecimal utilidad) {
        this.utilidad = utilidad;
    }

    public BigDecimal getPuntoEquilibrio() {
        return puntoEquilibrio;
    }

    public void setPuntoEquilibrio(BigDecimal puntoEquilibrio) {
        this.puntoEquilibrio = puntoEquilibrio;
    }

    public Estatus getEstatus() {
        return estatus;
    }

    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    public IdeaNegocio getIdeaNegocio() {
        return ideaNegocio;
    }

    public void setIdeaNegocio(IdeaNegocio ideaNegocio) {
        this.ideaNegocio = ideaNegocio;
    }
}
