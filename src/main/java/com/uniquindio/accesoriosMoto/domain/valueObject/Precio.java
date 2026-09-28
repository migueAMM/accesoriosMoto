package com.uniquindio.accesoriosMoto.domain.valueObject;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;

public record Precio(double monto, String moneda) {
    public Precio {
        if (esNegativoOCero()) {
            throw new ReglaDominioException("El precio debe ser mayor a cero");
        }
    }

    public Precio conProteccion(Proteccion proteccion) {
        return new Precio(monto * proteccion.getFactorProteccion(), moneda);
    }

    public boolean esNegativoOCero() {
        return monto <= 0;
    }
}
