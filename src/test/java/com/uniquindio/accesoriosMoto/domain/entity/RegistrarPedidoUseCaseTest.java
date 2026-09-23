package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.application.RegistrarPedidoUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.Casco;
import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrarPedidoUseCaseTest {

    private PedidoRepositoryEnMemoria pedidoRepository;
    private RegistrarPedidoUseCase useCase;

    @BeforeEach
    void setUp() {
        pedidoRepository = new PedidoRepositoryEnMemoria();
        useCase = new RegistrarPedidoUseCase(pedidoRepository);
    }

    private Comprador compradorPrueba() {
        return new Comprador("C1", "Ana Gomez", "anag@gmail.com", "123");
    }

    private Producto productoPrueba() {
        return new Casco("P1", new Marca("M1", "AGV"), new Precio(500000, "COP"), "negro",
                EstadoPublicacion.PUBLICADO, "K-3", Talla.M, Proteccion.NIVEL_2, "DOT");
    }

    @Test
    void unPedidoValidoSeRegistraCorrectamente() {
        Pedido pedido = new Pedido("PED1", compradorPrueba(), List.of(productoPrueba()),
                EstadoPedido.PENDIENTE, LocalDateTime.now(), new Precio(500000, "COP"));

        useCase.ejecutar(pedido);

        assertEquals(1, pedidoRepository.cantidadRegistrada());
        assertTrue(pedidoRepository.contiene(pedido));
    }

    @Test
    void noSePuedeRegistrarUnPedidoSinProductos() {
        Pedido pedido = new Pedido("PED2", compradorPrueba(), List.of(),
                EstadoPedido.PENDIENTE, LocalDateTime.now(), new Precio(500000, "COP"));

        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar(pedido));
        assertEquals(0, pedidoRepository.cantidadRegistrada());
    }

    @Test
    void noSePuedeRegistrarUnPedidoConTotalCero() {
        Pedido pedido = new Pedido("PED3", compradorPrueba(), List.of(productoPrueba()),
                EstadoPedido.PENDIENTE, LocalDateTime.now(), new Precio(0, "COP"));

        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar(pedido));
        assertEquals(0, pedidoRepository.cantidadRegistrada());
    }

    @Test
    void noSePuedeRegistrarUnPedidoNulo() {
        assertThrows(ReglaDominioException.class, () -> useCase.ejecutar(null));
        assertEquals(0, pedidoRepository.cantidadRegistrada());
    }

    /**
     * Doble de prueba en memoria del puerto PedidoRepository. Permite probar
     * el caso de uso de forma aislada, sin depender de la infraestructura
     * (JPA/MariaDB), y verificar si realmente se intentó persistir el pedido.
     */
    private static class PedidoRepositoryEnMemoria implements PedidoRepository {
        private final List<Pedido> pedidosRegistrados = new ArrayList<>();

        @Override
        public void registrar(Pedido pedido) {
            pedidosRegistrados.add(pedido);
        }

        boolean contiene(Pedido pedido) {
            return pedidosRegistrados.contains(pedido);
        }

        int cantidadRegistrada() {
            return pedidosRegistrados.size();
        }
    }
}