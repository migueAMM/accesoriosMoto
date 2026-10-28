package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

public class CompatibilidadProductoTest {

    private Visor visor(List<String> modelos) {
        return Visor.crear("v1", new Marca("m1", "AGV"), new Precio(100, "COP"), "Claro", "Integral", modelos);
    }

    private Baul baul() {
        return Baul.crear("b1", new Marca("m2", "Givi"), Material.ABS, 30, "40x40", new Precio(200, "COP"), 5, "Negro");
    }

    @Test
    public void visorConservaModelosInicialesYPermiteAgregarYRetirar() {
        Visor producto = visor(List.of("K-3"));
        Compatibilidad nueva = new Compatibilidad(TipoCompatibilidad.CASCO, "K-5");

        producto.indicarCompatibilidad(nueva);
        producto.retirarCompatibilidad(new Compatibilidad(TipoCompatibilidad.CASCO, " k-3 "));

        assertEquals(List.of("K-5"), producto.getModelosCascoCompatibles());
    }

    @Test
    public void baulPermiteAgregarYRetirarMoto() {
        Baul producto = baul();
        Compatibilidad compatibilidad = new Compatibilidad(TipoCompatibilidad.MOTO, "NKD 125");

        producto.indicarCompatibilidad(compatibilidad);
        var resultado1 = producto.getCompatibilidades().contains(compatibilidad);
        producto.retirarCompatibilidad(compatibilidad);

        assertTrue(resultado1);
        assertTrue(producto.getCompatibilidades().isEmpty());
    }

    @Test
    public void rechazaDestinosIncorrectosYProductosSinCompatibilidad() {
        Compatibilidad moto = new Compatibilidad(TipoCompatibilidad.MOTO, "NKD");
        Compatibilidad casco = new Compatibilidad(TipoCompatibilidad.CASCO, "K-3");
        Producto guantes = Guantes.crear("g1", new Marca("m", "Marca"), new Precio(10, "COP"), "Negro", Talla.M, Material.CUERO, Proteccion.NIVEL_1);

        var accion1Error = catchThrowable(() -> visor(List.of()).indicarCompatibilidad(moto));
        var accion2Error = catchThrowable(() -> baul().indicarCompatibilidad(casco));
        var resultado3 = guantes instanceof ProductoCompatible;

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        assertInstanceOf(ReglaDominioException.class, accion2Error);
        assertFalse(resultado3);
    }

    @Test
    public void rechazaDuplicadosYRetirosInexistentesSinCambiarEstado() {
        Visor producto = visor(List.of("K-3"));

        var accion1Error = catchThrowable(() -> producto.indicarCompatibilidad(new Compatibilidad(TipoCompatibilidad.CASCO, " k-3 ")));
        var accion2Error = catchThrowable(() -> producto.retirarCompatibilidad(new Compatibilidad(TipoCompatibilidad.CASCO, "K-5")));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        assertInstanceOf(ReglaDominioException.class, accion2Error);
        assertEquals(List.of("K-3"), producto.getModelosCascoCompatibles());
    }

    @Test
    public void protegeColeccionesYValidaListaInicial() {
        List<String> entrada = new ArrayList<>(List.of("K-3"));
        Visor producto = visor(entrada);

        entrada.clear();
        var resultado1 = producto.getCompatibilidades().size();
        var accion2Error = catchThrowable(() -> producto.getCompatibilidades().clear());
        var accion3Error = catchThrowable(() -> producto.getModelosCascoCompatibles().clear());
        var accion4Error = catchThrowable(() -> visor(null));
        var accion5Error = catchThrowable(() -> visor(List.of("K-3", "k-3")));
        var accion6Error = catchThrowable(() -> visor(List.of(" ")));
        var accion7Error = catchThrowable(() -> producto.indicarCompatibilidad(null));
        var accion8Error = catchThrowable(() -> producto.retirarCompatibilidad(null));

        assertEquals(1, resultado1);
        assertInstanceOf(UnsupportedOperationException.class, accion2Error);
        assertInstanceOf(UnsupportedOperationException.class, accion3Error);
        assertInstanceOf(ReglaDominioException.class, accion4Error);
        assertInstanceOf(ReglaDominioException.class, accion5Error);
        assertInstanceOf(ReglaDominioException.class, accion6Error);
        assertInstanceOf(ReglaDominioException.class, accion7Error);
        assertInstanceOf(ReglaDominioException.class, accion8Error);
    }

    @Test
    public void permiteGestionarCompatibilidadPublicadoSinCambiarPublicacion() {
        Baul producto = baul();
        producto.publicar();

        producto.indicarCompatibilidad(new Compatibilidad(TipoCompatibilidad.MOTO, "NKD"));

        assertTrue(producto.estaPublicado());
    }
}
