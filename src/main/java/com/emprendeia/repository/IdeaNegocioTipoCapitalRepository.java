package com.emprendeia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.IdeaNegocioTipoCapital;
import com.emprendeia.model.TipoCapital;

public interface IdeaNegocioTipoCapitalRepository extends JpaRepository<IdeaNegocioTipoCapital, Long> {

    List<IdeaNegocioTipoCapital> findByIdeaNegocio(IdeaNegocio ideaNegocio);

    Optional<IdeaNegocioTipoCapital> findByIdeaNegocioAndTipoCapital(IdeaNegocio ideaNegocio, TipoCapital tipoCapital);
}
