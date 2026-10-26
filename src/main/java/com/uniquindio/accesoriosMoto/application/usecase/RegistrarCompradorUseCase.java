package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.application.exception.CorreoDuplicadoException;
import com.uniquindio.accesoriosMoto.application.port.CodificadorContrasena;
import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional
public class RegistrarCompradorUseCase {
    private final UsuarioRepository usuarios;
    private final CodificadorContrasena contrasenas;

    public RegistrarCompradorUseCase(UsuarioRepository usuarios, CodificadorContrasena contrasenas) {
        this.usuarios = usuarios;
        this.contrasenas = contrasenas;
    }

    public Comprador ejecutar(String id, String nombre, String correo, String contrasena) {
        if (nombre == null || nombre.isBlank() || correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank() || contrasena.length() < 8 || contrasena.length() > 64) {
            throw new ReglaDominioException("Datos de registro inválidos");
        }
        String normalizado = correo.strip().toLowerCase(Locale.ROOT);
        if (usuarios.obtenerPorCorreo(normalizado).isPresent()) throw new CorreoDuplicadoException();
        Comprador comprador = new Comprador(id, nombre, normalizado, contrasenas.codificar(contrasena));
        usuarios.guardar(comprador);
        return comprador;
    }
}
