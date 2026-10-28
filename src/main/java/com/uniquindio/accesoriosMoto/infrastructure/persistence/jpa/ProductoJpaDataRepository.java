package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.domain.repository.ProductoVentas;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.ProductoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoJpaDataRepository extends JpaRepository<ProductoEntity, String>,
        JpaSpecificationExecutor<ProductoEntity> {
    @Query("""
            SELECT new ProductoVentas(producto.id, SUM(item.cantidad))
            FROM PedidoEntity pedido
            JOIN pedido.items item
            JOIN item.producto producto
            WHERE pedido.estadoPedido = :estado
            GROUP BY producto.id
            ORDER BY SUM(item.cantidad) DESC, producto.id ASC
            """)
    List<ProductoVentas> masVendidos(@Param("estado") EstadoPedido estado, Pageable pagina);
}
