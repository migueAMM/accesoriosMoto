package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Intercomunicador;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntercomunicadorTest {

    @Test
    public void nuevoIntercomunicadorNaceNoPublicadoYConservaSusAtributos() {
        Intercomunicador intercom = Intercomunicador.crear("ic1", new Marca("m1", "Cardo"),
                new Precio(450000, "COP"), "Gris", 1000, 12, true);

        assertFalse(intercom.estaPublicado());
        assertEquals(1000, intercom.getAlcanceMetros());
        assertEquals(12, intercom.getAutonomiaHoras());
        assertTrue(intercom.isBluetooth());
    }
}