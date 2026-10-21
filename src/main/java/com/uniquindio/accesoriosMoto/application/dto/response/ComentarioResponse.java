package com.uniquindio.accesoriosMoto.application.dto.response;

public record ComentarioResponse (
        String id,
        String productoId,
        int calificacion,
        String texto
){}
