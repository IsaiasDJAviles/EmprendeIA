package com.emprendeia.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

/**
 * Pese al nombre del módulo ({@link com.emprendeia.model.TipoAnalisis#MERCADO}), el contenido
 * es la interpretación financiera del {@link com.emprendeia.model.Formulario} (ver decisión
 * documentada en {@link com.emprendeia.ia.PromptBuilder}), no un análisis de mercado.
 */
public class MercadoForm {

    @NotBlank
    private String ingresosEstimados;

    @NotBlank
    private String costosPrincipales;

    @NotBlank
    private String utilidadAproximada;

    @NotBlank
    private String puntoEquilibrio;

    @NotEmpty
    private List<String> riesgosFinancieros = new ArrayList<>();

    @NotEmpty
    private List<String> recomendacionesViabilidad = new ArrayList<>();

    public String getIngresosEstimados() {
        return ingresosEstimados;
    }

    public void setIngresosEstimados(String ingresosEstimados) {
        this.ingresosEstimados = ingresosEstimados;
    }

    public String getCostosPrincipales() {
        return costosPrincipales;
    }

    public void setCostosPrincipales(String costosPrincipales) {
        this.costosPrincipales = costosPrincipales;
    }

    public String getUtilidadAproximada() {
        return utilidadAproximada;
    }

    public void setUtilidadAproximada(String utilidadAproximada) {
        this.utilidadAproximada = utilidadAproximada;
    }

    public String getPuntoEquilibrio() {
        return puntoEquilibrio;
    }

    public void setPuntoEquilibrio(String puntoEquilibrio) {
        this.puntoEquilibrio = puntoEquilibrio;
    }

    public List<String> getRiesgosFinancieros() {
        return riesgosFinancieros;
    }

    public void setRiesgosFinancieros(List<String> riesgosFinancieros) {
        this.riesgosFinancieros = riesgosFinancieros;
    }

    public List<String> getRecomendacionesViabilidad() {
        return recomendacionesViabilidad;
    }

    public void setRecomendacionesViabilidad(List<String> recomendacionesViabilidad) {
        this.recomendacionesViabilidad = recomendacionesViabilidad;
    }
}
