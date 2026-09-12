package com.uniquindio.accesoriosMoto.domain.valueObject;

public enum Proteccion {
    NIVEL_1(1),
    NIVEL_2(3),
    NIVEL_3(5),
    NIVEL_4(7),
    NIVEL_5(9);

    private final int factorProteccion;

    Proteccion(int factorProteccion) {
        this.factorProteccion = factorProteccion;
    }

    public int getFactorProteccion() {
        return factorProteccion;
    }
}
