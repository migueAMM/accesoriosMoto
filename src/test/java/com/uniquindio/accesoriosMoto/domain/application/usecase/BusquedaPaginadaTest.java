package com.uniquindio.accesoriosMoto.domain.application.usecase;

import com.uniquindio.accesoriosMoto.application.usecase.BuscarProductoUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.Pagina;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.BusquedaProducto;
import com.uniquindio.accesoriosMoto.domain.valueObject.FiltroProducto;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.ProductoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
        "spring.sql.init.mode=always",
        "spring.jpa.defer-datasource-initialization=true"})
public class BusquedaPaginadaTest {

    @Autowired
    private BuscarProductoUseCase buscar;
    @Autowired
    private ProductoRepository productos;

    private List<String> ids(Pagina<Producto> p) {
        return p.contenido().stream().map(Producto::getId).toList();
    }

    @Test
    public void buscaPorColorOrdenaPorPrecioYPagina() {
        var busqueda = new BusquedaProducto("negro", null, null, null, "precio", false);

        var p0 = buscar.buscarPorTexto(busqueda, 0, 2);
        var p1 = buscar.buscarPorTexto(busqueda, 1, 2);

        assertThat(p0.totalElementos()).isEqualTo(5);
        assertThat(p0.totalPaginas()).isEqualTo(3);
        assertThat(ids(p0)).containsExactly("seed-casco-1", "seed-chaqueta-1");
        assertThat(ids(p1)).containsExactly("seed-baul-1", "seed-guantes-1");
    }

    @Test
    public void combinaTextoDeMarcaConRangoDePrecio() {
        var busqueda = new BusquedaProducto(" agv ", null, 200000.0, "cop", null, true);

        assertThat(ids(buscar.buscarPorTexto(busqueda, 0, 5))).containsExactly("seed-visor-1");
    }

    @Test
    public void losComodinesSeBuscanDeFormaLiteral() {
        var busqueda = new BusquedaProducto("%", null, null, null, null, true);

        assertThat(buscar.buscarPorTexto(busqueda, 0, 5).totalElementos()).isZero();
    }

    @Test
    public void jpaYMemoriaDanElMismoResultado() {
        var busqueda = new BusquedaProducto("a", 50000.0, 500000.0, "COP", "marca", true);
        var enMemoria = new ProductoRepositoryEnMemoria();
        productos.buscar(new FiltroProducto(null, null, null, null, null, null)).forEach(enMemoria::guardar);

        assertThat(ids(productos.buscarPorTexto(busqueda, 0, 50)))
                .isEqualTo(ids(enMemoria.buscarPorTexto(busqueda, 0, 50)));
    }

    @Test
    public void rechazaOrdenInvalidoYPrecioSinMoneda() {
        assertThrows(ReglaDominioException.class, () -> new BusquedaProducto(null, null, null, null, "contrasena", true));
        assertThrows(ReglaDominioException.class, () -> new BusquedaProducto(null, 100.0, null, null, null, true));
    }
}