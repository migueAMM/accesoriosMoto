package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Casco;
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

    @Test
    void rechazaIdentificadorInvalido() {
        for (String id : new String[]{null, "", " "}) {
            assertThrows(ReglaDominioException.class, () -> Casco.crear(id, new Marca("m1", "AGV"), "K-3", "Negro", Talla.M, new Precio(100, "COP"), Proteccion.NIVEL_1, "DOT"));
        }
    }

    @Test
    void rechazaMarcaNula() {
        assertThrows(ReglaDominioException.class, () -> Casco.crear("p1", null, "K-3", "Negro", Talla.M, new Precio(100, "COP"), Proteccion.NIVEL_1, "DOT"));
    }

    @Test
    void rechazaPrecioNulo() {
        assertThrows(ReglaDominioException.class, () -> Casco.crear("p1", new Marca("m1", "AGV"), "K-3", "Negro", Talla.M, null, Proteccion.NIVEL_1, "DOT"));
    }

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
