package com.uniquindio.accesoriosMoto.domain.infraestructure.rest.mapper;

import com.uniquindio.accesoriosMoto.application.dto.response.ComentarioResponse;
import com.uniquindio.accesoriosMoto.domain.entity.Comentario;
import com.uniquindio.accesoriosMoto.domain.infraestructure.rest.DatosRestPrueba;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.ComentarioMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComentarioMapperTest {

    @Test
    void mapeaComentarioSinExponerCompradorCompleto() {
        Comentario comentario = DatosRestPrueba.comentario("c1");

        ComentarioResponse response = new ComentarioMapper().toResponse(comentario);

        assertEquals(new ComentarioResponse("c1", "p1", 5, "Excelente"), response);
        assertEquals("u1", comentario.getComprador().getId());
        assertEquals("Excelente", comentario.getTexto());
    }
}
