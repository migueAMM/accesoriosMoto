package com.uniquindio.accesoriosMoto.domain.domain.valueobject;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class PrecioTest {

    @Test
    public void rechazaMontosNoFinitos() {

        var entradas1 = new double[]{Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};

        var acciones = new ArrayList<Executable>();
        for (double monto : entradas1) {
            acciones.add(() -> new Precio(monto, "COP"));
        }

        for (var accion : acciones) {
            assertThrows(ReglaDominioException.class, accion);
        }
    }

    @Test
    public void rechazaMonedaNulaVaciaOEnBlanco() {

        var entradas1 = new String[]{null, "", " "};

        var acciones = new ArrayList<Executable>();
        for (String moneda : entradas1) {
            acciones.add(() -> new Precio(100, moneda));
        }

        for (var accion : acciones) {
            assertThrows(ReglaDominioException.class, accion);
        }
    }

    @Test
    public void multiplicaConservandoMonedaSinModificarOriginal() {

        Precio original = new Precio(12.5, "USD");

        Precio resultado = original.multiplicarPor(3);
        var resultado1 = original.multiplicarPor(1);

        assertEquals(new Precio(37.5, "USD"), resultado);
        assertEquals(new Precio(12.5, "USD"), original);
        assertNotSame(original, resultado1);
    }

    @Test
    public void rechazaMultiplicacionPorCantidadNoPositiva() {

        Precio precio = new Precio(100, "COP");

        var accion1Error = catchThrowable(() -> precio.multiplicarPor(0));
        var accion2Error = catchThrowable(() -> precio.multiplicarPor(-1));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        assertInstanceOf(ReglaDominioException.class, accion2Error);
    }

    @Test
    public void rechazaDesbordamientoEnMultiplicacion() {

        Precio precio = new Precio(Double.MAX_VALUE, "COP");

        var accion1Error = catchThrowable(() -> precio.multiplicarPor(2));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
    }

    @Test
    public void sumaConservandoMonedaYOperandos() {

        Precio primero = new Precio(10.5, "USD");
        Precio segundo = new Precio(2.5, "USD");

        Precio resultado = primero.sumar(segundo);

        assertEquals(new Precio(13, "USD"), resultado);
        assertEquals(new Precio(10.5, "USD"), primero);
        assertEquals(new Precio(2.5, "USD"), segundo);
        assertNotSame(primero, resultado);
    }

    @Test
    public void rechazaSumaNulaOMonedasDistintas() {

        Precio precio = new Precio(100, "COP");

        var accion1Error = catchThrowable(() -> precio.sumar(null));
        var accion2Error = catchThrowable(() -> precio.sumar(new Precio(100, "USD")));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        assertInstanceOf(ReglaDominioException.class, accion2Error);
    }

    @Test
    public void rechazaDesbordamientoEnSuma() {

        Precio precio = new Precio(Double.MAX_VALUE, "COP");

        var accion1Error = catchThrowable(() -> precio.sumar(precio));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
    }

    @Test
    void unPrecioPositivoConservaSuMonto() {

        Precio precio = new Precio(100, "COP");

        double monto = precio.monto();

        assertEquals(100, monto);
    }

    @Test
    void unPrecioNegativoLanzaReglaDominioException() {

        double monto = -100;
        String moneda = "COP";

        var error = catchThrowable(() -> new Precio(monto, moneda));

        assertInstanceOf(ReglaDominioException.class, error);
    }

    @Test
    void unPrecioEnCeroLanzaReglaDominioException() {

        double monto = 0;
        String moneda = "COP";

        var error = catchThrowable(() -> new Precio(monto, moneda));

        assertInstanceOf(ReglaDominioException.class, error);
    }

}
