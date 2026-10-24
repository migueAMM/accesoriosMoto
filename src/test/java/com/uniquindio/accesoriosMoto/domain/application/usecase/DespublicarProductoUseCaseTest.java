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
import java.util.Optional;

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
    void retiraDeLaBusquedaSinEliminarProductoNiSusDatos() {
        ProductoRepository repository = new ProductoRepositoryEnMemoria();
        Visor producto = visor();
        producto.publicar();
        repository.guardar(producto);
        FiltroProducto filtro = new FiltroProducto(null, null, null, null, null, null);
        assertEquals(List.of(producto), repository.buscar(filtro));

        Producto resultado = new DespublicarProductoUseCase(repository).ejecutar("v1");

        assertSame(producto, resultado);
        assertSame(producto, repository.obtenerPorId("v1").orElseThrow());
        assertFalse(resultado.estaPublicado());
        assertTrue(repository.buscar(filtro).isEmpty());
        assertEquals(new Precio(100, "COP"), resultado.getPrecio());
        assertEquals(List.of("K-3"), producto.getModelosCascoCompatibles());
        new PublicarProductoUseCase(repository).ejecutar("v1");
        assertEquals(List.of(producto), repository.buscar(filtro));
    }

    @Test
    void guardaSoloDespuesDeDespublicarYRechazaRepeticion() {
        Visor producto = visor();
        producto.publicar();
        class RepositorioPrueba implements ProductoRepository {
            int guardados;
            public Optional<Producto> obtenerPorId(String id) {
                assertEquals("v1", id);
                return Optional.of(producto);
            }
            public void guardar(Producto recibido) {
                assertSame(producto, recibido);
                assertFalse(recibido.estaPublicado());
                guardados++;
            }
            public List<Producto> buscar(FiltroProducto filtro) { throw new UnsupportedOperationException(); }
        }
        RepositorioPrueba repository = new RepositorioPrueba();
        DespublicarProductoUseCase caso = new DespublicarProductoUseCase(repository);

        caso.ejecutar("v1");
        assertEquals(1, repository.guardados);
        assertThrows(ReglaDominioException.class, () -> caso.ejecutar("v1"));
        assertEquals(1, repository.guardados);
    }

    @Test
    void noGuardaCuandoElProductoNoExisteOYaEstaDespublicado() {
        Visor producto = visor();
        ProductoRepository repository = new ProductoRepository() {
            public Optional<Producto> obtenerPorId(String id) {
                return "v1".equals(id) ? Optional.of(producto) : Optional.empty();
            }
            public void guardar(Producto recibido) { fail("No debe guardar tras una operación inválida"); }
            public List<Producto> buscar(FiltroProducto filtro) { throw new UnsupportedOperationException(); }
        };
        DespublicarProductoUseCase caso = new DespublicarProductoUseCase(repository);

        assertThrows(ReglaDominioException.class, () -> caso.ejecutar("inexistente"));
        assertThrows(ReglaDominioException.class, () -> caso.ejecutar("v1"));
        assertFalse(producto.estaPublicado());
    }
}
