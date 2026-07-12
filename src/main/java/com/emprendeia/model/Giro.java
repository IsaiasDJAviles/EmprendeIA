package com.emprendeia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "giro", uniqueConstraints = {
        @UniqueConstraint(name = "uq_giro_tipo", columnNames = "tipo_giro")
})
public class Giro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_giro")
    private Long id;

    @Column(name = "tipo_giro", nullable = false, length = 80)
    private String tipoGiro;

    protected Giro() {
    }

    public Giro(String tipoGiro) {
        this.tipoGiro = tipoGiro;
    }

    public Long getId() {
        return id;
    }

    public String getTipoGiro() {
        return tipoGiro;
    }

    public void setTipoGiro(String tipoGiro) {
        this.tipoGiro = tipoGiro;
    }
}
