package com.emprendeia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emprendeia.model.Giro;

public interface GiroRepository extends JpaRepository<Giro, Long> {

    Optional<Giro> findByTipoGiro(String tipoGiro);
}
