package com.uniquindio.accesoriosMoto.infrastructure.config;

import com.uniquindio.accesoriosMoto.application.dto.request.RegistrarCompradorRequest;
import com.uniquindio.accesoriosMoto.application.port.CodificadorContrasena;
import com.uniquindio.accesoriosMoto.domain.entity.Administrador;
import com.uniquindio.accesoriosMoto.domain.repository.UsuarioRepository;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.UUID;

@Configuration
@ConditionalOnProperty(name="security.admin-inicial.enabled", havingValue="true")
public class AdministradorInicialConfig {
    @Bean
    public ApplicationRunner administradorInicial(UsuarioRepository usuarios, CodificadorContrasena contrasenas,
                                                  Validator validator, @Value("${security.admin-inicial.nombre}") String nombre,
                                                  @Value("${security.admin-inicial.correo}") String correo,
                                                  @Value("${security.admin-inicial.contrasena}") String contrasena) {
        return args -> {
            String normalizado = correo.strip().toLowerCase(Locale.ROOT);
            var existente = usuarios.obtenerPorCorreo(normalizado);
            if (existente.isPresent()) {
                if (!(existente.get() instanceof Administrador)) {
                    throw new IllegalStateException("El correo configurado para administrador ya pertenece a un comprador");
                }
                return;
            }
            if (!validator.validate(new RegistrarCompradorRequest(nombre, normalizado, contrasena)).isEmpty()) {
                throw new IllegalStateException("Datos inválidos para el administrador inicial");
            }
            usuarios.guardar(new Administrador(UUID.randomUUID().toString(), nombre, normalizado, contrasenas.codificar(contrasena)));
        };
    }
}
