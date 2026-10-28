package com.uniquindio.accesoriosMoto.domain.application.usecase;

import com.uniquindio.accesoriosMoto.application.usecase.GestionarCompatibilidadUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.Guantes;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.entity.Visor;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.ProductoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class GestionarCompatibilidadUseCaseTest {

    private Visor visor() {
        return Visor.crear("v1", new Marca("m", "AGV"), new Precio(100, "COP"), "Claro", "Integral", List.of());
    }

    @Test
    public void indicaYRetiraConRepositorioEnMemoria() {
        ProductoRepository repository = new ProductoRepositoryEnMemoria();
        Visor producto = visor();
        repository.guardar(producto);
        GestionarCompatibilidadUseCase caso = new GestionarCompatibilidadUseCase(repository);

        caso.indicar("v1", TipoCompatibilidad.CASCO, "K-3");
        var resultado1 = repository.obtenerPorId("v1").orElseThrow();

        int cantidadIndicada = producto.getCompatibilidades().size();
        caso.retirar("v1", TipoCompatibilidad.CASCO, "K-3");

        assertSame(producto, resultado1);
        assertEquals(1, cantidadIndicada);
        assertTrue(producto.getCompatibilidades().isEmpty());
    }

    @Test
    public void rechazaProductoSinCapacidadSinGuardar() {
        var llamadasProhibidas = new java.util.ArrayList<String>();

        Producto producto = Guantes.crear("g1", new Marca("m", "Marca"), new Precio(10, "COP"), "Negro", Talla.M, Material.CUERO, Proteccion.NIVEL_1);
        ProductoRepository repository = new ProductoRepositoryEnMemoria() {
            public List<Producto> buscar(FiltroProducto filtro) {
                throw new UnsupportedOperationException();
            }
            public Optional<Producto> obtenerPorId(String id) {
                return Optional.of(producto);
            }
            public void guardar(Producto p) {
                llamadasProhibidas.add("No debe guardar un producto sin compatibilidad");
            }
        };
        GestionarCompatibilidadUseCase caso = new GestionarCompatibilidadUseCase(repository);

        var accion1Error = catchThrowable(() -> caso.indicar("g1", TipoCompatibilidad.MOTO, "NKD"));
        var accion2Error = catchThrowable(() -> caso.retirar("g1", TipoCompatibilidad.MOTO, "NKD"));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        assertInstanceOf(ReglaDominioException.class, accion2Error);
        assertTrue(llamadasProhibidas.isEmpty(), llamadasProhibidas.toString());
    }

    @Test
    public void rechazaProductoInexistenteEnAmbasOperaciones() {
        GestionarCompatibilidadUseCase caso = new GestionarCompatibilidadUseCase(new ProductoRepositoryEnMemoria());

        var accion1Error = catchThrowable(() -> caso.indicar("no", TipoCompatibilidad.CASCO, "K-3"));
        var accion2Error = catchThrowable(() -> caso.retirar("no", TipoCompatibilidad.CASCO, "K-3"));

        assertInstanceOf(NoSuchElementException.class, accion1Error);
        assertInstanceOf(NoSuchElementException.class, accion2Error);
    }

    @Test
    public void guardaSoloDespuesDeOperacionValida() {
        var observacion0 = new java.util.ArrayList<Producto>();

        Visor producto = visor();
        class RepositorioPrueba extends ProductoRepositoryEnMemoria {
            public List<Producto> buscar(FiltroProducto filtro) {
                throw new UnsupportedOperationException();
            }
            int guardados;
            public Optional<Producto> obtenerPorId(String id) {
                return Optional.of(producto);
            }
            public void guardar(Producto p) {
                observacion0.add( p); guardados++;
            }
        }
        RepositorioPrueba repository = new RepositorioPrueba();
        GestionarCompatibilidadUseCase caso = new GestionarCompatibilidadUseCase(repository);

        var producto1 =  caso.indicar("v1", TipoCompatibilidad.CASCO, "K-3");
        var contador2 =  repository.guardados;
        var error3 = catchThrowable(() -> caso.indicar("v1", TipoCompatibilidad.CASCO, "k-3"));
        var error4 = catchThrowable(() -> caso.indicar("v1", TipoCompatibilidad.MOTO, "NKD"));
        var error5 = catchThrowable(() -> caso.retirar("v1", TipoCompatibilidad.CASCO, "K-5"));
        var contador6 =  repository.guardados;
        var producto7 =  caso.retirar("v1", TipoCompatibilidad.CASCO, "K-3");
        var contador8 =  repository.guardados;

        assertSame(producto, producto1);
        assertEquals(1, contador2);
        assertInstanceOf(ReglaDominioException.class, error3);
        assertInstanceOf(ReglaDominioException.class, error4);
        assertInstanceOf(ReglaDominioException.class, error5);
        assertEquals(1, contador6);
        assertSame(producto, producto7);
        assertEquals(2, contador8);
        for (var observacion : observacion0) {
            assertSame(producto, observacion);
        }
    }
}
