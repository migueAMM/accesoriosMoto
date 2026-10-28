package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RegistrarCompradorUseCase {

    private final UsuarioRepository usuarios;

    @Transactional
    public Comprador ejecutar(String id,String nombre,String correo,String hashContrasena) {
        if(nombre==null||nombre.isBlank()||correo==null||correo.isBlank()||hashContrasena==null||hashContrasena.isBlank())
            throw new ReglaDominioException("Datos de registro inválidos");
        String normalizado=correo.strip().toLowerCase(Locale.ROOT);
        if(usuarios.obtenerPorCorreo(normalizado).isPresent()) throw new DuplicateKeyException("El correo ya está registrado");
        var comprador=new Comprador(id,nombre,normalizado,hashContrasena);
        usuarios.guardar(comprador);
        return comprador;
    }
}
