package com.emprendeia.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "analisis_generado")
public class AnalisisGenerado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_analisis_generado")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_analisis", nullable = false, length = 40)
    private TipoAnalisis tipoAnalisis;

    /**
     * Salida estructurada del LLM en formato JSON serializado como texto.
     * La deserialización a objetos Java ocurre en la capa de servicio (Jackson), no aquí.
     */
    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "prompt_completo", columnDefinition = "TEXT")
    private String promptCompleto;

    @Column(name = "fecha_generacion", nullable = false)
    private OffsetDateTime fechaGeneracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estatus", nullable = false)
    private Estatus estatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_idea_negocio", nullable = false)
    private IdeaNegocio ideaNegocio;

    protected AnalisisGenerado() {
    }

    public AnalisisGenerado(TipoAnalisis tipoAnalisis, String contenido, String promptCompleto,
            OffsetDateTime fechaGeneracion, Estatus estatus, IdeaNegocio ideaNegocio) {
        this.tipoAnalisis = tipoAnalisis;
        this.contenido = contenido;
        this.promptCompleto = promptCompleto;
        this.fechaGeneracion = fechaGeneracion;
        this.estatus = estatus;
        this.ideaNegocio = ideaNegocio;
    }

    public Long getId() {
        return id;
    }

    public TipoAnalisis getTipoAnalisis() {
        return tipoAnalisis;
    }

    public void setTipoAnalisis(TipoAnalisis tipoAnalisis) {
        this.tipoAnalisis = tipoAnalisis;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getPromptCompleto() {
        return promptCompleto;
    }

    public void setPromptCompleto(String promptCompleto) {
        this.promptCompleto = promptCompleto;
    }

    public OffsetDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(OffsetDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
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
