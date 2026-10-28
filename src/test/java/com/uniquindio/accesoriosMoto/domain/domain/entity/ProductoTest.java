package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Casco;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.domain.valueObject.Proteccion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Talla;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductoTest {

    @Test
    public void rechazaIdentificadorInvalido() {

        var entradas1 = new String[]{null, "", " "};

        var acciones = new java.util.ArrayList<org.junit.jupiter.api.function.Executable>();
        for (String id : entradas1) {
            acciones.add(() -> Casco.crear(id, new Marca("m1", "AGV"), "K-3", "Negro", Talla.M, new Precio(100, "COP"), Proteccion.NIVEL_1, "DOT"));
        }

        for (var accion : acciones) {
            assertThrows(ReglaDominioException.class, accion);
        }
    }

    @Test
    public void rechazaMarcaNula() {

        var accion1Error = catchThrowable(() -> Casco.crear("p1", null, "K-3", "Negro", Talla.M, new Precio(100, "COP"), Proteccion.NIVEL_1, "DOT"));


        assertInstanceOf(ReglaDominioException.class, accion1Error);    }

    @Test
    public void rechazaPrecioNulo() {

        var accion1Error = catchThrowable(() -> Casco.crear("p1", new Marca("m1", "AGV"), "K-3", "Negro", Talla.M, null, Proteccion.NIVEL_1, "DOT"));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
    }

    private Casco cascoPrueba(){
        return Casco.crear("P3", new Marca("M3", "AGV"), "K-3", "Negro con azul", Talla.M, new Precio(500000, "COP"), Proteccion.NIVEL_3, "DOT");
    }

    @Test
    public void noSePuedePublicarUnProductoQueYaEstaPublicado(){

        Casco casco = cascoPrueba();
        casco.publicar();

        var accion1Error = catchThrowable(casco ::publicar);

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        assertTrue(casco.estaPublicado());
    }

    @Test
    public void noSePuedeDespublicarUnProductoQueYaEstaDespublicado(){

        Casco casco = cascoPrueba();

        var accion1Error = catchThrowable(casco::despublicar);

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        assertFalse(casco.estaPublicado());
    }
}
