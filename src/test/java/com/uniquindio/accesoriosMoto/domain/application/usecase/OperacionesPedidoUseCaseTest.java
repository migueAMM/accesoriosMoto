package com.uniquindio.accesoriosMoto.domain.application.usecase;

import com.uniquindio.accesoriosMoto.application.usecase.CompletarPedidoUseCase;
import com.uniquindio.accesoriosMoto.application.usecase.ListarPedidosUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.PedidoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class OperacionesPedidoUseCaseTest {

    private Pedido pedido(String id, String compradorId, LocalDateTime fecha) {
        Comprador comprador = new Comprador(compradorId, "Ana", compradorId + "@correo.com", "clave");
        Producto producto = Visor.crear("v", new Marca("m", "AGV"), new Precio(100, "COP"), "Negro", "Integral", List.of());
        return Pedido.registrar(id, comprador, List.of(ItemPedido.crear("i-" + id, producto, 2)), fecha);
    }

    @Test
    public void completaYPersisteElPedidoRecuperado() {
        var repositorio = spy(new PedidoRepositoryEnMemoria());
        Pedido pedido = pedido("p", "u", LocalDateTime.now());
        repositorio.registrar(pedido);
        clearInvocations(repositorio);

        new CompletarPedidoUseCase(repositorio).ejecutar("p");
        var resultado1 = repositorio.obtenerPorId("p").orElseThrow();

        assertSame(pedido, resultado1);
        assertEquals(EstadoPedido.COMPLETADA, pedido.getEstadoPedido());
        verify(repositorio).registrar(pedido);
        assertEquals(new Precio(200, "COP"), pedido.getTotal());
    }

    @Test
    public void pedidoInexistenteNoSeGuarda() {
        var repositorio = spy(new PedidoRepositoryEnMemoria());

        var accion1Error = catchThrowable(() -> new CompletarPedidoUseCase(repositorio).ejecutar("ausente"));

        assertInstanceOf(NoSuchElementException.class, accion1Error);
        verify(repositorio, never()).registrar(any());
    }

    @Test
    public void estadoIncompatibleNoSeGuarda() {
        var repositorios = new java.util.ArrayList<PedidoRepositoryEnMemoria>();
        for (boolean reembolsar : new boolean[]{false, true}) {
            var repositorio = spy(new PedidoRepositoryEnMemoria());
            Pedido pedido = pedido("p", "u", LocalDateTime.now());
            pedido.completar();
            if (reembolsar) pedido.solicitarReembolso("Devolución");
            repositorio.registrar(pedido);
            clearInvocations(repositorio);
            repositorios.add(repositorio);
        }

        var acciones = repositorios.stream().map(repositorio -> (Executable)
                () -> new CompletarPedidoUseCase(repositorio).ejecutar("p")).toList();

        for (int indice = 0; indice < acciones.size(); indice++) {
            assertThrows(ReglaDominioException.class, acciones.get(indice));
            verify(repositorios.get(indice), never()).registrar(any());
        }
    }

    @Test
    public void listaSoloPedidosDelCompradorConOrdenDeterminista() {
        var repositorio = new PedidoRepositoryEnMemoria();
        var fecha = LocalDateTime.of(2026, 10, 27, 12, 0);
        repositorio.registrar(pedido("b", "u", fecha));
        repositorio.registrar(pedido("a", "u", fecha));
        repositorio.registrar(pedido("viejo", "u", fecha.minusDays(1)));
        repositorio.registrar(pedido("ajeno", "otro", fecha.plusDays(1)));
        var caso = new ListarPedidosUseCase(repositorio);

        var resultado1 = caso.ejecutar("u").stream().map(Pedido::getId).toList();
        var resultado2 = caso.ejecutar("ausente").isEmpty();

        assertEquals(List.of("a", "b", "viejo"), resultado1);
        assertTrue(resultado2);
    }
}
