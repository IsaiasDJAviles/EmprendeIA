package com.emprendeia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.emprendeia.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * JOIN FETCH de estatus: UsuarioPrincipal.isEnabled() lo necesita fuera de sesión
     * de Hibernate (se evalúa después de que UsuarioDetailsService retorna), así que
     * no puede quedar como proxy LAZY sin inicializar.
     */
    @Query("SELECT u FROM Usuario u JOIN FETCH u.estatus WHERE u.correo = :correo")
    Optional<Usuario> findByCorreo(@Param("correo") String correo);
}
