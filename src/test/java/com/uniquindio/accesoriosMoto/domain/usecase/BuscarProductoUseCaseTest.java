package com.uniquindio.accesoriosMoto.domain.usecase;

import com.uniquindio.accesoriosMoto.application.usecase.BuscarProductoUseCase;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuscarProductoUseCaseTest {

    @Test
    void delegaCriteriosAlPuertoSinGuardarNiBuscarPorId() {
        FiltroProducto filtro = new FiltroProducto("AGV", null, null, null, null, null);
        List<Producto> resultado = List.of();
        ProductoRepository puerto = new ProductoRepository() {
            public List<Producto> buscar(FiltroProducto recibido) {
                assertSame(filtro, recibido);
                return resultado;
            }
            public void guardar(Producto producto) { fail("La búsqueda no debe guardar"); }
            public Optional<Producto> obtenerPorId(String id) { fail("La búsqueda no usa un id"); return Optional.empty(); }
        };
        assertSame(resultado, new BuscarProductoUseCase(puerto).ejecutar(filtro));
    }

    @Test
    void consultaCatalogoEnMemoriaYReflejaDespublicacion() {
        ProductoRepository repository = new ProductoRepositoryEnMemoria();
        Visor publicado = Visor.crear("v1", new Marca("m", "AGV"), new Precio(100, "COP"), "Negro", "Integral", List.of("K-3"));
        Visor oculto = Visor.crear("v2", new Marca("m", "AGV"), new Precio(100, "COP"), "Negro", "Integral", List.of("K-3"));
        publicado.publicar();
        repository.guardar(publicado);
        repository.guardar(oculto);
        BuscarProductoUseCase caso = new BuscarProductoUseCase(repository);
        FiltroProducto filtro = new FiltroProducto("agv", "NEGRO", 100.0, 200.0, "COP", new Compatibilidad(TipoCompatibilidad.CASCO, "K-3"));
        assertEquals(List.of(publicado), caso.ejecutar(filtro));
        assertEquals(List.of(publicado), caso.ejecutar(new FiltroProducto(null, null, null, null, null, null)));
        assertTrue(caso.ejecutar(new FiltroProducto("Givi", null, null, null, null, null)).isEmpty());
        publicado.despublicar();
        assertTrue(caso.ejecutar(filtro).isEmpty());
    }

    @Test
    void catalogoVacioDevuelveListaVaciaYRechazaFiltroNulo() {
        BuscarProductoUseCase caso = new BuscarProductoUseCase(new ProductoRepositoryEnMemoria());
        assertTrue(caso.ejecutar(new FiltroProducto(null, null, null, null, null, null)).isEmpty());
        assertThrows(ReglaDominioException.class, () -> caso.ejecutar(null));
    }
}
