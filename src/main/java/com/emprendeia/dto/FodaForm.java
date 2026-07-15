package com.emprendeia.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class FodaForm {

    @NotEmpty
    private List<String> fortalezas = new ArrayList<>();

    @NotEmpty
    private List<String> oportunidades = new ArrayList<>();

    @NotEmpty
    private List<String> debilidades = new ArrayList<>();

    @NotEmpty
    private List<String> amenazas = new ArrayList<>();

    @NotEmpty
    private List<String> recomendacionesEstrategicas = new ArrayList<>();

    public List<String> getFortalezas() {
        return fortalezas;
    }

    public void setFortalezas(List<String> fortalezas) {
        this.fortalezas = fortalezas;
    }

    public List<String> getOportunidades() {
        return oportunidades;
    }

    public void setOportunidades(List<String> oportunidades) {
        this.oportunidades = oportunidades;
    }

    public List<String> getDebilidades() {
        return debilidades;
    }

    public void setDebilidades(List<String> debilidades) {
        this.debilidades = debilidades;
    }

    public List<String> getAmenazas() {
        return amenazas;
    }

    public void setAmenazas(List<String> amenazas) {
        this.amenazas = amenazas;
    }

    public List<String> getRecomendacionesEstrategicas() {
        return recomendacionesEstrategicas;
    }

    public void setRecomendacionesEstrategicas(List<String> recomendacionesEstrategicas) {
        this.recomendacionesEstrategicas = recomendacionesEstrategicas;
    }
}
