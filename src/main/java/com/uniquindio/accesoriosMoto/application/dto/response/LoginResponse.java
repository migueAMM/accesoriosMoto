package com.uniquindio.accesoriosMoto.application.dto.response;

public record LoginResponse(
        String token,
        String tipo,
        String usuarioId,
        String rol
)
{}

