package com.uniquindio.accesoriosMoto.domain.valueObject;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;

public record Precio(double monto, String moneda) {
    public Precio {
        if (monto < 0) {
            throw new ReglaDominioException("El precio no puede ser negativo");
        }
    }

    public Precio conProteccion(Proteccion proteccion) {
        return new Precio(monto * proteccion.factorProteccion(), moneda);
    }


}
