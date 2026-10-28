package com.uniquindio.accesoriosMoto.domain.infraestructure.persistence;

import com.uniquindio.accesoriosMoto.application.port.CodificadorContrasena;
import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.repository.CompradorRepository;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.ProductoJpaDataRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ProductoRepositoryIntegrationTest {
    @Autowired
    private ProductoRepository productos;
    @Autowired private PedidoRepository pedidos;
    @Autowired private CompradorRepository compradores;
    @Autowired private ProductoJpaDataRepository consultasProductos;
    @Autowired private CodificadorContrasena contrasenas;
    @Autowired private EntityManager entityManager;
    @Autowired private DataSource dataSource;

    @Test
    void deberiaGuardarYRecuperarCascoConPrecioYEstado() throws Exception {
        // Arrange
        Marca marca = new Marca("marca-casco", "AGV");
        Casco casco = Casco.crear("casco-conocido", marca, "K3", "Negro", Talla.M,
                new Precio(500000, "COP"), Proteccion.NIVEL_1, "DOT");
        casco.publicar();

        // Act
        productos.guardar(casco);
        entityManager.flush();
        entityManager.clear();
        Producto recuperado = productos.obtenerPorId("casco-conocido").orElseThrow();

        // Assert
        try (var conexion = dataSource.getConnection()) {
            assertTrue(conexion.getMetaData().getURL().startsWith("jdbc:h2:mem:"));
        }
        Casco recuperadoComoCasco = assertInstanceOf(Casco.class, recuperado);
        assertNotSame(casco, recuperado);
        assertEquals("casco-conocido", recuperado.getId());
        assertEquals(marca, recuperado.getMarca());
        assertEquals(500000, recuperado.getPrecio().monto());
        assertEquals("COP", recuperado.getPrecio().moneda());
        assertEquals(EstadoPublicacion.PUBLICADO, recuperado.getEstadoPublicacion());
        assertEquals("Negro", recuperado.getColor());
        assertEquals("K3", recuperadoComoCasco.getModelo());
        assertEquals(Talla.M, recuperadoComoCasco.getTalla());
        assertEquals(Proteccion.NIVEL_1, recuperadoComoCasco.getProteccion());
        assertEquals("DOT", recuperadoComoCasco.getCertificacionSeguridad());
    }

    @Test
    void deberiaOrdenarProductosPorCantidadVendidaDescendente() {
        // Arrange
        Comprador comprador = new Comprador("comprador-ranking", "Ana", "ranking@ejemplo.com",
                contrasenas.codificar("ClaveRanking123"));
        compradores.guardar(comprador);
        Producto a = casco("A");
        Producto b = casco("B");
        Producto c = casco("C");
        productos.guardar(a);
        productos.guardar(b);
        productos.guardar(c);
        productos.guardar(casco("sin-ventas"));

        Pedido primero = pedido("venta-1", comprador,
                ItemPedido.crear("i-a-1", a, 2), ItemPedido.crear("i-b-1", b, 1), ItemPedido.crear("i-c-1", c, 1));
        primero.completar();
        pedidos.registrar(primero);
        Pedido segundo = pedido("venta-2", comprador,
                ItemPedido.crear("i-a-2", a, 3), ItemPedido.crear("i-b-2", b, 2));
        segundo.completar();
        pedidos.registrar(segundo);

        Pedido pendiente = pedido("pendiente", comprador, ItemPedido.crear("i-pendiente", c, 100));
        pedidos.registrar(pendiente);
        Pedido reembolsado = pedido("reembolsado", comprador, ItemPedido.crear("i-reembolso", c, 100));
        reembolsado.completar();
        reembolsado.solicitarReembolso("Devolución");
        pedidos.registrar(reembolsado);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<ProductoVentas> ranking = consultasProductos.obtenerRankingMasVendidos();

        // Assert
        assertEquals(3, ranking.size());
        assertEquals("A", ranking.get(0).getProductoId());
        assertEquals(5L, ranking.get(0).getCantidadVendida());
        assertEquals("B", ranking.get(1).getProductoId());
        assertEquals(3L, ranking.get(1).getCantidadVendida());
        assertEquals("C", ranking.get(2).getProductoId());
        assertEquals(1L, ranking.get(2).getCantidadVendida());
    }

    private Casco casco(String id) {
        return Casco.crear(id, new Marca("marca-ranking", "AGV"), "K3", "Negro", Talla.M,
                new Precio(100, "COP"), Proteccion.NIVEL_1, "DOT");
    }

    private Pedido pedido(String id, Comprador comprador, ItemPedido... items) {
        return Pedido.registrar(id, comprador, List.of(items), LocalDateTime.of(2026, 10, 26, 12, 0));
    }
}
