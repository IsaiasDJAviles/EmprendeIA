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

@Entity
@Table(name = "socio")
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_socio")
    private Long id;

    @Column(name = "nombre_socio", nullable = false, length = 150)
    private String nombreSocio;

    @Column(name = "rol", length = 80)
    private String rol;

    @Column(name = "experiencia", length = 300)
    private String experiencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estatus", nullable = false)
    private Estatus estatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_idea_negocio", nullable = false)
    private IdeaNegocio ideaNegocio;

    protected Socio() {
    }

    public Socio(String nombreSocio, String rol, String experiencia, Estatus estatus, IdeaNegocio ideaNegocio) {
        this.nombreSocio = nombreSocio;
        this.rol = rol;
        this.experiencia = experiencia;
        this.estatus = estatus;
        this.ideaNegocio = ideaNegocio;
    }

    public Long getId() {
        return id;
    }

    public String getNombreSocio() {
        return nombreSocio;
    }

    public void setNombreSocio(String nombreSocio) {
        this.nombreSocio = nombreSocio;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
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
