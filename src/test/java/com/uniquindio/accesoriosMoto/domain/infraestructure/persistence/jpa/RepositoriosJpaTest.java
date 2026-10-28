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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Set.copyOf;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@Import({
        ProductoRepositoryJpa.class,
        PedidoRepositoryJpa.class,
        CompradorRepositoryJpa.class,
        ComentarioRepositoryJpa.class,
        ProductoPersistenceMapper.class,
        PedidoPersistenceMapper.class,
        CompradorPersistenceMapper.class,
        ComentarioPersistenceMapper.class})

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
    public void guardaObtieneYActualizaComprador() {

        compradores.guardar(comprador());
        recargar();
        var resultado1 = compradores.obtenerPorId("comprador").orElseThrow();
        compradores.guardar(new Comprador("comprador", "Ana actualizada", "ananuevo@gmail.com", "otra"));
        recargar();
        var resultado2 = compradores.obtenerPorId("comprador").orElseThrow().getNombre();

        assertThat(resultado1).usingRecursiveComparison().isEqualTo(comprador());
        assertThat(resultado2).isEqualTo("Ana actualizada");
    }

    @ParameterizedTest
    @MethodSource("productos")
    public void recuperaPolimorficamenteTodosLosAtributosDelProducto(Producto original) throws Exception {
        original.publicar();

        productos.guardar(original);
        recargar();
        Producto recuperado = productos.obtenerPorId(original.getId()).orElseThrow();
        String url;
        try (var conexion = dataSource.getConnection()) {
            url = conexion.getMetaData().getURL();
        }

        assertThat(url).startsWith("jdbc:h2:mem:");
        assertThat(recuperado).isExactlyInstanceOf(original.getClass());
        assertThat(recuperado).usingRecursiveComparison().isEqualTo(original);
    }

    @Test
    public void actualizaPublicacionYFiltraElCatalogo() {
        Producto producto = DatosPersistenciaPrueba.casco();

        productos.guardar(producto);
        recargar();
        var resultado1 = productos.buscar(new FiltroProducto(null, null, null, null, null, null));
        Producto recuperado = productos.obtenerPorId(producto.getId()).orElseThrow();
        recuperado.publicar();
        productos.guardar(recuperado);
        recargar();
        var resultado2 = productos.buscar(new FiltroProducto(" marca de prueba ", " negro ", 100.0, 500000.0, "cop", null));
        recuperado.despublicar();
        productos.guardar(recuperado);
        recargar();
        var resultado3 = productos.obtenerPorId(producto.getId()).orElseThrow().estaPublicado();

        assertThat(resultado1).isEmpty();
        assertThat(resultado2)
                .extracting(Producto::getId).containsExactly(producto.getId());
        assertThat(resultado3).isFalse();
    }

    @Test
    public void agregaYRetiraCompatibilidadesDeBaulYVisor() {
        var entradas = DatosPersistenciaPrueba.productos().filter(ProductoCompatible.class::isInstance).toList();
        record Observacion(String id, Compatibilidad nueva, Set<Compatibilidad> actualizadas, List<String> catalogo, Set<Compatibilidad> vacias) {}

        var observaciones = new ArrayList<Observacion>();
        for (Producto producto : entradas) {
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
            var actualizadas = copyOf(((ProductoCompatible) productos.obtenerPorId(producto.getId()).orElseThrow()).getCompatibilidades());
            var catalogo = productos.buscar(new FiltroProducto(null, null, null, null, null, nueva)).stream().map(Producto::getId).toList();
            compatible.retirarCompatibilidad(nueva);
            productos.guardar(recuperado);
            recargar();
            var vacias = copyOf(((ProductoCompatible) productos.obtenerPorId(producto.getId()).orElseThrow()).getCompatibilidades());
            observaciones.add(new Observacion(producto.getId(), nueva, actualizadas, catalogo, vacias));
        }

        for (var observacion : observaciones) {
            assertThat(observacion.actualizadas()).containsExactly(observacion.nueva());
            assertThat(observacion.catalogo()).containsExactly(observacion.id());
            assertThat(observacion.vacias()).isEmpty();
        }
    }

    @Test
    public void conservaRelacionOrdenHistoricosFechaYReembolsoAlCambiarPrecioActual() {
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
        var resultado1 = recuperado.getComprador().getId();
        var resultado2 = recuperado.getItems();
        var resultado3 = recuperado.getItems().getFirst().getProducto().getPrecio().monto();
        var resultado4 = recuperado.getItems().getFirst().getSubtotal();
        var resultado5 = recuperado.getTotal();
        var resultado6 = recuperado.getFecha();
        recuperado.completar();
        pedidos.registrar(recuperado);
        recargar();
        Pedido completado = pedidos.obtenerPorId("pedido").orElseThrow();
        completado.solicitarReembolso("Defectuoso");
        pedidos.registrar(completado);
        recargar();
        Pedido reembolsado = pedidos.obtenerPorId("pedido").orElseThrow();

        var resultado11 = entityManager.createQuery("select count(i) from ItemPedidoEntity i", Long.class).getSingleResult();
        long cantidadProductos = productosData.count();

        assertThat(resultado1).isEqualTo(comprador.getId());
        assertThat(resultado2).extracting(ItemPedido::getId).containsExactly("i1", "i2");
        assertThat(resultado3).isEqualTo(900000);
        assertThat(resultado4).isEqualTo(new Precio(1000000, "COP"));
        assertThat(resultado5).isEqualTo(new Precio(1500000, "COP"));
        assertThat(resultado6).isEqualTo(fecha);
        assertThat(reembolsado.getEstadoPedido()).isEqualTo(EstadoPedido.REEMBOLSADA);
        assertThat(reembolsado.getMotivoReembolso()).isEqualTo("Defectuoso");
        assertThat(reembolsado.getTotal()).isEqualTo(new Precio(1500000, "COP"));
        assertThat(cantidadProductos).isEqualTo(2);
        assertThat(resultado11).isEqualTo(2);
    }

    @Test
    public void persisteComentarioConRelacionesYRespuestaSinRelacionInventadaConPedido() {
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
        var resultado1 = comentarios.obtenerPorId("comentario").orElseThrow();
        comentarios.guardar(Comentario.reconstruir("comentario", comprador, producto, 5, "Muy bueno", "Gracias"));
        recargar();
        var resultado2 = comentarios.obtenerPorId("comentario").orElseThrow().getRespuestaVendedor();
        long cantidadProductos = productosData.count();

        assertThat(resultado1).usingRecursiveComparison().isEqualTo(comentario);
        assertThat(resultado2).isEqualTo("Gracias");
        assertThat(cantidadProductos).isEqualTo(1);
    }

    @Test
    public void devuelveOptionalVacioParaRecursosInexistentes() {

        var resultado1 = productos.obtenerPorId("inexistente");
        var resultado2 = compradores.obtenerPorId("inexistente");
        var resultado3 = pedidos.obtenerPorId("inexistente");
        var resultado4 = comentarios.obtenerPorId("inexistente");

        assertThat(resultado1).isEmpty();
        assertThat(resultado2).isEmpty();
        assertThat(resultado3).isEmpty();
        assertThat(resultado4).isEmpty();
    }
}
