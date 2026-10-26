package com.uniquindio.accesoriosMoto.domain.infraestructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.repository.ComentarioRepository;
import com.uniquindio.accesoriosMoto.domain.repository.CompradorRepository;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.Compatibilidad;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.FiltroProducto;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.*;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.PrecioEmbeddable;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.ProductoEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.ComentarioPersistenceMapper;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.CompradorPersistenceMapper;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.PedidoPersistenceMapper;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.ProductoPersistenceMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@Import({ProductoRepositoryJpa.class, PedidoRepositoryJpa.class, CompradorRepositoryJpa.class, ComentarioRepositoryJpa.class, ProductoPersistenceMapper.class, PedidoPersistenceMapper.class, CompradorPersistenceMapper.class, ComentarioPersistenceMapper.class})

public class RepositoriosJpaTest {

    @Autowired
    ProductoRepository productos;
    @Autowired
    PedidoRepository pedidos;
    @Autowired
    CompradorRepository compradores;
    @Autowired
    ComentarioRepository comentarios;
    @Autowired
    ProductoJpaDataRepository productosData;
    @Autowired
    EntityManager entityManager;
    @Autowired javax.sql.DataSource dataSource;

    static Stream<Producto> productos() {
        return DatosPersistenciaPrueba.productos();
    }

    private void recargar() {
        entityManager.flush(); entityManager.clear();
    }

    private Comprador comprador() {
        return new Comprador("comprador", "Ana", "ana@ejemplo.com", "clave");
    }

    @Test
    void guardaObtieneYActualizaComprador() {
        compradores.guardar(comprador());
        recargar();
        assertThat(compradores.obtenerPorId("comprador").orElseThrow()).usingRecursiveComparison().isEqualTo(comprador());
        compradores.guardar(new Comprador("comprador", "Ana actualizada", "nuevo@ejemplo.com", "otra"));
        recargar();
        assertThat(compradores.obtenerPorId("comprador").orElseThrow().getNombre()).isEqualTo("Ana actualizada");
    }

    @ParameterizedTest
    @MethodSource("productos")
    void recuperaPolimorficamenteTodosLosAtributosDelProducto(Producto original) throws Exception {

        original.publicar();

        productos.guardar(original);
        recargar();
        Producto recuperado = productos.obtenerPorId(original.getId()).orElseThrow();

        try (var conexion = dataSource.getConnection()) {
            assertThat(conexion.getMetaData().getURL()).startsWith("jdbc:h2:mem:");
        }
        assertThat(recuperado).isExactlyInstanceOf(original.getClass());
        assertThat(recuperado).usingRecursiveComparison().isEqualTo(original);
    }

    @Test
    void actualizaPublicacionYFiltraElCatalogo() {
        Producto producto = DatosPersistenciaPrueba.casco();
        productos.guardar(producto);
        recargar();
        assertThat(productos.buscar(new FiltroProducto(null, null, null, null, null, null))).isEmpty();
        Producto recuperado = productos.obtenerPorId(producto.getId()).orElseThrow();
        recuperado.publicar();
        productos.guardar(recuperado);
        recargar();
        assertThat(productos.buscar(new FiltroProducto(" marca de prueba ", " negro ", 100.0, 500000.0, "cop", null)))
                .extracting(Producto::getId).containsExactly(producto.getId());
        recuperado.despublicar();
        productos.guardar(recuperado);
        recargar();
        assertThat(productos.obtenerPorId(producto.getId()).orElseThrow().estaPublicado()).isFalse();
    }

    @Test
    void agregaYRetiraCompatibilidadesDeBaulYVisor() {
        for (Producto producto : DatosPersistenciaPrueba.productos().filter(ProductoCompatible.class::isInstance).toList()) {
            producto.publicar();
            productos.guardar(producto);
            recargar();
            Producto recuperado = productos.obtenerPorId(producto.getId()).orElseThrow();
            ProductoCompatible compatible = (ProductoCompatible) recuperado;
            Compatibilidad existente = compatible.getCompatibilidades().iterator().next();
            compatible.retirarCompatibilidad(existente);
            Compatibilidad nueva = new Compatibilidad(existente.tipo(), "Nuevo modelo");
            compatible.indicarCompatibilidad(nueva);
            productos.guardar(recuperado);
            recargar();
            assertThat(((ProductoCompatible) productos.obtenerPorId(producto.getId()).orElseThrow()).getCompatibilidades())
                    .containsExactly(nueva);
            assertThat(productos.buscar(new FiltroProducto(null, null, null, null, null, nueva)))
                    .extracting(Producto::getId).containsExactly(producto.getId());
            compatible.retirarCompatibilidad(nueva);
            productos.guardar(recuperado);
            recargar();
            assertThat(((ProductoCompatible) productos.obtenerPorId(producto.getId()).orElseThrow()).getCompatibilidades()).isEmpty();
        }
    }

    @Test
    void conservaRelacionOrdenHistoricosFechaYReembolsoAlCambiarPrecioActual() {
        Comprador comprador = comprador();
        compradores.guardar(comprador);
        List<Producto> catalogo = DatosPersistenciaPrueba.productos().limit(2).toList();
        catalogo.forEach(productos::guardar);
        LocalDateTime fecha = LocalDateTime.of(2026, 1, 1, 10, 20, 30);
        Pedido pedido = Pedido.registrar("pedido", comprador, List.of(ItemPedido.crear("i1", catalogo.get(0), 2), ItemPedido.crear("i2", catalogo.get(1), 1)), fecha);
        pedidos.registrar(pedido);
        recargar();
        ProductoEntity productoActual = productosData.findById(catalogo.getFirst().getId()).orElseThrow();
        productoActual.setPrecio(new PrecioEmbeddable(900000, "COP"));
        recargar();
        Pedido recuperado = pedidos.obtenerPorId("pedido").orElseThrow();
        assertThat(recuperado.getComprador().getId()).isEqualTo(comprador.getId());
        assertThat(recuperado.getItems()).extracting(ItemPedido::getId).containsExactly("i1", "i2");
        assertThat(recuperado.getItems().getFirst().getProducto().getPrecio().monto()).isEqualTo(900000);
        assertThat(recuperado.getItems().getFirst().getSubtotal()).isEqualTo(new Precio(1000000, "COP"));
        assertThat(recuperado.getTotal()).isEqualTo(new Precio(1500000, "COP"));
        assertThat(recuperado.getFecha()).isEqualTo(fecha);
        recuperado.completar();
        pedidos.registrar(recuperado);
        recargar();
        Pedido completado = pedidos.obtenerPorId("pedido").orElseThrow();
        completado.solicitarReembolso("Defectuoso");
        pedidos.registrar(completado);
        recargar();
        Pedido reembolsado = pedidos.obtenerPorId("pedido").orElseThrow();
        assertThat(reembolsado.getEstadoPedido()).isEqualTo(EstadoPedido.REEMBOLSADA);
        assertThat(reembolsado.getMotivoReembolso()).isEqualTo("Defectuoso");
        assertThat(reembolsado.getTotal()).isEqualTo(new Precio(1500000, "COP"));
        assertThat(productosData.count()).isEqualTo(2);
        assertThat(entityManager.createQuery("select count(i) from ItemPedidoEntity i", Long.class).getSingleResult()).isEqualTo(2);
    }

    @Test
    void persisteComentarioConRelacionesYRespuestaSinRelacionInventadaConPedido() {
        Comprador comprador = comprador();
        Producto producto = DatosPersistenciaPrueba.casco();
        compradores.guardar(comprador);
        productos.guardar(producto);
        Pedido pedido = Pedido.registrar("pedido", comprador, List.of(ItemPedido.crear("i", producto, 1)), LocalDateTime.now());
        pedido.completar();
        pedidos.registrar(pedido);
        Comentario comentario = Comentario.dejar("comentario", comprador, pedido, producto.getId(), 5, "Muy bueno");
        comentarios.guardar(comentario);
        recargar();
        assertThat(comentarios.obtenerPorId("comentario").orElseThrow()).usingRecursiveComparison().isEqualTo(comentario);
        comentarios.guardar(Comentario.reconstruir("comentario", comprador, producto, 5, "Muy bueno", "Gracias"));
        recargar();
        assertThat(comentarios.obtenerPorId("comentario").orElseThrow().getRespuestaVendedor()).isEqualTo("Gracias");
        assertThat(productosData.count()).isEqualTo(1);
    }

    @Test
    void devuelveOptionalVacioParaRecursosInexistentes() {
        assertThat(productos.obtenerPorId("inexistente")).isEmpty();
        assertThat(compradores.obtenerPorId("inexistente")).isEmpty();
        assertThat(pedidos.obtenerPorId("inexistente")).isEmpty();
        assertThat(comentarios.obtenerPorId("inexistente")).isEmpty();
    }
}
