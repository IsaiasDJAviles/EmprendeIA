package com.emprendeia.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class CanvasForm {

    @NotEmpty
    private List<String> segmentosClientes = new ArrayList<>();

    @NotBlank
    private String propuestaValor;

    @NotEmpty
    private List<String> canales = new ArrayList<>();

    @NotBlank
    private String relacionClientes;

    @NotEmpty
    private List<String> fuentesIngresos = new ArrayList<>();

    @NotEmpty
    private List<String> recursosClave = new ArrayList<>();

    @NotEmpty
    private List<String> actividadesClave = new ArrayList<>();

    @NotEmpty
    private List<String> sociosClave = new ArrayList<>();

    @NotEmpty
    private List<String> estructuraCostos = new ArrayList<>();

    public List<String> getSegmentosClientes() {
        return segmentosClientes;
    }

    public void setSegmentosClientes(List<String> segmentosClientes) {
        this.segmentosClientes = segmentosClientes;
    }

    public String getPropuestaValor() {
        return propuestaValor;
    }

    public void setPropuestaValor(String propuestaValor) {
        this.propuestaValor = propuestaValor;
    }

    public List<String> getCanales() {
        return canales;
    }

    public void setCanales(List<String> canales) {
        this.canales = canales;
    }

    public String getRelacionClientes() {
        return relacionClientes;
    }

    public void setRelacionClientes(String relacionClientes) {
        this.relacionClientes = relacionClientes;
    }

    public List<String> getFuentesIngresos() {
        return fuentesIngresos;
    }

    public void setFuentesIngresos(List<String> fuentesIngresos) {
        this.fuentesIngresos = fuentesIngresos;
    }

    public List<String> getRecursosClave() {
        return recursosClave;
    }

    public void setRecursosClave(List<String> recursosClave) {
        this.recursosClave = recursosClave;
    }

    public List<String> getActividadesClave() {
        return actividadesClave;
    }

    public void setActividadesClave(List<String> actividadesClave) {
        this.actividadesClave = actividadesClave;
    }

    public List<String> getSociosClave() {
        return sociosClave;
    }

    public void setSociosClave(List<String> sociosClave) {
        this.sociosClave = sociosClave;
    }

    public List<String> getEstructuraCostos() {
        return estructuraCostos;
    }

    public void setEstructuraCostos(List<String> estructuraCostos) {
        this.estructuraCostos = estructuraCostos;
    }
}
