package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.domain.valueObject.Proteccion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Talla;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductoTest {

    private Casco cascoPrueba(){
        return Casco.crear("P3", new Marca("M3", "AGV"), "K-3", "Negro con azul", Talla.M, new Precio(500000, "COP"), Proteccion.NIVEL_3, "DOT");
    }

    @Test
    public void noSePuedePublicarUnProductoQueYaEstaPublicado(){
        Casco casco = cascoPrueba();
        casco.publicar();

        assertThrows(ReglaDominioException.class, casco ::publicar);
        assertTrue(casco.estaPublicado());
    }

    @Test
    void noSePuedeDespublicarUnProductoQueYaEstaDespublicado(){
        Casco casco = cascoPrueba();

        assertThrows(ReglaDominioException.class, casco::despublicar);
        assertFalse(casco.estaPublicado());
    }
}
