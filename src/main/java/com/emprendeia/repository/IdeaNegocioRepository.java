package com.emprendeia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.Usuario;

public interface IdeaNegocioRepository extends JpaRepository<IdeaNegocio, Long> {

    List<IdeaNegocio> findByUsuario(Usuario usuario);
}
