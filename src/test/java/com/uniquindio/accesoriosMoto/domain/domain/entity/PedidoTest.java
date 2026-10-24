package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoTest {

    private ItemPedido item(String id, double monto, String moneda, int cantidad) {
        Producto producto = Visor.crear("p-" + id, new Marca("m1", "AGV"), new Precio(monto, moneda), "Claro", "Integral", List.of());
        return ItemPedido.crear(id, producto, cantidad);
    }

    @Test
    void calculaTotalDeUnItemYConservaFechaYEstadoInicial() {
        LocalDateTime fecha = LocalDateTime.of(2026, 10, 21, 10, 0);
        ItemPedido item = item("i1", 100, "COP", 3);

        Pedido pedido = Pedido.registrar("p1", compradorPrueba(), List.of(item), fecha);

        assertEquals(new Precio(300, "COP"), pedido.getTotal());
        assertEquals(fecha, pedido.getFecha());
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstadoPedido());
    }

    @Test
    void calculaTotalComoSumaDeVariosSubtotales() {
        List<ItemPedido> items = List.of(item("i1", 100, "USD", 2), item("i2", 50, "USD", 3), item("i3", 25, "USD", 1));

        Pedido pedido = Pedido.registrar("p1", compradorPrueba(), items, LocalDateTime.now());

        assertEquals(new Precio(375, "USD"), pedido.getTotal());
        assertEquals(items.get(0).getSubtotal().sumar(items.get(1).getSubtotal()).sumar(items.get(2).getSubtotal()), pedido.getTotal());
    }

    @Test
    void rechazaListaNula() {
        assertThrows(ReglaDominioException.class, () -> Pedido.registrar("p1", compradorPrueba(), null, LocalDateTime.now()));
    }

    @Test
    void rechazaListaVacia() {
        assertThrows(ReglaDominioException.class, () -> Pedido.registrar("p1", compradorPrueba(), List.of(), LocalDateTime.now()));
    }

    @Test
    void rechazaElementoNulo() {
        List<ItemPedido> items = Arrays.asList(itemPrueba(), null);

        assertThrows(ReglaDominioException.class, () -> Pedido.registrar("p1", compradorPrueba(), items, LocalDateTime.now()));
    }

    @Test
    void rechazaCompradorNulo() {
        assertThrows(ReglaDominioException.class, () -> Pedido.registrar("p1", null, List.of(itemPrueba()), LocalDateTime.now()));
    }

    @Test
    void rechazaIdentificadorInvalido() {
        List<ItemPedido> items = List.of(itemPrueba());
        for (String id : new String[]{null, "", " "}) {
            assertThrows(ReglaDominioException.class, () -> Pedido.registrar(id, compradorPrueba(), items, LocalDateTime.now()));
        }
    }

    @Test
    void rechazaItemsConMonedasDiferentes() {
        List<ItemPedido> items = List.of(item("i1", 100, "COP", 1), item("i2", 100, "USD", 1));

        assertThrows(ReglaDominioException.class, () -> Pedido.registrar("p1", compradorPrueba(), items, LocalDateTime.now()));
    }

    @Test
    void rechazaDesbordamientoDelTotal() {
        List<ItemPedido> items = List.of(item("i1", Double.MAX_VALUE, "COP", 1), item("i2", Double.MAX_VALUE, "COP", 1));

        assertThrows(ReglaDominioException.class, () -> Pedido.registrar("p1", compradorPrueba(), items, LocalDateTime.now()));
    }

    @Test
    void protegeListaYTotalFrenteACambiosExternos() {
        ItemPedido item = itemPrueba();
        List<ItemPedido> entrada = new ArrayList<>(List.of(item));
        Pedido pedido = Pedido.registrar("p1", compradorPrueba(), entrada, LocalDateTime.now());

        entrada.clear();

        assertNotSame(entrada, pedido.getItems());
        assertEquals(List.of(item), pedido.getItems());
        assertEquals(new Precio(200000, "COP"), pedido.getTotal());
        assertThrows(UnsupportedOperationException.class, () -> pedido.getItems().clear());
        assertThrows(UnsupportedOperationException.class, () -> pedido.getItems().set(0, item));
        pedido.validarParaRegistro();
    }

    @Test
    void conservaTotalHistoricoAunqueCambiePrecioDelProducto() {
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
        assertEquals("Primer reembolso", pedido.getMotivoReembolso());
    }

    @Test
    void unPedidoCompletadoConservaElMotivoDelReembolso() {
        Pedido pedido = pedidoPendiente();
        pedido.completar();

        pedido.solicitarReembolso("  El accesorio llegó dañado  ");

        assertEquals(EstadoPedido.REEMBOLSADA, pedido.getEstadoPedido());
        assertEquals("El accesorio llegó dañado", pedido.getMotivoReembolso());
    }

    @Test
    void unMotivoInvalidoNoModificaElPedido() {
        Pedido pedido = pedidoPendiente();
        pedido.completar();

        for (String motivo : new String[]{null, "", "   "}) {
            assertThrows(ReglaDominioException.class, () -> pedido.solicitarReembolso(motivo));
            assertEquals(EstadoPedido.COMPLETADA, pedido.getEstadoPedido());
            assertNull(pedido.getMotivoReembolso());
        }
    }
}
