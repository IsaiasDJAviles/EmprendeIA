package com.emprendeia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emprendeia.model.Pais;

public interface PaisRepository extends JpaRepository<Pais, Long> {

    Optional<Pais> findByNombrePais(String nombrePais);
}
