package com.emprendeia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tipo_capital", uniqueConstraints = {
        @UniqueConstraint(name = "uq_tipo_capital_nombre", columnNames = "tipo_capital")
})
public class TipoCapital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_capital")
    private Long id;

    @Column(name = "tipo_capital", nullable = false, length = 80)
    private String tipoCapital;

    @Column(name = "descripcion", length = 150)
    private String descripcion;

    protected TipoCapital() {
    }

    public TipoCapital(String tipoCapital, String descripcion) {
        this.tipoCapital = tipoCapital;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public String getTipoCapital() {
        return tipoCapital;
    }

    public void setTipoCapital(String tipoCapital) {
        this.tipoCapital = tipoCapital;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
