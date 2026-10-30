package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.domain.repository.ProductoVentas;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.ProductoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoJpaDataRepository extends JpaRepository<ProductoEntity, String>, JpaSpecificationExecutor<ProductoEntity> {
    @Query("""
            SELECT new com.uniquindio.accesoriosMoto.domain.repository.ProductoVentas(producto.id, SUM(item.cantidad))
            FROM PedidoEntity pedido
            JOIN pedido.items item
            JOIN item.producto producto
            WHERE pedido.estadoPedido = :estado
            GROUP BY producto.id
            ORDER BY SUM(item.cantidad) DESC, producto.id ASC
            """)
    List<ProductoVentas> masVendidos(@Param("estado") EstadoPedido estado, Pageable pagina);

    @Query("""
        SELECT p FROM ProductoEntity p
        WHERE p.estadoPublicacion = :estado
          AND (UPPER(p.marca.nombre) LIKE :patron ESCAPE '!'
               OR UPPER(COALESCE(p.color, '')) LIKE :patron ESCAPE '!')
          AND p.precio.monto >= :precioMinimo
          AND p.precio.monto <= :precioMaximo
          AND (:moneda = '' OR UPPER(p.precio.moneda) = :moneda)
        """)
    Page<ProductoEntity> buscarPorTexto(@Param("estado") EstadoPublicacion estado,
                                        @Param("patron") String patron,
                                        @Param("precioMinimo") double precioMinimo,
                                        @Param("precioMaximo") double precioMaximo,
                                        @Param("moneda") String moneda,
                                        Pageable pagina);
}
