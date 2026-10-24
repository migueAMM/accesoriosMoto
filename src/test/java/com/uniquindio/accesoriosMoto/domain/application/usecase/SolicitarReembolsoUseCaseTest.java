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
import java.util.Optional;

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
    void solicitaYConservaReembolsoEnRepositorioEnMemoria() {
        PedidoRepositoryEnMemoria repository = new PedidoRepositoryEnMemoria();
        repository.registrar(pedido(true));
        SolicitarReembolsoUseCase caso = new SolicitarReembolsoUseCase(repository);

        Pedido resultado = caso.ejecutar("p1", "Accesorio dañado");

        Pedido guardado = repository.obtenerPorId("p1").orElseThrow();
        assertSame(resultado, guardado);
        assertEquals(EstadoPedido.REEMBOLSADA, guardado.getEstadoPedido());
        assertEquals("Accesorio dañado", guardado.getMotivoReembolso());
        assertEquals(1, repository.cantidadRegistrada());
    }

    @Test
    void guardaSoloDespuesDeUnaSolicitudValida() {
        Pedido pedido = pedido(true);
        class RepositorioPrueba implements PedidoRepository {
            int registros;
            public Optional<Pedido> obtenerPorId(String id) { return Optional.of(pedido); }
            public void registrar(Pedido recibido) {
                assertSame(pedido, recibido);
                assertEquals(EstadoPedido.REEMBOLSADA, recibido.getEstadoPedido());
                assertEquals("Motivo válido", recibido.getMotivoReembolso());
                registros++;
            }
        }
        RepositorioPrueba repository = new RepositorioPrueba();
        SolicitarReembolsoUseCase caso = new SolicitarReembolsoUseCase(repository);

        assertThrows(ReglaDominioException.class, () -> caso.ejecutar("p1", " "));
        assertEquals(0, repository.registros);
        assertSame(pedido, caso.ejecutar("p1", "Motivo válido"));
        assertEquals(1, repository.registros);
        assertThrows(ReglaDominioException.class, () -> caso.ejecutar("p1", "Otro motivo"));
        assertEquals(1, repository.registros);
    }

    @Test
    void noRegistraCuandoPedidoNoExisteOEstaPendiente() {
        Pedido pendiente = pedido(false);
        PedidoRepository repository = new PedidoRepository() {
            public Optional<Pedido> obtenerPorId(String id) {
                return "p1".equals(id) ? Optional.of(pendiente) : Optional.empty();
            }
            public void registrar(Pedido pedido) { fail("No debe registrar una solicitud inválida"); }
        };
        SolicitarReembolsoUseCase caso = new SolicitarReembolsoUseCase(repository);

        assertThrows(ReglaDominioException.class, () -> caso.ejecutar("inexistente", "Motivo"));
        assertThrows(ReglaDominioException.class, () -> caso.ejecutar("p1", "Motivo"));
        assertEquals(EstadoPedido.PENDIENTE, pendiente.getEstadoPedido());
        assertNull(pendiente.getMotivoReembolso());
    }
}
