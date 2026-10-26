package com.uniquindio.accesoriosMoto.application.dto.response;

public record UsuarioResponse(
        String id,
        String nombre,
        String correo,
        String rol
) {}

