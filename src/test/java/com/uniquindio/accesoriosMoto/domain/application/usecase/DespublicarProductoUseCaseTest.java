package com.uniquindio.accesoriosMoto.domain.application.usecase;

import com.uniquindio.accesoriosMoto.application.usecase.DespublicarProductoUseCase;
import com.uniquindio.accesoriosMoto.application.usecase.PublicarProductoUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.entity.Visor;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.FiltroProducto;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.ProductoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DespublicarProductoUseCaseTest {

    private Visor visor() {
        return Visor.crear("v1", new Marca("m", "AGV"), new Precio(100, "COP"), "Claro", "Integral", List.of("K-3"));
    }

    @Test
    public void retiraDeLaBusquedaSinEliminarProductoNiSusDatos() {
        ProductoRepository repository = new ProductoRepositoryEnMemoria();
        Visor producto = visor();
        producto.publicar();
        repository.guardar(producto);
        FiltroProducto filtro = new FiltroProducto(null, null, null, null, null, null);

        var resultado1 = repository.buscar(filtro);
        Producto resultado = new DespublicarProductoUseCase(repository).ejecutar("v1");
        var resultado2 = repository.obtenerPorId("v1").orElseThrow();
        var resultado3 = resultado.estaPublicado();
        var resultado4 = repository.buscar(filtro).isEmpty();
        var resultado5 = resultado.getPrecio();
        var resultado6 = producto.getModelosCascoCompatibles();
        new PublicarProductoUseCase(repository).ejecutar("v1");
        var resultado7 = repository.buscar(filtro);

        assertEquals(List.of(producto), resultado1);
        assertSame(producto, resultado);
        assertSame(producto, resultado2);
        assertFalse(resultado3);
        assertTrue(resultado4);
        assertEquals(new Precio(100, "COP"), resultado5);
        assertEquals(List.of("K-3"), resultado6);
        assertEquals(List.of(producto), resultado7);
    }

    @Test
    public void guardaSoloDespuesDeDespublicarYRechazaRepeticion() {
        var observacion0 = new java.util.ArrayList<String>();
        var observacion1 = new java.util.ArrayList<Producto>();
        var observacion2 = new java.util.ArrayList<Boolean>();

        Visor producto = visor();
        producto.publicar();
        class RepositorioPrueba extends ProductoRepositoryEnMemoria {
            int guardados;
            public Optional<Producto> obtenerPorId(String id) {
                observacion0.add( id);
                return Optional.of(producto);
            }
            public void guardar(Producto recibido) {
                observacion1.add( recibido);
                observacion2.add(recibido.estaPublicado());
                guardados++;
            }
            public List<Producto> buscar(FiltroProducto filtro) {
                throw new UnsupportedOperationException();
            }
        }
        RepositorioPrueba repository = new RepositorioPrueba();
        DespublicarProductoUseCase caso = new DespublicarProductoUseCase(repository);

        caso.ejecutar("v1");
        var contador1 =  repository.guardados;
        var error2 = catchThrowable(() -> caso.ejecutar("v1"));
        var contador3 =  repository.guardados;

        assertEquals(1, contador1);
        assertInstanceOf(ReglaDominioException.class, error2);
        assertEquals(1, contador3);
        for (var observacion : observacion0) {
            assertEquals("v1", observacion);
        }
        for (var observacion : observacion1) {
            assertSame(producto, observacion);
        }
        for (var observacion : observacion2) {
            assertFalse(observacion);
        }
    }

    @Test
    public void noGuardaCuandoElProductoNoExisteOYaEstaDespublicado() {
        var llamadasProhibidas = new java.util.ArrayList<String>();

        Visor producto = visor();
        ProductoRepository repository = new ProductoRepositoryEnMemoria() {
            public Optional<Producto> obtenerPorId(String id) {
                return "v1".equals(id) ? Optional.of(producto) : Optional.empty();
            }
            public void guardar(Producto recibido) {
                llamadasProhibidas.add("No debe guardar tras una operación inválida");
            }
            public List<Producto> buscar(FiltroProducto filtro) {
                throw new UnsupportedOperationException();
            }
        };
        DespublicarProductoUseCase caso = new DespublicarProductoUseCase(repository);

        var accion1Error = catchThrowable(() -> caso.ejecutar("inexistente"));
        var accion2Error = catchThrowable(() -> caso.ejecutar("v1"));

        assertInstanceOf(NoSuchElementException.class, accion1Error);
        assertInstanceOf(ReglaDominioException.class, accion2Error);
        assertFalse(producto.estaPublicado());
        assertTrue(llamadasProhibidas.isEmpty(), llamadasProhibidas.toString());
    }
}
