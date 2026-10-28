package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Chaqueta;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChaquetaTest {

    private Chaqueta crear(Proteccion proteccion) {
        return Chaqueta.crear("ch1", new Marca("m1", "Alpinestars"), new Precio(300000, "COP"),
                "Negro", Talla.L, Material.CUERO, proteccion);
    }

    @Test
    public void nuevaChaquetaNaceNoPublicadaYConservaSusAtributos() {
        Chaqueta chaqueta = crear(Proteccion.NIVEL_2);

        assertFalse(chaqueta.estaPublicado());
        assertEquals(Talla.L, chaqueta.getTalla());
        assertEquals(Material.CUERO, chaqueta.getMaterial());
        assertEquals(Proteccion.NIVEL_2, chaqueta.getProteccion());
    }

    @Test
    public void rechazaChaquetaSinProteccion() {
        var error = assertThrows(ReglaDominioException.class, () -> crear(null));

        assertEquals("Se debe especificar la protección de la chaqueta", error.getMessage());
    }
}