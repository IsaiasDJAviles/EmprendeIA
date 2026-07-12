package com.emprendeia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emprendeia.model.EstadoReporte;

public interface EstadoReporteRepository extends JpaRepository<EstadoReporte, Long> {

    Optional<EstadoReporte> findByEstadoReporte(String estadoReporte);
}
