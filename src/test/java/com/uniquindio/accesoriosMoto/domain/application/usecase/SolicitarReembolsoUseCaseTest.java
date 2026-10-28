package com.uniquindio.accesoriosMoto.domain.application.usecase;

import com.uniquindio.accesoriosMoto.application.usecase.SolicitarReembolsoUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.PedidoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class SolicitarReembolsoUseCaseTest {

    private Pedido pedido(boolean completar) {
        Producto producto = Visor.crear("v1", new Marca("m", "AGV"), new Precio(100, "COP"), "Claro", "Integral", List.of());
        Pedido pedido = Pedido.registrar("p1", new Comprador("u1", "Ana", "ana@correo.com", "clave"), List.of(ItemPedido.crear("i1", producto, 1)), LocalDateTime.now());
        if (completar) pedido.completar();
        return pedido;
    }

    @Test
    public void solicitaYConservaReembolsoEnRepositorioEnMemoria() {
        PedidoRepositoryEnMemoria repository = new PedidoRepositoryEnMemoria();
        repository.registrar(pedido(true));
        SolicitarReembolsoUseCase caso = new SolicitarReembolsoUseCase(repository);

        caso.ejecutar("p1", "u1", "Accesorio dañado");
        Pedido guardado = repository.obtenerPorId("p1").orElseThrow();

        assertNotNull(guardado);
        assertEquals(EstadoPedido.REEMBOLSADA, guardado.getEstadoPedido());
        assertEquals("Accesorio dañado", guardado.getMotivoReembolso());
        assertEquals(1, repository.cantidadRegistrada());
    }

    @Test
    public void guardaSoloDespuesDeUnaSolicitudValida() {
        var observacion0 = new java.util.ArrayList<Pedido>();
        var observacion1 = new java.util.ArrayList<EstadoPedido>();
        var observacion2 = new java.util.ArrayList<String>();

        Pedido pedido = pedido(true);
        class RepositorioPrueba extends PedidoRepositoryEnMemoria {
            int registros;
            public Optional<Pedido> obtenerPorId(String id) {
                return Optional.of(pedido);
            }
            public void registrar(Pedido recibido) {
                observacion0.add( recibido);
                observacion1.add( recibido.getEstadoPedido());
                observacion2.add( recibido.getMotivoReembolso());
                registros++;
            }
        }
        RepositorioPrueba repository = new RepositorioPrueba();
        SolicitarReembolsoUseCase caso = new SolicitarReembolsoUseCase(repository);

        var error1 = catchThrowable(() -> caso.ejecutar("p1", "u1", " "));
        var contador2 =  repository.registros;
        caso.ejecutar("p1", "u1","Motivo válido");
        var contador3 =  repository.registros;
        var error4 = catchThrowable(() -> caso.ejecutar("p1", "u1", "Otro motivo"));
        var contador5 =  repository.registros;

        assertInstanceOf(ReglaDominioException.class, error1);
        assertEquals(0, contador2);
        assertEquals(1, contador3);
        assertInstanceOf(ReglaDominioException.class, error4);
        assertEquals(1, contador5);
        for (var observacion : observacion0) { assertSame(pedido, observacion); }
        for (var observacion : observacion1) { assertEquals(EstadoPedido.REEMBOLSADA, observacion); }
        for (var observacion : observacion2) { assertEquals("Motivo válido", observacion); }
    }

    @Test
    public void noRegistraCuandoPedidoNoExisteOEstaPendiente() {
        var llamadasProhibidas = new java.util.ArrayList<String>();

        Pedido pendiente = pedido(false);
        PedidoRepository repository = new PedidoRepositoryEnMemoria() {
            public Optional<Pedido> obtenerPorId(String id) {
                return "p1".equals(id) ? Optional.of(pendiente) : Optional.empty();
            }
            public void registrar(Pedido pedido) {
                llamadasProhibidas.add("No debe registrar una solicitud inválida");
            }
        };
        SolicitarReembolsoUseCase caso = new SolicitarReembolsoUseCase(repository);

        var accion1Error = catchThrowable(() -> caso.ejecutar("inexistente", "u1", "Motivo"));
        var accion2Error = catchThrowable(() -> caso.ejecutar("p1", "u1", "Motivo"));

        assertInstanceOf(NoSuchElementException.class, accion1Error);
        assertInstanceOf(ReglaDominioException.class, accion2Error);
        assertEquals(EstadoPedido.PENDIENTE, pendiente.getEstadoPedido());
        assertNull(pendiente.getMotivoReembolso());
        assertTrue(llamadasProhibidas.isEmpty(), llamadasProhibidas.toString());
    }
}
