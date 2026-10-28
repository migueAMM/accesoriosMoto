package com.uniquindio.accesoriosMoto.infrastructure.rest.mapper;

import com.uniquindio.accesoriosMoto.application.dto.response.ComentarioResponse;
import com.uniquindio.accesoriosMoto.domain.entity.Comentario;
import org.springframework.stereotype.Component;

@Component
public class ComentarioMapper {

    public ComentarioResponse toResponse(Comentario comentario) {
        return new ComentarioResponse(comentario.getId(), comentario.getProducto().getId(), comentario.getCalificacion(), comentario.getTexto());
    }
}
