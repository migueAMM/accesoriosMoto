package com.uniquindio.accesoriosMoto.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SolicitarReembolsoRequest (
        @NotBlank(message = "Debe indicar el motivo del reembolso") String motivo
){}
