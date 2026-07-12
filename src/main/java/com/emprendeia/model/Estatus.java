package com.emprendeia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "estatus", uniqueConstraints = {
        @UniqueConstraint(name = "uq_estatus_nombre", columnNames = "nombre_estatus")
})
public class Estatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estatus")
    private Long id;

    @Column(name = "nombre_estatus", nullable = false, length = 30)
    private String nombreEstatus;

    @Column(name = "descripcion", length = 150)
    private String descripcion;

    protected Estatus() {
    }

    public Estatus(String nombreEstatus, String descripcion) {
        this.nombreEstatus = nombreEstatus;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public String getNombreEstatus() {
        return nombreEstatus;
    }

    public void setNombreEstatus(String nombreEstatus) {
        this.nombreEstatus = nombreEstatus;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
