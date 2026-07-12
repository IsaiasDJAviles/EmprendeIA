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
@Table(name = "idea_negocio")
public class IdeaNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_idea_negocio")
    private Long id;

    @Column(name = "nombre_negocio", nullable = false, length = 120)
    private String nombreNegocio;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "problema", length = 500)
    private String problema;

    @Column(name = "sector_mercado", length = 120)
    private String sectorMercado;

    @Column(name = "cliente_objetivo", length = 300)
    private String clienteObjetivo;

    @Column(name = "tipo_oferta", length = 60)
    private String tipoOferta;

    @Column(name = "nivel_avance", length = 60)
    private String nivelAvance;

    /**
     * Soft delete transversal (activo/inactivo/eliminado). No confundir con {@link #estado}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estatus", nullable = false)
    private Estatus estatus;

    /**
     * Catálogo geográfico (Estado/Provincia). No confundir con {@link #estatus}
     * ni con el estado de negocio de {@link Reporte}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    protected IdeaNegocio() {
    }

    public IdeaNegocio(String nombreNegocio, String descripcion, String problema, String sectorMercado,
            String clienteObjetivo, String tipoOferta, String nivelAvance, Estatus estatus, Estado estado,
            Usuario usuario) {
        this.nombreNegocio = nombreNegocio;
        this.descripcion = descripcion;
        this.problema = problema;
        this.sectorMercado = sectorMercado;
        this.clienteObjetivo = clienteObjetivo;
        this.tipoOferta = tipoOferta;
        this.nivelAvance = nivelAvance;
        this.estatus = estatus;
        this.estado = estado;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public String getNombreNegocio() {
        return nombreNegocio;
    }

    public void setNombreNegocio(String nombreNegocio) {
        this.nombreNegocio = nombreNegocio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getProblema() {
        return problema;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public String getSectorMercado() {
        return sectorMercado;
    }

    public void setSectorMercado(String sectorMercado) {
        this.sectorMercado = sectorMercado;
    }

    public String getClienteObjetivo() {
        return clienteObjetivo;
    }

    public void setClienteObjetivo(String clienteObjetivo) {
        this.clienteObjetivo = clienteObjetivo;
    }

    public String getTipoOferta() {
        return tipoOferta;
    }

    public void setTipoOferta(String tipoOferta) {
        this.tipoOferta = tipoOferta;
    }

    public String getNivelAvance() {
        return nivelAvance;
    }

    public void setNivelAvance(String nivelAvance) {
        this.nivelAvance = nivelAvance;
    }

    public Estatus getEstatus() {
        return estatus;
    }

    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
