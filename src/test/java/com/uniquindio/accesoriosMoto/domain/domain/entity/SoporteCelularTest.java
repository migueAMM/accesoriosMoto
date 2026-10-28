package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.SoporteCelular;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SoporteCelularTest {

    @Test
    public void nuevoSoporteNaceNoPublicadoYConservaElTipoDeSujecion() {
        SoporteCelular soporte = SoporteCelular.crear("s1", new Marca("m1", "Quad Lock"),
                new Precio(60000, "COP"), "Negro", "Manillar");

        assertFalse(soporte.estaPublicado());
        assertEquals("Manillar", soporte.getTipoSujecion());
    }
}