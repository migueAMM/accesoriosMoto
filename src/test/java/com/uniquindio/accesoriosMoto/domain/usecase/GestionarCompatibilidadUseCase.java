package com.uniquindio.accesoriosMoto.domain.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Guantes;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.entity.Visor;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.ProductoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class GestionarCompatibilidadUseCase {

    private Visor visor() {
        return Visor.crear("v1", new Marca("m", "AGV"), new Precio(100, "COP"), "Claro", "Integral", List.of());
    }

    @Test
    void indicaYRetiraConRepositorioEnMemoria() {
        ProductoRepository repository = new ProductoRepositoryEnMemoria();
        Visor producto = visor();
        repository.guardar(producto);
        com.uniquindio.accesoriosMoto.application.usecase.GestionarCompatibilidadUseCase caso = new com.uniquindio.accesoriosMoto.application.usecase.GestionarCompatibilidadUseCase(repository);
        caso.indicar("v1", TipoCompatibilidad.CASCO, "K-3");
        assertSame(producto, repository.obtenerPorId("v1").orElseThrow());
        assertEquals(1, producto.getCompatibilidades().size());
        caso.retirar("v1", TipoCompatibilidad.CASCO, "K-3");
        assertTrue(producto.getCompatibilidades().isEmpty());
    }

    @Test
    void rechazaProductoSinCapacidadSinGuardar() {
        Producto producto = Guantes.crear("g1", new Marca("m", "Marca"), new Precio(10, "COP"), "Negro", Talla.M, Material.CUERO, Proteccion.NIVEL_1);
        ProductoRepository repository = new ProductoRepository() {
            public List<Producto> buscar(FiltroProducto filtro) { throw new UnsupportedOperationException(); }
            public Optional<Producto> obtenerPorId(String id) { return Optional.of(producto); }
            public void guardar(Producto p) { fail("No debe guardar un producto sin compatibilidad"); }
        };
        com.uniquindio.accesoriosMoto.application.usecase.GestionarCompatibilidadUseCase caso = new com.uniquindio.accesoriosMoto.application.usecase.GestionarCompatibilidadUseCase(repository);
        assertThrows(ReglaDominioException  .class, () -> caso.indicar("g1", TipoCompatibilidad.MOTO, "NKD"));
        assertThrows(ReglaDominioException.class, () -> caso.retirar("g1", TipoCompatibilidad.MOTO, "NKD"));
    }

    @Test
    void rechazaProductoInexistenteEnAmbasOperaciones() {
        com.uniquindio.accesoriosMoto.application.usecase.GestionarCompatibilidadUseCase caso = new com.uniquindio.accesoriosMoto.application.usecase.GestionarCompatibilidadUseCase(new ProductoRepositoryEnMemoria());
        assertThrows(ReglaDominioException.class, () -> caso.indicar("no", TipoCompatibilidad.CASCO, "K-3"));
        assertThrows(ReglaDominioException.class, () -> caso.retirar("no", TipoCompatibilidad.CASCO, "K-3"));
    }

    @Test
    void guardaSoloDespuesDeOperacionValida() {
        Visor producto = visor();
        class RepositorioPrueba implements ProductoRepository {
            public List<Producto> buscar(FiltroProducto filtro) { throw new UnsupportedOperationException(); }
            int guardados;
            public Optional<Producto> obtenerPorId(String id) { return Optional.of(producto); }
            public void guardar(Producto p) { assertSame(producto, p); guardados++; }
        }
        RepositorioPrueba repository = new RepositorioPrueba();
        com.uniquindio.accesoriosMoto.application.usecase.GestionarCompatibilidadUseCase caso = new com.uniquindio.accesoriosMoto.application.usecase.GestionarCompatibilidadUseCase(repository);
        assertSame(producto, caso.indicar("v1", TipoCompatibilidad.CASCO, "K-3"));
        assertEquals(1, repository.guardados);
        assertThrows(ReglaDominioException.class, () -> caso.indicar("v1", TipoCompatibilidad.CASCO, "k-3"));
        assertThrows(ReglaDominioException.class, () -> caso.indicar("v1", TipoCompatibilidad.MOTO, "NKD"));
        assertThrows(ReglaDominioException.class, () -> caso.retirar("v1", TipoCompatibilidad.CASCO, "K-5"));
        assertEquals(1, repository.guardados);
        assertSame(producto, caso.retirar("v1", TipoCompatibilidad.CASCO, "K-3"));
        assertEquals(2, repository.guardados);
    }
}
