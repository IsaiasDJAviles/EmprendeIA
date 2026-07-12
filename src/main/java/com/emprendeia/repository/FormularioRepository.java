package com.emprendeia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emprendeia.model.Formulario;
import com.emprendeia.model.IdeaNegocio;

public interface FormularioRepository extends JpaRepository<Formulario, Long> {

    Optional<Formulario> findByIdeaNegocio(IdeaNegocio ideaNegocio);
}
