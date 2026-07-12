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

@Entity
@Table(name = "idea_negocio_giro", uniqueConstraints = {
        @UniqueConstraint(name = "uq_idea_negocio_giro", columnNames = {"id_idea_negocio", "id_giro"})
})
public class IdeaNegocioGiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_idea_negocio_giro")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_idea_negocio", nullable = false)
    private IdeaNegocio ideaNegocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_giro", nullable = false)
    private Giro giro;

    protected IdeaNegocioGiro() {
    }

    public IdeaNegocioGiro(IdeaNegocio ideaNegocio, Giro giro) {
        this.ideaNegocio = ideaNegocio;
        this.giro = giro;
    }

    public Long getId() {
        return id;
    }

    public IdeaNegocio getIdeaNegocio() {
        return ideaNegocio;
    }

    public void setIdeaNegocio(IdeaNegocio ideaNegocio) {
        this.ideaNegocio = ideaNegocio;
    }

    public Giro getGiro() {
        return giro;
    }

    public void setGiro(Giro giro) {
        this.giro = giro;
    }
}
