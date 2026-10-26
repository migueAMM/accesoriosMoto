package com.uniquindio.accesoriosMoto.infrastructure.rest.mapper;

import com.uniquindio.accesoriosMoto.domain.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol().name());
    }
}
