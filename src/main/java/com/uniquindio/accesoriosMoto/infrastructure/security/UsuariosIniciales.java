package com.uniquindio.accesoriosMoto.infrastructure.security;

import com.uniquindio.accesoriosMoto.application.dto.request.RegistrarCompradorRequest;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.AdministradorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.CompradorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.RolSeguridad;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.UsuarioEntity;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name="security.admin-inicial.enabled", havingValue="true")
public class UsuariosIniciales implements CommandLineRunner {
    private record Semilla(String id, String correo, String clave, RolSeguridad rol) {}

    private final UsuarioJpaDataRepository repository;
    private final PasswordEncoder encoder;
    private final Validator validator;

    @Value("${security.admin-inicial.nombre}") private String nombre;
    @Value("${security.admin-inicial.correo}") private String correo;
    @Value("${security.admin-inicial.contrasena}") private String contrasena;

    @Override
    public void run(String... args) {
        List<Semilla> semillas = List.of(new Semilla(UUID.randomUUID().toString(),
                correo.strip().toLowerCase(Locale.ROOT), contrasena, RolSeguridad.ADMINISTRADOR));
        for (Semilla semilla : semillas) {
            var existente = repository.findByCorreo(semilla.correo());
            if (existente.isPresent()) {
                if (existente.get().getRol() != semilla.rol())
                    throw new IllegalStateException("El correo configurado para administrador ya pertenece a un comprador");
                continue;
            }
            if (!validator.validate(new RegistrarCompradorRequest(nombre, semilla.correo(), semilla.clave())).isEmpty())
                throw new IllegalStateException("Datos inválidos para el administrador inicial");
            UsuarioEntity usuario = semilla.rol() == RolSeguridad.ADMINISTRADOR ? new AdministradorEntity() : new CompradorEntity();
            usuario.setId(semilla.id());
            usuario.setNombre(nombre);
            usuario.setCorreo(semilla.correo());
            usuario.setContrasena(encoder.encode(semilla.clave()));
            repository.save(usuario);
        }
    }
}
