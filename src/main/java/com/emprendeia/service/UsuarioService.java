package com.emprendeia.service;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emprendeia.dto.RegistroForm;
import com.emprendeia.exception.CorreoYaRegistradoException;
import com.emprendeia.model.Estatus;
import com.emprendeia.model.Usuario;
import com.emprendeia.repository.EstatusRepository;
import com.emprendeia.repository.UsuarioRepository;

@Service
public class UsuarioService {

    /**
     * Requiere que el catálogo {@code estatus} tenga una fila con este valor
     * (precondición de datos: no hay script de seed en el repo todavía).
     */
    private static final String ESTATUS_ACTIVO = "ACTIVO";

    private final UsuarioRepository usuarioRepository;
    private final EstatusRepository estatusRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, EstatusRepository estatusRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.estatusRepository = estatusRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrar(RegistroForm form) {
        usuarioRepository.findByCorreo(form.getCorreo()).ifPresent(usuario -> {
            throw new CorreoYaRegistradoException("Ya existe una cuenta registrada con este correo.");
        });

        Estatus activo = estatusRepository.findByNombreEstatus(ESTATUS_ACTIVO)
                .orElseThrow(() -> new IllegalStateException(
                        "El catálogo estatus no tiene una fila con nombre_estatus = '" + ESTATUS_ACTIVO + "'."));

        Usuario usuario = new Usuario(
                form.getNombre(),
                form.getApellidoPaterno(),
                form.getApellidoMaterno(),
                form.getCorreo(),
                passwordEncoder.encode(form.getContrasena()),
                LocalDate.now(),
                activo);

        return usuarioRepository.save(usuario);
    }
}
