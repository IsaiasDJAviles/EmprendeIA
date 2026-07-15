package com.emprendeia.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class MarketingForm {

    @NotBlank
    private String publicoObjetivo;

    @NotBlank
    private String mensajePrincipal;

    @NotEmpty
    private List<String> canalesPromocion = new ArrayList<>();

    @NotEmpty
    private List<String> estrategiasDigitales = new ArrayList<>();

    @NotEmpty
    private List<String> promocionesIniciales = new ArrayList<>();

    @NotEmpty
    private List<String> calendarioBasico = new ArrayList<>();

    @NotEmpty
    private List<String> indicadoresResultados = new ArrayList<>();

    public String getPublicoObjetivo() {
        return publicoObjetivo;
    }

    public void setPublicoObjetivo(String publicoObjetivo) {
        this.publicoObjetivo = publicoObjetivo;
    }

    public String getMensajePrincipal() {
        return mensajePrincipal;
    }

    public void setMensajePrincipal(String mensajePrincipal) {
        this.mensajePrincipal = mensajePrincipal;
    }

    public List<String> getCanalesPromocion() {
        return canalesPromocion;
    }

    public void setCanalesPromocion(List<String> canalesPromocion) {
        this.canalesPromocion = canalesPromocion;
    }

    public List<String> getEstrategiasDigitales() {
        return estrategiasDigitales;
    }

    public void setEstrategiasDigitales(List<String> estrategiasDigitales) {
        this.estrategiasDigitales = estrategiasDigitales;
    }

    public List<String> getPromocionesIniciales() {
        return promocionesIniciales;
    }

    public void setPromocionesIniciales(List<String> promocionesIniciales) {
        this.promocionesIniciales = promocionesIniciales;
    }

    public List<String> getCalendarioBasico() {
        return calendarioBasico;
    }

    public void setCalendarioBasico(List<String> calendarioBasico) {
        this.calendarioBasico = calendarioBasico;
    }

    public List<String> getIndicadoresResultados() {
        return indicadoresResultados;
    }

    public void setIndicadoresResultados(List<String> indicadoresResultados) {
        this.indicadoresResultados = indicadoresResultados;
    }
}
