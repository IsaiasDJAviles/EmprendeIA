package com.emprendeia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emprendeia.model.Estado;
import com.emprendeia.model.Pais;

public interface EstadoRepository extends JpaRepository<Estado, Long> {

    List<Estado> findByPais(Pais pais);

    Optional<Estado> findByNombreEstadoAndPais(String nombreEstado, Pais pais);

    List<Estado> findAllByOrderByNombreEstadoAsc();
}
