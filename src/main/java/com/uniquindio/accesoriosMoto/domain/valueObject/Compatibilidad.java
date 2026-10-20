package com.uniquindio.accesoriosMoto.domain.valueObject;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;

import java.util.Locale;

public record Compatibilidad(TipoCompatibilidad tipo, String modelo) {
    public Compatibilidad {
        if(tipo == null || modelo == null || modelo.isBlank()) {
            throw new ReglaDominioException("La compatibilidad debe indicar un tipo y un modelo valido");
        }
        modelo = modelo.strip().toUpperCase(Locale.ROOT);
    }
}
