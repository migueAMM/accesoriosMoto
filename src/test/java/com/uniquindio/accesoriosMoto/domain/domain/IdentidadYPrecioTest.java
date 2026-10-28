package com.uniquindio.accesoriosMoto.domain.domain;

import com.uniquindio.accesoriosMoto.domain.entity.Casco;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.entity.Visor;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.domain.valueObject.Proteccion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Talla;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class IdentidadYPrecioTest {
    private Casco casco(String id, double monto) {
        return Casco.crear(id,new Marca("m","AGV"),"K3","Negro", Talla.M,new Precio(monto,"COP"), Proteccion.NIVEL_1,"DOT");
    }

    @Test
    public void productoSeIdentificaPorClaseEIdIndependienteDeSusAtributos() {
        Producto a=casco("p",100),b=casco("p",200);
        b.publicar();

        var resultado2 = casco("otro",100);
        var resultado3 = Visor.crear("p",new Marca("m","AGV"),new Precio(100,"COP"),"Negro","Integral", List.of());

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, resultado2);
        assertNotEquals(a, resultado3);
        assertNotEquals(a, null);
    }

    @Test
    public void precioTieneIgualdadPorMontoYMoneda() {

        var resultado1 = new Precio(100,"COP");
        var resultado2 = new Precio(100,"COP").hashCode();
        var resultado3 = new Precio(101,"COP");
        var resultado4 = new Precio(100,"USD");

        assertEquals(new Precio(100,"COP"), resultado1);
        assertEquals(new Precio(100,"COP").hashCode(), resultado2);
        assertNotEquals(new Precio(100,"COP"), resultado3);
        assertNotEquals(new Precio(100,"COP"), resultado4);
    }

    @Test
    public void proteccionAplicaFactorConservandoMonedaYPrecioOriginal() {
        Precio original = new Precio(100,"COP");
        Proteccion[] protecciones = Proteccion.values();

        var resultados = java.util.Arrays.stream(protecciones).map(original::conProteccion).toList();

        for (int indice = 0; indice < protecciones.length; indice++) {
            Precio resultado = resultados.get(indice);
            assertEquals(100 * protecciones[indice].getFactorProteccion(), resultado.monto());
            assertEquals("COP", resultado.moneda());
        }
        assertEquals(new Precio(100,"COP"),original);
    }
}
