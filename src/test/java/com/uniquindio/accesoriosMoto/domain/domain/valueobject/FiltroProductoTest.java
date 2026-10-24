package com.uniquindio.accesoriosMoto.domain.domain.valueobject;

import com.uniquindio.accesoriosMoto.domain.entity.Baul;
import com.uniquindio.accesoriosMoto.domain.entity.Guantes;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.entity.Visor;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;

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
    void sinFiltrosSoloCoincidenPublicados() {
        FiltroProducto filtro = new FiltroProducto(null, null, null, null, null, null);
        Visor producto = visor(100, "COP");
        assertTrue(filtro.coincideCon(producto));
        producto.despublicar();
        assertFalse(filtro.coincideCon(producto));
        assertFalse(filtro.coincideCon(null));
    }

    @Test
    void marcaYColorSonExactosIgnorandoMayusculasYEspaciosExteriores() {
        Visor producto = visor(100, "COP");
        assertTrue(new FiltroProducto(" agv ", null, null, null, null, null).coincideCon(producto));
        assertTrue(new FiltroProducto(null, " negro ", null, null, null, null).coincideCon(producto));
        assertFalse(new FiltroProducto("AG", null, null, null, null, null).coincideCon(producto));
        assertFalse(new FiltroProducto(null, "Blanco", null, null, null, null).coincideCon(producto));
        assertTrue(new FiltroProducto(" ", " ", null, null, " ", null).coincideCon(producto));
    }

    @Test
    void precioIncluyeAmbosLimitesYExigeMismaMoneda() {
        FiltroProducto filtro = new FiltroProducto(null, null, 100.0, 200.0, " cop ", null);
        assertTrue(filtro.coincideCon(visor(100, "COP")));
        assertTrue(filtro.coincideCon(visor(200, "COP")));
        assertFalse(filtro.coincideCon(visor(99, "COP")));
        assertFalse(filtro.coincideCon(visor(201, "COP")));
        assertFalse(filtro.coincideCon(visor(100, "USD")));
    }

    @Test
    void permiteLimitesIndependientesYCero() {
        Visor producto = visor(100, "COP");
        assertTrue(new FiltroProducto(null, null, 0.0, null, "COP", null).coincideCon(producto));
        assertTrue(new FiltroProducto(null, null, null, 100.0, "COP", null).coincideCon(producto));
        assertTrue(new FiltroProducto(null, null, 100.0, 100.0, "COP", null).coincideCon(producto));
        assertFalse(new FiltroProducto(null, null, null, 0.0, "COP", null).coincideCon(producto));
    }

    @Test
    void compatibilidadDistingueDestinoYModelo() {
        Compatibilidad casco = new Compatibilidad(TipoCompatibilidad.CASCO, " k-3 ");
        FiltroProducto filtro = new FiltroProducto(null, null, null, null, null, casco);
        assertTrue(filtro.coincideCon(visor(100, "COP")));
        assertFalse(new FiltroProducto(null, null, null, null, null, new Compatibilidad(TipoCompatibilidad.CASCO, "K-5")).coincideCon(visor(100, "COP")));
        Baul baul = Baul.crear("b1", new Marca("m2", "Givi"), Material.ABS, 30, "40x40", new Precio(200, "COP"), 5, "Negro");
        baul.indicarCompatibilidad(new Compatibilidad(TipoCompatibilidad.MOTO, "K-3"));
        baul.publicar();
        assertFalse(filtro.coincideCon(baul));
        assertTrue(new FiltroProducto(null, null, null, null, null, new Compatibilidad(TipoCompatibilidad.MOTO, "K-3")).coincideCon(baul));
        Producto guantes = Guantes.crear("g1", new Marca("m", "AGV"), new Precio(100, "COP"), "Negro", Talla.M, Material.CUERO, Proteccion.NIVEL_1);
        guantes.publicar();
        assertFalse(filtro.coincideCon(guantes));
    }

    @Test
    void combinaFiltrosConAnd() {
        Compatibilidad casco = new Compatibilidad(TipoCompatibilidad.CASCO, "K-3");
        Visor producto = visor(100, "COP");
        assertTrue(new FiltroProducto("AGV", "Negro", 100.0, 100.0, "COP", casco).coincideCon(producto));
        assertFalse(new FiltroProducto("AGV", "Blanco", 100.0, 100.0, "COP", casco).coincideCon(producto));
    }

    @Test
    void rechazaRangosInvalidosYMonedaAusente() {
        for (double invalido : new double[]{-1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            assertThrows(ReglaDominioException.class, () -> new FiltroProducto(null, null, invalido, null, "COP", null));
            assertThrows(ReglaDominioException.class, () -> new FiltroProducto(null, null, null, invalido, "COP", null));
        }
        assertThrows(ReglaDominioException.class, () -> new FiltroProducto(null, null, 200.0, 100.0, "COP", null));
        assertThrows(ReglaDominioException.class, () -> new FiltroProducto(null, null, 0.0, null, null, null));
        assertThrows(ReglaDominioException.class, () -> new FiltroProducto(null, null, null, 100.0, " ", null));
    }
}
