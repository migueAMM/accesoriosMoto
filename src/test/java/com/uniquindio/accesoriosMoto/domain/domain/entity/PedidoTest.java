package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

public class PedidoTest {

    private ItemPedido item(String id, double monto, String moneda, int cantidad) {
        Producto producto = Visor.crear("p-" + id, new Marca("m1", "AGV"), new Precio(monto, moneda), "Claro", "Integral", List.of());
        return ItemPedido.crear(id, producto, cantidad);
    }

    @Test
    public void calculaTotalDeUnItemYConservaFechaYEstadoInicial() {

        LocalDateTime fecha = LocalDateTime.of(2026, 10, 21, 10, 0);
        ItemPedido item = item("i1", 100, "COP", 3);

        Pedido pedido = Pedido.registrar("p1", compradorPrueba(), List.of(item), fecha);

        assertEquals(new Precio(300, "COP"), pedido.getTotal());
        assertEquals(fecha, pedido.getFecha());
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstadoPedido());
    }

    @Test
    public void calculaTotalComoSumaDeVariosSubtotales() {

        List<ItemPedido> items = List.of(item("i1", 100, "USD", 2), item("i2", 50, "USD", 3), item("i3", 25, "USD", 1));

        Pedido pedido = Pedido.registrar("p1", compradorPrueba(), items, LocalDateTime.now());

        assertEquals(new Precio(375, "USD"), pedido.getTotal());
        assertEquals(items.get(0).getSubtotal().sumar(items.get(1).getSubtotal()).sumar(items.get(2).getSubtotal()), pedido.getTotal());
    }

    @Test
    public void rechazaListaNula() {

        var accion1Error = catchThrowable(() -> Pedido.registrar("p1", compradorPrueba(), null, LocalDateTime.now()));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
    }

    @Test
    public void rechazaListaVacia() {

        var accion1Error = catchThrowable(() -> Pedido.registrar("p1", compradorPrueba(), List.of(), LocalDateTime.now()));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
    }

    @Test
    public void rechazaElementoNulo() {

        List<ItemPedido> items = Arrays.asList(itemPrueba(), null);
        var accion1Error = catchThrowable(() -> Pedido.registrar("p1", compradorPrueba(), items, LocalDateTime.now()));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
    }

    @Test
    public void rechazaCompradorNulo() {

        var accion1Error = catchThrowable(() -> Pedido.registrar("p1", null, List.of(itemPrueba()), LocalDateTime.now()));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
    }

    @Test
    public void rechazaIdentificadorInvalido() {

        List<ItemPedido> items = List.of(itemPrueba());

        var entradas1 = new String[]{null, "", " "};


        var acciones = new ArrayList<Executable>();
        for (String id : entradas1) {
            acciones.add(() -> Pedido.registrar(id, compradorPrueba(), items, LocalDateTime.now()));
        }

        for (var accion : acciones) {
            assertThrows(ReglaDominioException.class, accion);
        }
    }

    @Test
    public void rechazaItemsConMonedasDiferentes() {

        List<ItemPedido> items = List.of(item("i1", 100, "COP", 1), item("i2", 100, "USD", 1));

        var accion1Error = catchThrowable(() -> Pedido.registrar("p1", compradorPrueba(), items, LocalDateTime.now()));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
    }

    @Test
    public void rechazaDesbordamientoDelTotal() {

        List<ItemPedido> items = List.of(item("i1", Double.MAX_VALUE, "COP", 1), item("i2", Double.MAX_VALUE, "COP", 1));

        var accion1Error = catchThrowable(() -> Pedido.registrar("p1", compradorPrueba(), items, LocalDateTime.now()));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
    }

    @Test
    public void protegeListaYTotalFrenteACambiosExternos() {

        ItemPedido item = itemPrueba();
        List<ItemPedido> entrada = new ArrayList<>(List.of(item));
        Pedido pedido = Pedido.registrar("p1", compradorPrueba(), entrada, LocalDateTime.now());

        entrada.clear();
        var resultado1 = pedido.getItems();
        var resultado2 = pedido.getItems();
        var resultado3 = pedido.getTotal();
        var accion4Error = catchThrowable(() -> pedido.getItems().clear());
        var accion5Error = catchThrowable(() -> pedido.getItems().set(0, item));
        pedido.validarParaRegistro();

        assertNotSame(entrada, resultado1);
        assertEquals(List.of(item), resultado2);
        assertEquals(new Precio(200000, "COP"), resultado3);
        assertInstanceOf(UnsupportedOperationException.class, accion4Error);
        assertInstanceOf(UnsupportedOperationException.class, accion5Error);
    }

    @Test
    public void conservaTotalHistoricoAunqueCambiePrecioDelProducto() {

        class ProductoConPrecioVariable extends Producto {
            ProductoConPrecioVariable() {
                super("p1", new Marca("m1", "Marca"), new Precio(100, "COP"), "Negro", EstadoPublicacion.NO_PUBLICADO);
            }
            void cambiarPrecio(Precio precio) { this.precio = precio; }
        }
        ProductoConPrecioVariable producto = new ProductoConPrecioVariable();
        ItemPedido item = ItemPedido.crear("i1", producto, 2);
        Pedido pedido = Pedido.registrar("p1", compradorPrueba(), List.of(item), LocalDateTime.now());

        producto.cambiarPrecio(new Precio(500, "COP"));
        pedido.completar();

        assertEquals(new Precio(200, "COP"), item.getSubtotal());
        assertEquals(new Precio(200, "COP"), pedido.getTotal());
    }

    private Comprador compradorPrueba(){
        return new Comprador("C1", "Juan Perez", "juanP@gmail.com", "456");
    }

    private ItemPedido itemPrueba() {
        Producto casco = Casco.crear("P2", new Marca("M2", "AGV"), "K-3", "Blanco con plateado", Talla.M, new Precio(200000, "COP"), Proteccion.NIVEL_3, "DOT");
        return ItemPedido.crear("i1", casco, 1);
    }

    private Pedido pedidoPendiente(){
        return Pedido.registrar("ped1", compradorPrueba(), List.of(itemPrueba()), LocalDateTime.now());
    }

    @Test
    public void noSePuedeSolicitarReembolsoDeUnPedidoPendiente(){

        Pedido pedido = pedidoPendiente();

        var accion1Error = catchThrowable(() -> pedido.solicitarReembolso("No llego a tiempo"));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstadoPedido());
    }

    @Test
    public void noSePuedeSolicitarReembolosoDeUnPedidoYaReembolsado(){

        Pedido pedido = pedidoPendiente();
        pedido.completar();
        pedido.solicitarReembolso("Primer reembolso");


        var accion1Error = catchThrowable(() -> pedido.solicitarReembolso("otra vez"));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        assertEquals(EstadoPedido.REEMBOLSADA, pedido.getEstadoPedido());
        assertEquals("Primer reembolso", pedido.getMotivoReembolso());
    }

    @Test
    public void unPedidoCompletadoConservaElMotivoDelReembolso() {

        Pedido pedido = pedidoPendiente();
        pedido.completar();


        pedido.solicitarReembolso("  El accesorio llegó dañado  ");


        assertEquals(EstadoPedido.REEMBOLSADA, pedido.getEstadoPedido());
        assertEquals("El accesorio llegó dañado", pedido.getMotivoReembolso());
    }

    @Test
    public void unMotivoInvalidoNoModificaElPedido() {

        Pedido pedido = pedidoPendiente();
        pedido.completar();
        String[] motivos = {null, "", "   "};
        record Intento(Throwable error, EstadoPedido estado, String motivo) {}

        var intentos = new ArrayList<Intento>();
        for (String motivo : motivos) {
            var error = catchThrowable(() -> pedido.solicitarReembolso(motivo));
            intentos.add(new Intento(error, pedido.getEstadoPedido(), pedido.getMotivoReembolso()));
        }

        for (var intento : intentos) {
            assertInstanceOf(ReglaDominioException.class, intento.error());
            assertEquals(EstadoPedido.COMPLETADA, intento.estado());
            assertNull(intento.motivo());
        }
    }
}
