package com.uniquindio.accesoriosMoto.domain.application.usecase;

import com.uniquindio.accesoriosMoto.application.dto.request.ItemPedidoRequest;
import com.uniquindio.accesoriosMoto.application.usecase.RegistrarPedidoUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.CompradorRepositoryEnMemoria;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.PedidoRepositoryEnMemoria;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.ProductoRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegistrarPedidoUseCaseTest {

    private final CompradorRepositoryEnMemoria compradores = new CompradorRepositoryEnMemoria();
    private final ProductoRepositoryEnMemoria productos = new ProductoRepositoryEnMemoria();
    private final PedidosObservados pedidos = new PedidosObservados();
    private final Comprador comprador = new Comprador("u1", "Ana", "ana@correo.com", "clave");
    private Producto visor;
    private RegistrarPedidoUseCase caso;

    private static class PedidosObservados extends PedidoRepositoryEnMemoria {
        int registros;

        @Override
        public void registrar(Pedido pedido) {
            registros++;
            super.registrar(pedido);
        }
    }

    @BeforeEach
    public void prepararDatos() {
        compradores.guardar(comprador);
        visor = Visor.crear("v1", new Marca("m1", "AGV"), new Precio(100, "COP"), "Claro", "Integral", List.of());
        productos.guardar(visor);
        caso = new RegistrarPedidoUseCase(compradores, productos, pedidos);
    }

    private void comprobarQueNoRegistro() {
        assertEquals(0, pedidos.registros);
        assertEquals(0, pedidos.cantidadRegistrada());
    }

    @Test
    public void registraPedidoRecuperandoEntidadesEImportesDelDominio() {

        Pedido resultado = caso.ejecutar("ped1", "u1", List.of(new RegistrarPedidoUseCase.ItemSolicitado("v1", 2)));

        var resultado11 = pedidos.obtenerPorId("ped1").orElseThrow();
        var resultado12 = pedidos.registros;

        assertSame(comprador, resultado.getComprador());
        assertSame(visor, resultado.getItems().getFirst().getProducto());
        assertEquals("ped1", resultado.getId());
        assertEquals(2, resultado.getItems().getFirst().getCantidad());
        assertEquals(new Precio(200, "COP"), resultado.getItems().getFirst().getSubtotal());
        assertEquals(new Precio(200, "COP"), resultado.getTotal());
        assertNotNull(resultado.getItems().getFirst().getId());
        assertFalse(resultado.getItems().getFirst().getId().isBlank());
        assertEquals(EstadoPedido.PENDIENTE, resultado.getEstadoPedido());
        assertNotNull(resultado.getFecha());
        assertSame(resultado, resultado11);
        assertEquals(1, resultado12);
    }

    @Test
    public void registraVariosProductosConIdsInternosDistintos() {
        Producto baul = Baul.crear("b1", new Marca("m2", "Givi"), Material.ABS, 30, "40x40", new Precio(50, "COP"), 5, "Negro");
        productos.guardar(baul);

        Pedido resultado = caso.ejecutar("ped1", "u1", List.of(new RegistrarPedidoUseCase.ItemSolicitado("v1", 2), new RegistrarPedidoUseCase.ItemSolicitado("b1", 3)));

        ItemPedido primero = resultado.getItems().get(0);
        ItemPedido segundo = resultado.getItems().get(1);

        var resultado12 = pedidos.obtenerPorId("ped1").orElseThrow();
        var resultado13 = pedidos.registros;

        assertEquals(2, resultado.getItems().size());
        assertSame(visor, primero.getProducto());
        assertSame(baul, segundo.getProducto());
        assertEquals(new Precio(200, "COP"), primero.getSubtotal());
        assertEquals(new Precio(150, "COP"), segundo.getSubtotal());
        assertEquals(new Precio(350, "COP"), resultado.getTotal());
        assertNotNull(primero.getId());
        assertNotNull(segundo.getId());
        assertFalse(primero.getId().isBlank());
        assertFalse(segundo.getId().isBlank());
        assertNotEquals(primero.getId(), segundo.getId());
        assertSame(resultado, resultado12);
        assertEquals(1, resultado13);
    }

    @Test
    public void noRegistraSiCompradorNoExiste() {

        var error = catchThrowable(() -> caso.ejecutar("ped1", "inexistente", List.of(new RegistrarPedidoUseCase.ItemSolicitado("v1", 1))));

        assertInstanceOf(NoSuchElementException.class, error);

        assertEquals("No existe un comprador con ese id", error.getMessage());
        comprobarQueNoRegistro();
    }

    @Test
    public void noRegistraSiProductoNoExiste() {

        var error = catchThrowable(() -> caso.ejecutar("ped1", "u1", List.of(new RegistrarPedidoUseCase.ItemSolicitado("inexistente", 1))));

        assertInstanceOf(NoSuchElementException.class, error);

        assertEquals("No existe un producto con el id: inexistente", error.getMessage());
        comprobarQueNoRegistro();
    }

    @Test
    public void noRegistraParcialmenteSiFallaUnProductoPosterior() {

        var accion1Error = catchThrowable(() -> caso.ejecutar("ped1", "u1", List.of(new RegistrarPedidoUseCase.ItemSolicitado("v1", 1), new RegistrarPedidoUseCase.ItemSolicitado("inexistente", 1))));

        assertInstanceOf(NoSuchElementException.class, accion1Error);
        comprobarQueNoRegistro();
    }

    @Test
    public void noRegistraCuandoDominioRechazaCantidad() {
        int[] cantidades = {0, -1};

        var acciones = java.util.Arrays.stream(cantidades)
                .mapToObj(cantidad -> (Executable) () -> caso.ejecutar("ped1", "u1", List.of(new RegistrarPedidoUseCase.ItemSolicitado("v1", cantidad))))
                .toList();

        for (var accion : acciones) {
            assertThrows(ReglaDominioException.class, accion);
            comprobarQueNoRegistro();
        }
    }

    @Test
    public void noRegistraCuandoDominioRechazaMonedasIncompatibles() {
        productos.guardar(Visor.crear("v2", new Marca("m1", "AGV"), new Precio(10, "USD"), "Claro", "Integral", List.of()));

        var accion1Error = catchThrowable(() -> caso.ejecutar("ped1", "u1", List.of(new RegistrarPedidoUseCase.ItemSolicitado("v1", 1), new RegistrarPedidoUseCase.ItemSolicitado("v2", 1))));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        comprobarQueNoRegistro();
    }

    @Test
    public void dejaAlDominioRechazarPedidoSinItems() {

        var accion1Error = catchThrowable(() -> caso.ejecutar("ped1", "u1", List.of()));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        comprobarQueNoRegistro();
    }

    @Test
    public void rechazaListaNulaConErrorClaroSinRegistrar() {

        var error = catchThrowable(() -> caso.ejecutar("ped1", "u1", null));

        assertInstanceOf(ReglaDominioException.class, error);

        assertEquals("Debe proporcionar la lista de ítems del pedido", error.getMessage());
        comprobarQueNoRegistro();
    }

    @Test
    public void rechazaElementoNuloConErrorClaroSinRegistrar() {

        var error = catchThrowable(() -> caso.ejecutar("ped1", "u1", Arrays.asList((RegistrarPedidoUseCase.ItemSolicitado) null)));

        assertInstanceOf(ReglaDominioException.class, error);

        assertEquals("Los ítems solicitados no pueden ser nulos", error.getMessage());
        comprobarQueNoRegistro();
    }

    @Test
    public void rechazaCantidadOmitidaConErrorClaroSinRegistrar() {

        var error = catchThrowable(() -> caso.ejecutar("ped1", "u1", List.of(new RegistrarPedidoUseCase.ItemSolicitado("v1", null))));

        assertInstanceOf(ReglaDominioException.class, error);

        assertEquals("Debe indicar la cantidad del ítem", error.getMessage());
        comprobarQueNoRegistro();
    }
}