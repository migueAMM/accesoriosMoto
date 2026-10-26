package com.uniquindio.accesoriosMoto.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Email String correo,
        @NotBlank @Size(max=64) String contrasena) {

    public LoginRequest {
        correo = correo == null ? null : correo.strip();
    }
}
