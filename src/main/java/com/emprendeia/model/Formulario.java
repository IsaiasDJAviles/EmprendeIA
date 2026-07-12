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
 * Respuestas del cuestionario guiado, cardinalidad 1:1 con {@link IdeaNegocio}
 * (garantizada por la FK única {@code id_idea_negocio} en el DDL).
 */
@Entity
@Table(name = "formulario", uniqueConstraints = {
        @UniqueConstraint(name = "uq_formulario_idea_negocio", columnNames = "id_idea_negocio")
})
public class Formulario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formulario")
    private Long id;

    @Column(name = "inversion_inicial", nullable = false, precision = 14, scale = 2)
    private BigDecimal inversionInicial;

    @Column(name = "costos_fijos", nullable = false, precision = 14, scale = 2)
    private BigDecimal costosFijos;

    @Column(name = "costos_variables", nullable = false, precision = 14, scale = 2)
    private BigDecimal costosVariables;

    @Column(name = "precio_venta", nullable = false, precision = 14, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "unidades_estimadas", nullable = false)
    private Integer unidadesEstimadas;

    @Column(name = "destino_inversion", length = 300)
    private String destinoInversion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estatus", nullable = false)
    private Estatus estatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_idea_negocio", nullable = false, unique = true)
    private IdeaNegocio ideaNegocio;

    protected Formulario() {
    }

    public Formulario(BigDecimal inversionInicial, BigDecimal costosFijos, BigDecimal costosVariables,
            BigDecimal precioVenta, Integer unidadesEstimadas, String destinoInversion, Estatus estatus,
            IdeaNegocio ideaNegocio) {
        this.inversionInicial = inversionInicial;
        this.costosFijos = costosFijos;
        this.costosVariables = costosVariables;
        this.precioVenta = precioVenta;
        this.unidadesEstimadas = unidadesEstimadas;
        this.destinoInversion = destinoInversion;
        this.estatus = estatus;
        this.ideaNegocio = ideaNegocio;
    }

    public Long getId() {
        return id;
    }

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
