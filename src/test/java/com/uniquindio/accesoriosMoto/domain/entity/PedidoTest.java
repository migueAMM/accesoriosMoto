package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PedidoTest {

    private Comprador compradorPrueba(){
        return new Comprador("C1", "Juan Perez", "juanP@gmail.com", "456");
    }

    @Test
    public void noSePuedeSolicitarReembolsoDeUnPedidoPendiente(){
        Pedido pedido = new Pedido("P1", compradorPrueba(), List.of(), EstadoPedido.PENDIENTE, LocalDateTime.now(), new Precio(500000, "COP") );

        assertThrows(ReglaDominioException.class, () -> {
            pedido.solicitarReembolso("No llego a tiempo");
        });

        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstadoPedido());
    }

    @Test
    public void noSePuedeSolicitarReembolosoDeUnPedidoYaReembolsado(){
        Pedido pedido = new Pedido("P2", compradorPrueba(), List.of(), EstadoPedido.REEMBOLSADA, LocalDateTime.now(), new Precio(100000, "COP"));

        assertThrows(ReglaDominioException.class, () -> {
            pedido.solicitarReembolso("Quiero reembolso otra vez");
        });

        assertEquals(EstadoPedido.REEMBOLSADA, pedido.getEstadoPedido());
    }
}
