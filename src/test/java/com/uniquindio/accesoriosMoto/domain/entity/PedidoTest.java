package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PedidoTest {

    private Comprador compradorPrueba(){
        return new Comprador("C1", "Juan Perez", "juanP@gmail.com", "456");
    }

    private ItemPedido itemPrueba() {
        Producto casco = Casco.crear("P2", new Marca("M2", "AGV"), "K-3", "Blanco con plateado", Talla.M, new Precio(200000, "COP"), Proteccion.NIVEL_3, "DOT");
        return ItemPedido.crear("i1", casco, 1, new Precio(200000, "COP"));
    }

    private Pedido pedidoPendiente(){
        return Pedido.registrar("ped1", compradorPrueba(), List.of(itemPrueba()), LocalDateTime.now(), new Precio(200000, "COP"));
    }

    @Test
    public void noSePuedeSolicitarReembolsoDeUnPedidoPendiente(){
        Pedido pedido = pedidoPendiente();
        assertThrows(ReglaDominioException.class, () -> pedido.solicitarReembolso("No llego a tiempo"));
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstadoPedido());
    }

    @Test
    public void noSePuedeSolicitarReembolosoDeUnPedidoYaReembolsado(){
        Pedido pedido = pedidoPendiente();
        pedido.completar();
        pedido.solicitarReembolso("Primer reembolso");

        assertThrows(ReglaDominioException.class, () -> pedido.solicitarReembolso("otra vez"));
        assertEquals(EstadoPedido.REEMBOLSADA, pedido.getEstadoPedido());
    }
}
