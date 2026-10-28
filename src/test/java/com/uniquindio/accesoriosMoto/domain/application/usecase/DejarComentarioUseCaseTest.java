package com.uniquindio.accesoriosMoto.domain.application.usecase;

import com.uniquindio.accesoriosMoto.application.usecase.DejarComentarioUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ComentarioRepository;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.service.VerificadorDeCompra;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.ComentarioRepositoryEnMemoria;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.CompradorRepositoryEnMemoria;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.PedidoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DejarComentarioUseCaseTest {

    private final Comprador comprador = new Comprador("u1", "Ana", "ana@correo.com", "clave");

    private Pedido pedido(boolean completar) {
        Producto producto = Visor.crear("v1", new Marca("m", "AGV"), new Precio(100, "COP"), "Claro", "Integral", List.of());
        Pedido pedido = Pedido.registrar("p1", comprador, List.of(ItemPedido.crear("i1", producto, 1)), LocalDateTime.now());
        if (completar) pedido.completar();
        return pedido;
    }

    @Test
    public void guardaComentarioDeCompraCompletadaEnMemoria() {
        CompradorRepositoryEnMemoria compradores = new CompradorRepositoryEnMemoria();
        compradores.guardar(comprador);
        PedidoRepositoryEnMemoria pedidos = new PedidoRepositoryEnMemoria();
        ComentarioRepositoryEnMemoria comentarios = new ComentarioRepositoryEnMemoria();
        Pedido pedido = pedido(true);
        pedidos.registrar(pedido);
        DejarComentarioUseCase caso = new DejarComentarioUseCase(compradores, pedidos, comentarios, new com.uniquindio.accesoriosMoto.domain.service.VerificadorDeCompra(pedidos));

        Comentario resultado = caso.ejecutar("c1", comprador.getId(), "p1", "v1", 5, "Excelente visor");
        var resultado1 = comentarios.obtenerPorId("c1").orElseThrow();

        var resultado9 = comentarios.obtenerPorId("inexistente").isEmpty();

        assertSame(resultado, resultado1);
        assertEquals("c1", resultado.getId());
        assertEquals(5, resultado.getCalificacion());
        assertEquals("Excelente visor", resultado.getTexto());
        assertSame(comprador, resultado.getComprador());
        assertSame(pedido.getItems().getFirst().getProducto(), resultado.getProducto());
        assertEquals(EstadoPedido.COMPLETADA, pedido.getEstadoPedido());
        assertEquals(1, pedidos.cantidadRegistrada());
        assertTrue(resultado9);
    }

    @Test
    public void noGuardaCuandoNoExistePedidoOFallaUnaRegla() {
        var llamadasProhibidas = new java.util.ArrayList<String>();

        CompradorRepositoryEnMemoria compradores = new CompradorRepositoryEnMemoria();
        compradores.guardar(comprador);
        compradores.guardar(new Comprador("u2", "Otro", "otro@correo.com", "clave"));
        PedidoRepositoryEnMemoria pedidos = new PedidoRepositoryEnMemoria();
        ComentarioRepository comentarios = new ComentarioRepository() {
            public void guardar(Comentario comentario) { llamadasProhibidas.add("No debe guardar un comentario inválido"); }
            public Optional<Comentario> obtenerPorId(String id) { return Optional.empty(); }
        };
        DejarComentarioUseCase caso = new DejarComentarioUseCase(compradores, pedidos, comentarios, new VerificadorDeCompra(pedidos));

        var error1 = catchThrowable( () -> caso.ejecutar("c1", "u1", "no", "v1", 5, "Opinión"));
        pedidos.registrar(pedido(false));
        var error2 = catchThrowable( () -> caso.ejecutar("c1", "u1", "p1", "v1", 5, "Opinión"));
        pedidos.registrar(pedido(true));
        var error3 =catchThrowable( () -> caso.ejecutar("c1", "u1", "p1", "otro", 5, "Opinión"));
        var error4 = catchThrowable( () -> caso.ejecutar("c1", "u2", "p1", "v1", 5, "Opinión"));
        var error5 = catchThrowable( () -> caso.ejecutar("c1", "u1", "p1", "v1", 6, "Opinión"));
        var error6 = catchThrowable( () -> caso.ejecutar("c1", "u1", "p1", "v1", 5, " "));

        assertInstanceOf(NoSuchElementException.class, error1);
        assertInstanceOf(ReglaDominioException.class, error2);
        assertInstanceOf(ReglaDominioException.class, error3);
        assertInstanceOf(AccessDeniedException.class, error4);
        assertInstanceOf(ReglaDominioException.class, error5);
        assertInstanceOf(ReglaDominioException.class, error6);
        assertTrue(llamadasProhibidas.isEmpty(), llamadasProhibidas.toString());
    }

    @Test
    public void noGuardaNiConsultaPedidoSiCompradorNoExiste() {
        var llamadasProhibidas = new java.util.ArrayList<String>();

        CompradorRepositoryEnMemoria compradores = new CompradorRepositoryEnMemoria();

        PedidoRepository pedidos = new PedidoRepositoryEnMemoria() {

            public Optional<Pedido> obtenerPorId(String id) {
                llamadasProhibidas.add("No debe consultar el pedido sin comprador"); return Optional.empty();
            }
            public void registrar(Pedido pedido) {
                llamadasProhibidas.add("Comentar no registra pedidos");
            }
        };
        ComentarioRepository comentarios = new ComentarioRepository() {
            public void guardar(Comentario comentario) {
                llamadasProhibidas.add("No debe guardar si no existe comprador");
            }
            public Optional<Comentario> obtenerPorId(String id) {
                return Optional.empty();
            }
        };
        DejarComentarioUseCase caso = new DejarComentarioUseCase(compradores, pedidos, comentarios, new VerificadorDeCompra(pedidos));

        var error = catchThrowable(() -> caso.ejecutar("c1", "inexistente", "p1", "v1", 5, "Opinión"));

        assertInstanceOf(NoSuchElementException.class, error);

        assertNotNull(error);
        assertTrue(llamadasProhibidas.isEmpty(), llamadasProhibidas.toString());
    }
}
