package com.emprendeia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emprendeia.model.TipoCapital;

public interface TipoCapitalRepository extends JpaRepository<TipoCapital, Long> {

    Optional<TipoCapital> findByTipoCapital(String tipoCapital);
}
