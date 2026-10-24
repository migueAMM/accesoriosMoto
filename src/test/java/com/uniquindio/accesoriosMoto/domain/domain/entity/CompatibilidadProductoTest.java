package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CompatibilidadProductoTest {

    private Visor visor(List<String> modelos) {
        return Visor.crear("v1", new Marca("m1", "AGV"), new Precio(100, "COP"), "Claro", "Integral", modelos);
    }

    private Baul baul() {
        return Baul.crear("b1", new Marca("m2", "Givi"), Material.ABS, 30, "40x40", new Precio(200, "COP"), 5, "Negro");
    }

    @Test
    void visorConservaModelosInicialesYPermiteAgregarYRetirar() {
        Visor producto = visor(List.of("K-3"));
        Compatibilidad nueva = new Compatibilidad(TipoCompatibilidad.CASCO, "K-5");
        producto.indicarCompatibilidad(nueva);
        producto.retirarCompatibilidad(new Compatibilidad(TipoCompatibilidad.CASCO, " k-3 "));
        assertEquals(List.of("K-5"), producto.getModelosCascoCompatibles());
    }

    @Test
    void baulPermiteAgregarYRetirarMoto() {
        Baul producto = baul();
        Compatibilidad compatibilidad = new Compatibilidad(TipoCompatibilidad.MOTO, "NKD 125");
        producto.indicarCompatibilidad(compatibilidad);
        assertTrue(producto.getCompatibilidades().contains(compatibilidad));
        producto.retirarCompatibilidad(compatibilidad);
        assertTrue(producto.getCompatibilidades().isEmpty());
    }

    @Test
    void rechazaDestinosIncorrectosYProductosSinCompatibilidad() {
        Compatibilidad moto = new Compatibilidad(TipoCompatibilidad.MOTO, "NKD");
        Compatibilidad casco = new Compatibilidad(TipoCompatibilidad.CASCO, "K-3");
        Producto guantes = Guantes.crear("g1", new Marca("m", "Marca"), new Precio(10, "COP"), "Negro", Talla.M, Material.CUERO, Proteccion.NIVEL_1);
        assertThrows(ReglaDominioException.class, () -> visor(List.of()).indicarCompatibilidad(moto));
        assertThrows(ReglaDominioException.class, () -> baul().indicarCompatibilidad(casco));
        assertFalse(guantes instanceof ProductoCompatible);
    }

    @Test
    void rechazaDuplicadosYRetirosInexistentesSinCambiarEstado() {
        Visor producto = visor(List.of("K-3"));
        assertThrows(ReglaDominioException.class, () -> producto.indicarCompatibilidad(new Compatibilidad(TipoCompatibilidad.CASCO, " k-3 ")));
        assertThrows(ReglaDominioException.class, () -> producto.retirarCompatibilidad(new Compatibilidad(TipoCompatibilidad.CASCO, "K-5")));
        assertEquals(List.of("K-3"), producto.getModelosCascoCompatibles());
    }

    @Test
    void protegeColeccionesYValidaListaInicial() {
        List<String> entrada = new ArrayList<>(List.of("K-3"));
        Visor producto = visor(entrada);
        entrada.clear();
        assertEquals(1, producto.getCompatibilidades().size());
        assertThrows(UnsupportedOperationException.class, () -> producto.getCompatibilidades().clear());
        assertThrows(UnsupportedOperationException.class, () -> producto.getModelosCascoCompatibles().clear());
        assertThrows(ReglaDominioException.class, () -> visor(null));
        assertThrows(ReglaDominioException.class, () -> visor(List.of("K-3", "k-3")));
        assertThrows(ReglaDominioException.class, () -> visor(List.of(" ")));
        assertThrows(ReglaDominioException.class, () -> producto.indicarCompatibilidad(null));
        assertThrows(ReglaDominioException.class, () -> producto.retirarCompatibilidad(null));
    }

    @Test
    void permiteGestionarCompatibilidadPublicadoSinCambiarPublicacion() {
        Baul producto = baul();
        producto.publicar();
        producto.indicarCompatibilidad(new Compatibilidad(TipoCompatibilidad.MOTO, "NKD"));
        assertTrue(producto.estaPublicado());
    }
}
