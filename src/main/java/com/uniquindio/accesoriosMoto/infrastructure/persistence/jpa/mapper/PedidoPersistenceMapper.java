package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper;

import com.uniquindio.accesoriosMoto.domain.entity.ItemPedido;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class PedidoPersistenceMapper {
    private final CompradorPersistenceMapper compradores;
    private final ProductoPersistenceMapper productos;
    public PedidoPersistenceMapper(CompradorPersistenceMapper compradores, ProductoPersistenceMapper productos) {
        this.compradores = compradores;
        this.productos = productos;
    }
    public PedidoEntity toEntity(Pedido pedido, CompradorEntity comprador, Map<String, ProductoEntity> referencias) {
        PedidoEntity entidad = new PedidoEntity();
        entidad.setId(pedido.getId());
        entidad.setComprador(comprador);
        entidad.setEstadoPedido(pedido.getEstadoPedido());
        entidad.setFecha(pedido.getFecha());
        entidad.setTotal(new PrecioEmbeddable(pedido.getTotal().monto(), pedido.getTotal().moneda()));
        entidad.setMotivoReembolso(pedido.getMotivoReembolso());
        for (ItemPedido item : pedido.getItems()) {
            ItemPedidoEntity e = new ItemPedidoEntity();
            e.setId(item.getId());
            e.setPedido(entidad);
            e.setProducto(Objects.requireNonNull(referencias.get(item.getProducto().getId())));
            e.setCantidad(item.getCantidad());
            e.setSubtotal(new PrecioEmbeddable(item.getSubtotal().monto(), item.getSubtotal().moneda()));
            entidad.getItems().add(e);
        }
        return entidad;
    }
    public Pedido toDomain(PedidoEntity entidad) {
        List<ItemPedido> items = entidad.getItems().stream().map(e -> ItemPedido.reconstruir(
                e.getId(), productos.toDomain(e.getProducto()), e.getCantidad(),
                new Precio(e.getSubtotal().getMonto(), e.getSubtotal().getMoneda()))).toList();
        return Pedido.reconstruir(entidad.getId(), compradores.toDomain(entidad.getComprador()), items,
                entidad.getEstadoPedido(), entidad.getFecha(),
                new Precio(entidad.getTotal().getMonto(), entidad.getTotal().getMoneda()), entidad.getMotivoReembolso());
    }
}
