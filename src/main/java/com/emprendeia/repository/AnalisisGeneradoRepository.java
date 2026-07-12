package com.emprendeia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emprendeia.model.AnalisisGenerado;
import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.TipoAnalisis;

public interface AnalisisGeneradoRepository extends JpaRepository<AnalisisGenerado, Long> {

    List<AnalisisGenerado> findByIdeaNegocio(IdeaNegocio ideaNegocio);

    Optional<AnalisisGenerado> findByIdeaNegocioAndTipoAnalisis(IdeaNegocio ideaNegocio, TipoAnalisis tipoAnalisis);
}
