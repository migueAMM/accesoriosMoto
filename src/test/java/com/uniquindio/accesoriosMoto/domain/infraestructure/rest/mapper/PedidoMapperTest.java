package com.uniquindio.accesoriosMoto.domain.infraestructure.rest.mapper;

import com.uniquindio.accesoriosMoto.application.dto.response.ItemPedidoResponse;
import com.uniquindio.accesoriosMoto.application.dto.response.PedidoResponse;
import com.uniquindio.accesoriosMoto.domain.entity.ItemPedido;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.infraestructure.rest.DatosRestPrueba;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.PedidoMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PedidoMapperTest {

    private final PedidoMapper mapper = new PedidoMapper();

    @Test
    void mapeaDetalleConIdsEImportesSinExponerEntidades() {
        Pedido pedido = DatosRestPrueba.pedido("ped1");

        PedidoResponse response = mapper.toResponse(pedido);

        assertEquals(new PedidoResponse("ped1", "PENDIENTE", LocalDateTime.of(2026, 10, 24, 10, 0), List.of(new ItemPedidoResponse("i1", "p1", 2, 200, "COP")), 200, "COP"), response);
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstadoPedido());
    }

    @Test
    void utilizaImportesHistoricosAunqueCambiePrecioVigente() {
        class ProductoVariable extends Producto {
            ProductoVariable() {
                super("p1", new Marca("m1", "Marca"), new Precio(100, "USD"), "Negro", EstadoPublicacion.NO_PUBLICADO);
            }
            void cambiarPrecio(Precio precio) { this.precio = precio; }
        }
        ProductoVariable producto = new ProductoVariable();
        ItemPedido item = ItemPedido.crear("i1", producto, 2);
        Pedido pedido = Pedido.registrar("ped1", DatosRestPrueba.comprador(), List.of(item), LocalDateTime.now());
        producto.cambiarPrecio(new Precio(900, "USD"));

        PedidoResponse response = mapper.toResponse(pedido);

        assertEquals(200, response.totalMonto());
        assertEquals("USD", response.totalMoneda());
        assertEquals(new ItemPedidoResponse("i1", "p1", 2, 200, "USD"), response.items().getFirst());
        assertEquals(new Precio(900, "USD"), producto.getPrecio());
    }

    @Test
    void reflejaEstadosCompletadoYReembolsado() {
        Pedido pedido = DatosRestPrueba.pedido("ped1");
        pedido.completar();
        assertEquals("COMPLETADA", mapper.toResponse(pedido).estado());
        pedido.solicitarReembolso("Dañado");
        assertEquals("REEMBOLSADA", mapper.toResponse(pedido).estado());
    }
}
