package com.uniquindio.accesoriosMoto.domain.domain.valueobject;

import com.uniquindio.accesoriosMoto.domain.entity.Baul;
import com.uniquindio.accesoriosMoto.domain.entity.Guantes;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.entity.Visor;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FiltroProductoTest {

    private Visor visor(double monto, String moneda) {
        Visor producto = Visor.crear("v1", new Marca("m1", "AGV"), new Precio(monto, moneda), "Negro", "Integral", List.of("K-3"));
        producto.publicar();
        return producto;
    }

    @Test
    public void sinFiltrosSoloCoincidenPublicados() {

        FiltroProducto filtro = new FiltroProducto(null, null, null, null, null, null);
        Visor producto = visor(100, "COP");

        var resultado1 = filtro.coincideCon(producto);
        producto.despublicar();
        var resultado2 = filtro.coincideCon(producto);
        var resultado3 = filtro.coincideCon(null);

        assertTrue(resultado1);
        assertFalse(resultado2);
        assertFalse(resultado3);
    }

    @Test
    public void marcaYColorSonExactosIgnorandoMayusculasYEspaciosExteriores() {

        Visor producto = visor(100, "COP");

        var resultado1 = new FiltroProducto(" agv ", null, null, null, null, null).coincideCon(producto);
        var resultado2 = new FiltroProducto(null, " negro ", null, null, null, null).coincideCon(producto);
        var resultado3 = new FiltroProducto("AG", null, null, null, null, null).coincideCon(producto);
        var resultado4 = new FiltroProducto(null, "Blanco", null, null, null, null).coincideCon(producto);
        var resultado5 = new FiltroProducto(" ", " ", null, null, " ", null).coincideCon(producto);

        assertTrue(resultado1);
        assertTrue(resultado2);
        assertFalse(resultado3);
        assertFalse(resultado4);
        assertTrue(resultado5);
    }

    @Test
    void precioIncluyeAmbosLimitesYExigeMismaMoneda() {

        FiltroProducto filtro = new FiltroProducto(null, null, 100.0, 200.0, " cop ", null);

        var resultado1 = filtro.coincideCon(visor(100, "COP"));
        var resultado2 = filtro.coincideCon(visor(200, "COP"));
        var resultado3 = filtro.coincideCon(visor(99, "COP"));
        var resultado4 = filtro.coincideCon(visor(201, "COP"));
        var resultado5 = filtro.coincideCon(visor(100, "USD"));

        assertTrue(resultado1);
        assertTrue(resultado2);
        assertFalse(resultado3);
        assertFalse(resultado4);
        assertFalse(resultado5);
    }

    @Test
    public void permiteLimitesIndependientesYCero() {

        Visor producto = visor(100, "COP");

        var resultado1 = new FiltroProducto(null, null, 0.0, null, "COP", null).coincideCon(producto);
        var resultado2 = new FiltroProducto(null, null, null, 100.0, "COP", null).coincideCon(producto);
        var resultado3 = new FiltroProducto(null, null, 100.0, 100.0, "COP", null).coincideCon(producto);
        var resultado4 = new FiltroProducto(null, null, null, 0.0, "COP", null).coincideCon(producto);

        assertTrue(resultado1);
        assertTrue(resultado2);
        assertTrue(resultado3);
        assertFalse(resultado4);
    }

    @Test
    public void compatibilidadDistingueDestinoYModelo() {

        Compatibilidad casco = new Compatibilidad(TipoCompatibilidad.CASCO, " k-3 ");
        FiltroProducto filtro = new FiltroProducto(null, null, null, null, null, casco);

        var resultado1 = filtro.coincideCon(visor(100, "COP"));
        var resultado2 = new FiltroProducto(null, null, null, null, null, new Compatibilidad(TipoCompatibilidad.CASCO, "K-5")).coincideCon(visor(100, "COP"));
        Baul baul = Baul.crear("b1", new Marca("m2", "Givi"), Material.ABS, 30, "40x40", new Precio(200, "COP"), 5, "Negro");
        baul.indicarCompatibilidad(new Compatibilidad(TipoCompatibilidad.MOTO, "K-3"));
        baul.publicar();
        var resultado3 = filtro.coincideCon(baul);
        var resultado4 = new FiltroProducto(null, null, null, null, null, new Compatibilidad(TipoCompatibilidad.MOTO, "K-3")).coincideCon(baul);
        Producto guantes = Guantes.crear("g1", new Marca("m", "AGV"), new Precio(100, "COP"), "Negro", Talla.M, Material.CUERO, Proteccion.NIVEL_1);
        guantes.publicar();
        var resultado5 = filtro.coincideCon(guantes);

        assertTrue(resultado1);
        assertFalse(resultado2);
        assertFalse(resultado3);
        assertTrue(resultado4);
        assertFalse(resultado5);
    }

    @Test
    public void combinaFiltrosConAnd() {

        Compatibilidad casco = new Compatibilidad(TipoCompatibilidad.CASCO, "K-3");
        Visor producto = visor(100, "COP");

        var resultado1 = new FiltroProducto("AGV", "Negro", 100.0, 100.0, "COP", casco).coincideCon(producto);
        var resultado2 = new FiltroProducto("AGV", "Blanco", 100.0, 100.0, "COP", casco).coincideCon(producto);

        assertTrue(resultado1);
        assertFalse(resultado2);
    }

    @Test
    public void rechazaRangosInvalidosYMonedaAusente() {

        var entradas1 = new double[]{-1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};

        var acciones = new ArrayList<Executable>();
        for (double invalido : entradas1) {
            acciones.add(() -> new FiltroProducto(null, null, invalido, null, "COP", null));
            acciones.add(() -> new FiltroProducto(null, null, null, invalido, "COP", null));
        }
        acciones.add(() -> new FiltroProducto(null, null, 200.0, 100.0, "COP", null));
        acciones.add(() -> new FiltroProducto(null, null, 0.0, null, null, null));
        acciones.add(() -> new FiltroProducto(null, null, null, 100.0, " ", null));

        for (var accion : acciones) {
            assertThrows(ReglaDominioException.class, accion);
        }
    }
}
