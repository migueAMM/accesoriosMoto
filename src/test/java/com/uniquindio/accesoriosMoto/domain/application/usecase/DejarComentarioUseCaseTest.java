package com.uniquindio.accesoriosMoto.domain.application.usecase;

import com.uniquindio.accesoriosMoto.application.exception.AccesoDenegadoException;
import com.uniquindio.accesoriosMoto.application.usecase.DejarComentarioUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.ComentarioRepository;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.ComentarioRepositoryEnMemoria;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.CompradorRepositoryEnMemoria;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.PedidoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class DejarComentarioUseCaseTest {

    private final Comprador comprador = new Comprador("u1", "Ana", "ana@correo.com", "clave");

    private Pedido pedido(boolean completar) {
        Producto producto = Visor.crear("v1", new Marca("m", "AGV"), new Precio(100, "COP"), "Claro", "Integral", List.of());
        Pedido pedido = Pedido.registrar("p1", comprador, List.of(ItemPedido.crear("i1", producto, 1)), LocalDateTime.now());
        if (completar) pedido.completar();
        return pedido;
    }

    @Test
    void guardaComentarioDeCompraCompletadaEnMemoria() {
        CompradorRepositoryEnMemoria compradores = new CompradorRepositoryEnMemoria();
        compradores.guardar(comprador);
        PedidoRepositoryEnMemoria pedidos = new PedidoRepositoryEnMemoria();
        ComentarioRepositoryEnMemoria comentarios = new ComentarioRepositoryEnMemoria();
        Pedido pedido = pedido(true);
        pedidos.registrar(pedido);
        DejarComentarioUseCase caso = new DejarComentarioUseCase(compradores, pedidos, comentarios);

        Comentario resultado = caso.ejecutar("c1", comprador.getId(), "p1", "v1", 5, "Excelente visor");

        assertSame(resultado, comentarios.obtenerPorId("c1").orElseThrow());
        assertEquals("c1", resultado.getId());
        assertEquals(5, resultado.getCalificacion());
        assertEquals("Excelente visor", resultado.getTexto());
        assertSame(comprador, resultado.getComprador());
        assertSame(pedido.getItems().getFirst().getProducto(), resultado.getProducto());
        assertEquals(EstadoPedido.COMPLETADA, pedido.getEstadoPedido());
        assertEquals(1, pedidos.cantidadRegistrada());
        assertTrue(comentarios.obtenerPorId("inexistente").isEmpty());
    }

    @Test
    void noGuardaCuandoNoExistePedidoOFallaUnaRegla() {
        CompradorRepositoryEnMemoria compradores = new CompradorRepositoryEnMemoria();
        compradores.guardar(comprador);
        compradores.guardar(new Comprador("u2", "Otro", "otro@correo.com", "clave"));
        PedidoRepositoryEnMemoria pedidos = new PedidoRepositoryEnMemoria();
        ComentarioRepository comentarios = new ComentarioRepository() {
            public void guardar(Comentario comentario) { fail("No debe guardar un comentario inválido"); }
            public Optional<Comentario> obtenerPorId(String id) { return Optional.empty(); }
        };
        DejarComentarioUseCase caso = new DejarComentarioUseCase(compradores, pedidos, comentarios);
        assertThrows(ReglaDominioException.class, () -> caso.ejecutar("c1", "u1", "no", "v1", 5, "Opinión"));
        pedidos.registrar(pedido(false));
        assertThrows(ReglaDominioException.class, () -> caso.ejecutar("c1", "u1", "p1", "v1", 5, "Opinión"));
        pedidos.registrar(pedido(true));
        assertThrows(ReglaDominioException.class, () -> caso.ejecutar("c1", "u1", "p1", "otro", 5, "Opinión"));
        assertThrows(AccesoDenegadoException.class, () -> caso.ejecutar("c1", "u2", "p1", "v1", 5, "Opinión"));
        assertThrows(ReglaDominioException.class, () -> caso.ejecutar("c1", "u1", "p1", "v1", 6, "Opinión"));
        assertThrows(ReglaDominioException.class, () -> caso.ejecutar("c1", "u1", "p1", "v1", 5, " "));
    }

    @Test
    void noGuardaNiConsultaPedidoSiCompradorNoExiste() {
        CompradorRepositoryEnMemoria compradores = new CompradorRepositoryEnMemoria();
        PedidoRepository pedidos = new PedidoRepository() {
            public Optional<Pedido> obtenerPorId(String id) { fail("No debe consultar el pedido sin comprador"); return Optional.empty(); }
            public void registrar(Pedido pedido) { fail("Comentar no registra pedidos"); }
        };
        ComentarioRepository comentarios = new ComentarioRepository() {
            public void guardar(Comentario comentario) { fail("No debe guardar si no existe comprador"); }
            public Optional<Comentario> obtenerPorId(String id) { return Optional.empty(); }
        };
        DejarComentarioUseCase caso = new DejarComentarioUseCase(compradores, pedidos, comentarios);

        ReglaDominioException error = assertThrows(ReglaDominioException.class,
                () -> caso.ejecutar("c1", "inexistente", "p1", "v1", 5, "Opinión"));

        assertEquals("No existe un comprador con ese id", error.getMessage());
    }
}
