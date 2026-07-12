package com.emprendeia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "pais", uniqueConstraints = {
        @UniqueConstraint(name = "uq_pais_nombre", columnNames = "nombre_pais")
})
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pais")
    private Long id;

    @Column(name = "nombre_pais", nullable = false, length = 80)
    private String nombrePais;

    protected Pais() {
    }

    public Pais(String nombrePais) {
        this.nombrePais = nombrePais;
    }

    public Long getId() {
        return id;
    }

    public String getNombrePais() {
        return nombrePais;
    }

    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }
}
