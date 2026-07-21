package com.emprendeia.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.emprendeia.model.IdeaNegocio;
import com.emprendeia.model.Usuario;

public interface IdeaNegocioRepository extends JpaRepository<IdeaNegocio, Long> {

    /**
     * Ideas activas (no eliminadas por soft delete) de un usuario, filtradas por coincidencia
     * parcial de nombre (insensible a mayúsculas). {@code patron} vacío ("") equivale a "%%",
     * que no filtra nada (ver {@code IdeaService#listarPorUsuario}) — se evita a propósito pasar
     * {@code null} aquí: Postgres no puede inferir el tipo del parámetro dentro de
     * {@code LOWER(CONCAT(...))} cuando el valor es nulo y falla con
     * "function lower(bytea) does not exist". El orden lo resuelve Spring Data a partir del
     * {@link Sort} recibido.
     */
    @Query("SELECT i FROM IdeaNegocio i WHERE i.usuario = :usuario AND i.estatus.nombreEstatus = :estatusActivo "
            + "AND LOWER(i.nombreNegocio) LIKE LOWER(CONCAT('%', :patron, '%'))")
    List<IdeaNegocio> buscarActivasPorUsuario(@Param("usuario") Usuario usuario,
            @Param("estatusActivo") String estatusActivo, @Param("patron") String patron, Sort sort);
}
