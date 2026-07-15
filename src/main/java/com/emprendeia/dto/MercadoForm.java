package com.emprendeia.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

/**
 * Interpretación de viabilidad del LLM sobre cifras ya calculadas en Java (RF-13);
 * ver decisión documentada en {@link com.emprendeia.ia.PromptBuilder}. Los números en sí
 * viven en {@link com.emprendeia.model.ModuloFinanciero}, no en este formulario.
 */
public class MercadoForm {

    @NotBlank
    private String interpretacionGeneral;

    @NotEmpty
    private List<String> riesgosFinancieros = new ArrayList<>();

    @NotEmpty
    private List<String> recomendacionesViabilidad = new ArrayList<>();

    public String getInterpretacionGeneral() {
        return interpretacionGeneral;
    }

    public void setInterpretacionGeneral(String interpretacionGeneral) {
        this.interpretacionGeneral = interpretacionGeneral;
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
