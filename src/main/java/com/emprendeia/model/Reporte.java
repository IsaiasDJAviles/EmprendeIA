package com.emprendeia.model;

import java.time.LocalDate;

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
@Table(name = "reporte", uniqueConstraints = {
        @UniqueConstraint(name = "uq_reporte_idea_negocio", columnNames = "id_idea_negocio")
})
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Long id;

    /**
     * Estado de negocio del reporte (borrador/consolidado/descargado).
     * No confundir con {@link #estatus} (soft delete transversal).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_reporte", nullable = false)
    private EstadoReporte estadoReporte;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estatus", nullable = false)
    private Estatus estatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_idea_negocio", nullable = false, unique = true)
    private IdeaNegocio ideaNegocio;

    protected Reporte() {
    }

    public Reporte(EstadoReporte estadoReporte, LocalDate fecha, Estatus estatus, IdeaNegocio ideaNegocio) {
        this.estadoReporte = estadoReporte;
        this.fecha = fecha;
        this.estatus = estatus;
        this.ideaNegocio = ideaNegocio;
    }

    public Long getId() {
        return id;
    }

    public EstadoReporte getEstadoReporte() {
        return estadoReporte;
    }

    public void setEstadoReporte(EstadoReporte estadoReporte) {
        this.estadoReporte = estadoReporte;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
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
