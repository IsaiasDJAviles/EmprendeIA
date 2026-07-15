package com.emprendeia.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class DiagnosticoForm {

    @NotBlank
    private String descripcionGeneral;

    @NotBlank
    private String problemaQueResuelve;

    @NotBlank
    private String clienteObjetivo;

    @NotBlank
    private String nivelClaridadPropuesta;

    @NotEmpty
    private List<String> principalesRiesgos = new ArrayList<>();

    @NotEmpty
    private List<String> recomendacionesMejora = new ArrayList<>();

    public String getDescripcionGeneral() {
        return descripcionGeneral;
    }

    public void setDescripcionGeneral(String descripcionGeneral) {
        this.descripcionGeneral = descripcionGeneral;
    }

    public String getProblemaQueResuelve() {
        return problemaQueResuelve;
    }

    public void setProblemaQueResuelve(String problemaQueResuelve) {
        this.problemaQueResuelve = problemaQueResuelve;
    }

    public String getClienteObjetivo() {
        return clienteObjetivo;
    }

    public void setClienteObjetivo(String clienteObjetivo) {
        this.clienteObjetivo = clienteObjetivo;
    }

    public String getNivelClaridadPropuesta() {
        return nivelClaridadPropuesta;
    }

    public void setNivelClaridadPropuesta(String nivelClaridadPropuesta) {
        this.nivelClaridadPropuesta = nivelClaridadPropuesta;
    }

    public List<String> getPrincipalesRiesgos() {
        return principalesRiesgos;
    }

    public void setPrincipalesRiesgos(List<String> principalesRiesgos) {
        this.principalesRiesgos = principalesRiesgos;
    }

    public List<String> getRecomendacionesMejora() {
        return recomendacionesMejora;
    }

    public void setRecomendacionesMejora(List<String> recomendacionesMejora) {
        this.recomendacionesMejora = recomendacionesMejora;
    }
}
