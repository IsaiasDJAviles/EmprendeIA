package com.emprendeia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Estado de negocio del reporte ejecutivo (borrador, consolidado, descargado).
 * Distinto de {@link Estatus} (soft delete transversal).
 */
@Entity
@Table(name = "estado_reporte", uniqueConstraints = {
        @UniqueConstraint(name = "uq_estado_reporte_nombre", columnNames = "estado_reporte")
})
public class EstadoReporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_reporte")
    private Long id;

    @Column(name = "estado_reporte", nullable = false, length = 30)
    private String estadoReporte;

    protected EstadoReporte() {
    }

    public EstadoReporte(String estadoReporte) {
        this.estadoReporte = estadoReporte;
    }

    public Long getId() {
        return id;
    }

    public String getEstadoReporte() {
        return estadoReporte;
    }

    public void setEstadoReporte(String estadoReporte) {
        this.estadoReporte = estadoReporte;
    }
}
