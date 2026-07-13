package com.emprendeia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class IdeaForm {

    @NotBlank
    @Size(max = 120)
    private String nombreNegocio;

    @Size(max = 500)
    private String descripcion;

    @Size(max = 500)
    private String problema;

    @Size(max = 120)
    private String sectorMercado;

    @Size(max = 300)
    private String clienteObjetivo;

    @Size(max = 60)
    private String tipoOferta;

    @Size(max = 60)
    private String nivelAvance;

    @NotNull
    private Long idEstado;

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

    public Long getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Long idEstado) {
        this.idEstado = idEstado;
    }
}
