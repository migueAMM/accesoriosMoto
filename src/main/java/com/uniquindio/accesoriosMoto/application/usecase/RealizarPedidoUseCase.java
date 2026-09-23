package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

public class RealizarPedidoUseCase {

    private final PedidoRepository pedidoRepository;

    public RealizarPedidoUseCase(PedidoRepository pedidoRepository){
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido ejecutar(String id, Comprador comprador, List<Producto> productos, Precio precioTotal) {
        Pedido pedido = new Pedido(id, comprador, productos, EstadoPedido.PENDIENTE, LocalDateTime.now(), precioTotal);
        pedidoRepository.guardar(pedido);
        return pedido;
    }
}
