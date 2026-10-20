package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.Compatibilidad;

import java.util.Set;

public interface ProductoCompatible {
    void indicarCompatibilidad(Compatibilidad compatibilidad);
    void retirarCompatibilidad(Compatibilidad compatibilidad);
    Set<Compatibilidad> getCompatibilidades();
}
