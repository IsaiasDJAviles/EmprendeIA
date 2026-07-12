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
@Table(name = "idea_negocio_tipo_capital", uniqueConstraints = {
        @UniqueConstraint(name = "uq_idea_negocio_tipo_capital", columnNames = {"id_idea_negocio", "id_tipo_capital"})
})
public class IdeaNegocioTipoCapital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_idea_tipo_capital")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_idea_negocio", nullable = false)
    private IdeaNegocio ideaNegocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_capital", nullable = false)
    private TipoCapital tipoCapital;

    protected IdeaNegocioTipoCapital() {
    }

    public IdeaNegocioTipoCapital(IdeaNegocio ideaNegocio, TipoCapital tipoCapital) {
        this.ideaNegocio = ideaNegocio;
        this.tipoCapital = tipoCapital;
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

    public TipoCapital getTipoCapital() {
        return tipoCapital;
    }

    public void setTipoCapital(TipoCapital tipoCapital) {
        this.tipoCapital = tipoCapital;
    }
}
