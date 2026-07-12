package com.emprendeia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.Reporte;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    Optional<Reporte> findByIdeaNegocio(IdeaNegocio ideaNegocio);
}
