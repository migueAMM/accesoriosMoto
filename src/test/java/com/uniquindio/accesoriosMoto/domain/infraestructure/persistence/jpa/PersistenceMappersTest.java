package com.uniquindio.accesoriosMoto.domain.infraestructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.PedidoEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.ComentarioPersistenceMapper;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.CompradorPersistenceMapper;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.PedidoPersistenceMapper;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.ProductoPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PersistenceMappersTest {

    private final ProductoPersistenceMapper productos = new ProductoPersistenceMapper();
    private final CompradorPersistenceMapper compradores = new CompradorPersistenceMapper();
    private final PedidoPersistenceMapper pedidos = new PedidoPersistenceMapper(compradores, productos);
    private final ComentarioPersistenceMapper comentarios = new ComentarioPersistenceMapper(compradores, productos);

    static Stream<Producto> productos() { return DatosPersistenciaPrueba.productos(); }

    @ParameterizedTest
    @MethodSource("productos")
    void conservaTipoEstadoAtributosYCompatibilidades(Producto original) {
        original.publicar();
        Producto recuperado = productos.toDomain(productos.toEntity(original));
        assertThat(recuperado).isExactlyInstanceOf(original.getClass());
        assertThat(recuperado).usingRecursiveComparison().isEqualTo(original);
    }

    @Test
    void conservaCompradorSinModificarSuIdentidadNiDatos() {
        Comprador original = new Comprador("c", "Ana", "ana@ejemplo.com", "clave");
        assertThat(compradores.toDomain(compradores.toEntity(original))).usingRecursiveComparison().isEqualTo(original);
    }

    @Test
    void restauraPedidoSinRecalcularHistoricosYConservaReembolso() {
        Comprador comprador = new Comprador("c", "Ana", "ana@ejemplo.com", "clave");
        Producto producto = DatosPersistenciaPrueba.casco();
        ItemPedido item = ItemPedido.reconstruir("i", producto, 2, new Precio(200, "COP"));
        LocalDateTime fecha = LocalDateTime.of(2026, 1, 1, 12, 0);
        Pedido original = Pedido.reconstruir("p", comprador, List.of(item), EstadoPedido.REEMBOLSADA, fecha, new Precio(200, "COP"), "Defectuoso");
        PedidoEntity entidad = pedidos.toEntity(original, compradores.toEntity(comprador),
                Map.of(producto.getId(), productos.toEntity(producto)));
        Pedido recuperado = pedidos.toDomain(entidad);
        assertThat(recuperado).usingRecursiveComparison().isEqualTo(original);
        assertThat(recuperado.getTotal().monto()).isEqualTo(200);
        assertThat(recuperado.getItems().getFirst().getSubtotal().monto()).isEqualTo(200);
        assertThat(recuperado.getItems().getFirst().getProducto().getPrecio().monto()).isEqualTo(500000);
    }

    @Test
    void restauraComentarioConRespuestaSinRevalidarUnaCompra() {
        Comprador comprador = new Comprador("c", "Ana", "ana@ejemplo.com", "clave");
        Producto producto = DatosPersistenciaPrueba.casco();
        Comentario original = Comentario.reconstruir("comentario", comprador, producto, 5, "Excelente", "Gracias");
        Comentario recuperado = comentarios.toDomain(comentarios.toEntity(original, compradores.toEntity(comprador), productos.toEntity(producto)));
        assertThat(recuperado).usingRecursiveComparison().isEqualTo(original);
    }
}
