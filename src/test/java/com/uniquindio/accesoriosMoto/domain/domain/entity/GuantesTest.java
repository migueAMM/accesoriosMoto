package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Guantes;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GuantesTest {

    private Guantes crear(Proteccion proteccion) {
        return Guantes.crear("g1", new Marca("m1", "Dainese"), new Precio(120000, "COP"),
                "Negro", Talla.M, Material.TEXTIL, proteccion);
    }

    @Test
    public void nuevosGuantesNacenNoPublicadosYConservanSusAtributos() {
        Guantes guantes = crear(Proteccion.NIVEL_1);

        assertFalse(guantes.estaPublicado());
        assertEquals(Talla.M, guantes.getTalla());
        assertEquals(Material.TEXTIL, guantes.getMaterial());
        assertEquals(Proteccion.NIVEL_1, guantes.getProteccion());
    }

    @Test
    public void rechazaGuantesSinProteccion() {
        assertThrows(ReglaDominioException.class, () -> crear(null));
    }
}