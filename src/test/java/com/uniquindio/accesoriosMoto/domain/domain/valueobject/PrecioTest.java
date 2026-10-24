package com.uniquindio.accesoriosMoto.domain.domain.valueobject;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class PrecioTest {

    @Test
    void rechazaMontosNoFinitos() {
        for (double monto : new double[]{Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            assertThrows(ReglaDominioException.class, () -> new Precio(monto, "COP"));
        }
    }

    @Test
    void rechazaMonedaNulaVaciaOEnBlanco() {
        for (String moneda : new String[]{null, "", " "}) {
            assertThrows(ReglaDominioException.class, () -> new Precio(100, moneda));
        }
    }

    @Test
    void multiplicaConservandoMonedaSinModificarOriginal() {
        Precio original = new Precio(12.5, "USD");

        Precio resultado = original.multiplicarPor(3);

        assertEquals(new Precio(37.5, "USD"), resultado);
        assertEquals(new Precio(12.5, "USD"), original);
        assertNotSame(original, original.multiplicarPor(1));
    }

    @Test
    void rechazaMultiplicacionPorCantidadNoPositiva() {
        Precio precio = new Precio(100, "COP");
        assertThrows(ReglaDominioException.class, () -> precio.multiplicarPor(0));
        assertThrows(ReglaDominioException.class, () -> precio.multiplicarPor(-1));
    }

    @Test
    void rechazaDesbordamientoEnMultiplicacion() {
        Precio precio = new Precio(Double.MAX_VALUE, "COP");
        assertThrows(ReglaDominioException.class, () -> precio.multiplicarPor(2));
    }

    @Test
    void sumaConservandoMonedaYOperandos() {
        Precio primero = new Precio(10.5, "USD");
        Precio segundo = new Precio(2.5, "USD");

        Precio resultado = primero.sumar(segundo);

        assertEquals(new Precio(13, "USD"), resultado);
        assertEquals(new Precio(10.5, "USD"), primero);
        assertEquals(new Precio(2.5, "USD"), segundo);
        assertNotSame(primero, resultado);
    }

    @Test
    void rechazaSumaNulaOMonedasDistintas() {
        Precio precio = new Precio(100, "COP");
        assertThrows(ReglaDominioException.class, () -> precio.sumar(null));
        assertThrows(ReglaDominioException.class, () -> precio.sumar(new Precio(100, "USD")));
    }

    @Test
    void rechazaDesbordamientoEnSuma() {
        Precio precio = new Precio(Double.MAX_VALUE, "COP");
        assertThrows(ReglaDominioException.class, () -> precio.sumar(precio));
    }

    @Test
    void unPrecioPositivoConservaSuMonto() {
        org.junit.jupiter.api.Assertions.assertEquals(100, new Precio(100, "COP").monto());
    }

    @Test
    void unPrecioNegativoLanzaReglaDominioException() {
        assertThrows(ReglaDominioException.class, () -> new Precio(-100, "COP"));
    }

    @Test
    void unPrecioEnCeroLanzaReglaDominioException() {
        assertThrows(ReglaDominioException.class, () -> new Precio(0, "COP"));
    }

}
