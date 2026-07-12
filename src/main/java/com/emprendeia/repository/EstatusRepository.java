package com.emprendeia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emprendeia.model.Estatus;

public interface EstatusRepository extends JpaRepository<Estatus, Long> {

    Optional<Estatus> findByNombreEstatus(String nombreEstatus);
}
