package com.uniquindio.accesoriosMoto.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarCompradorRequest(
        @NotBlank String nombre,
        @NotBlank @Email String correo,
        @NotBlank @Size(min=8, max=64) String contrasena) {

    public RegistrarCompradorRequest {
        correo = correo == null ? null : correo.strip();
    }
}
