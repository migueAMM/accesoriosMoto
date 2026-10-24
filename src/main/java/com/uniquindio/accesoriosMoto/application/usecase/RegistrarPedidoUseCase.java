package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.application.dto.request.ItemPedidoRequest;
import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.entity.ItemPedido;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.CompradorRepository;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RegistrarPedidoUseCase {
    private final CompradorRepository compradorRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;

    public RegistrarPedidoUseCase(CompradorRepository compradorRepository, ProductoRepository productoRepository, PedidoRepository pedidoRepository) {
        this.compradorRepository = compradorRepository;
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido ejecutar(String idPedido, String compradorId, List<ItemPedidoRequest> items) {
        Comprador comprador = compradorRepository.obtenerPorId(compradorId)
                .orElseThrow(() -> new ReglaDominioException("No existe un comprador con ese id"));
        if (items == null) {
            throw new ReglaDominioException("Debe proporcionar la lista de ítems del pedido");
        }
        List<ItemPedido> itemsPedido = new ArrayList<>();
        for (ItemPedidoRequest item : items) {
            itemsPedido.add(crearItem(item));
        }
        Pedido pedido = Pedido.registrar(idPedido, comprador, itemsPedido, LocalDateTime.now());
        pedidoRepository.registrar(pedido);
        return pedido;
    }

    private ItemPedido crearItem(ItemPedidoRequest item) {
        if (item == null) {
            throw new ReglaDominioException("Los ítems solicitados no pueden ser nulos");
        }
        if (item.cantidad() == null) {
            throw new ReglaDominioException("Debe indicar la cantidad del ítem");
        }
        Producto producto = productoRepository.obtenerPorId(item.productoId())
                .orElseThrow(() -> new ReglaDominioException("No existe un producto con el id: " + item.productoId()));
        return ItemPedido.crear(UUID.randomUUID().toString(), producto, item.cantidad());
    }


}

