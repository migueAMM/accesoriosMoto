package com.uniquindio.accesoriosMoto.domain.valueObject;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;

public record Marca(String id, String nombre) {
    public Marca {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaDominioException("El nombre de la marca no puede estar vacío ni ser nulo");
        }
    }
}
