package com.emprendeia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emprendeia.model.Giro;
import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.IdeaNegocioGiro;

public interface IdeaNegocioGiroRepository extends JpaRepository<IdeaNegocioGiro, Long> {

    List<IdeaNegocioGiro> findByIdeaNegocio(IdeaNegocio ideaNegocio);

    Optional<IdeaNegocioGiro> findByIdeaNegocioAndGiro(IdeaNegocio ideaNegocio, Giro giro);
}
