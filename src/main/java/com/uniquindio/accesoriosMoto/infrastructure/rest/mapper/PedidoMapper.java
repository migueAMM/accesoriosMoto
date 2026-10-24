package com.uniquindio.accesoriosMoto.infrastructure.rest.mapper;

import com.uniquindio.accesoriosMoto.application.dto.response.ItemPedidoResponse;
import com.uniquindio.accesoriosMoto.application.dto.response.PedidoResponse;
import com.uniquindio.accesoriosMoto.domain.entity.ItemPedido;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {
    public PedidoResponse toResponse(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getEstadoPedido().name(),
                pedido.getFecha(),
                pedido.getItems().stream().map(this::toItemResponse).toList(),
                pedido.getTotal().monto(),
                pedido.getTotal().moneda());
    }

    public ItemPedidoResponse toItemResponse(ItemPedido item) {
        return new ItemPedidoResponse(
                item.getId(),
                item.getProducto().getId(),
                item.getCantidad(),
                item.getSubtotal().monto(),
                item.getSubtotal().moneda());
    }
}
