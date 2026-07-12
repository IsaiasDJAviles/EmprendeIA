package com.emprendeia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.ModuloFinanciero;

public interface ModuloFinancieroRepository extends JpaRepository<ModuloFinanciero, Long> {

    Optional<ModuloFinanciero> findByIdeaNegocio(IdeaNegocio ideaNegocio);
}
