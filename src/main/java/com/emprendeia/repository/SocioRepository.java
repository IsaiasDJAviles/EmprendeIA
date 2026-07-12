package com.emprendeia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.Socio;

public interface SocioRepository extends JpaRepository<Socio, Long> {

    List<Socio> findByIdeaNegocio(IdeaNegocio ideaNegocio);
}
