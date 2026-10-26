package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoJpaDataRepository extends JpaRepository<ProductoEntity, String> {
    @Query("""
            SELECT producto.id AS productoId, SUM(item.cantidad) AS cantidadVendida
            FROM PedidoEntity pedido
            JOIN pedido.items item
            JOIN item.producto producto
            WHERE pedido.estadoPedido = com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido.COMPLETADA
            GROUP BY producto.id
            ORDER BY SUM(item.cantidad) DESC, producto.id ASC
            """)
    List<ProductoVentas> obtenerRankingMasVendidos();
}
