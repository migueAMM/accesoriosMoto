package com.uniquindio.accesoriosMoto.domain.valueObject;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;

public record Precio(double monto, String moneda) {

    public Precio {
        if (!Double.isFinite(monto) || monto <= 0) {
            throw new ReglaDominioException("El precio debe ser finito y mayor a cero");
        }
        if (moneda == null || moneda.isBlank()) {
            throw new ReglaDominioException("El precio debe indicar una moneda");
        }
    }

    public Precio multiplicarPor(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaDominioException("La cantidad debe ser mayor a cero");
        }
        return new Precio(monto * cantidad, moneda);
    }

    public Precio sumar(Precio otro) {
        if (otro == null) {
            throw new ReglaDominioException("Debe indicar el precio que desea sumar");
        }
        if (!moneda.equals(otro.moneda())) {
            throw new ReglaDominioException("Solo se pueden sumar precios de la misma moneda");
        }
        return new Precio(monto + otro.monto(), moneda);
    }


    public Precio conProteccion(Proteccion proteccion) {
        return new Precio(monto * proteccion.getFactorProteccion(), moneda);
    }

    public boolean esNegativoOCero() {
        return monto <= 0;
    }
}
