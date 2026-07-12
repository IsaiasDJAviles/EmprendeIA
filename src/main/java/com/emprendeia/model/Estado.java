package com.emprendeia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Catálogo geográfico (Estado/Provincia). No confundir con {@link Estatus}
 * (soft delete) ni con el estado de negocio de {@link Reporte}.
 */
@Entity
@Table(name = "estado", uniqueConstraints = {
        @UniqueConstraint(name = "uq_estado_nombre_pais", columnNames = {"nombre_estado", "id_pais"})
})
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Long id;

    @Column(name = "nombre_estado", nullable = false, length = 80)
    private String nombreEstado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pais", nullable = false)
    private Pais pais;

    protected Estado() {
    }

    public Estado(String nombreEstado, Pais pais) {
        this.nombreEstado = nombreEstado;
        this.pais = pais;
    }

    public Long getId() {
        return id;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }
}
