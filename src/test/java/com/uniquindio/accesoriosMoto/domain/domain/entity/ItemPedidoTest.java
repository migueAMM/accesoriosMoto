package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Casco;
import com.uniquindio.accesoriosMoto.domain.entity.ItemPedido;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemPedidoTest {

    private Producto producto() {
        return Casco.crear("p1", new Marca("m1", "AGV"), "K-3", "Negro", Talla.M, new Precio(500000, "COP"), Proteccion.NIVEL_1, "DOT");
    }

    @Test
    void calculaSubtotalConCantidadYPrecioDelProducto() {
        ItemPedido item = ItemPedido.crear("i1", producto(), 2);

        assertEquals(new Precio(1000000, "COP"), item.getSubtotal());
        assertEquals(2, item.getCantidad());
    }

    @Test
    void rechazaCantidadCeroONegativa() {
        Producto producto = producto();
        for (int cantidad : new int[]{0, -1}) {
            assertThrows(ReglaDominioException.class, () -> ItemPedido.crear("i1", producto, cantidad));
        }
    }

    @Test
    void rechazaProductoNulo() {
        assertThrows(ReglaDominioException.class, () -> ItemPedido.crear("i1", null, 1));
    }

    @Test
    void rechazaIdentificadorInvalido() {
        Producto producto = producto();
        for (String id : new String[]{null, "", " "}) {
            assertThrows(ReglaDominioException.class, () -> ItemPedido.crear(id, producto, 1));
        }
    }

    @Test
    void conservaSubtotalHistoricoAunqueCambiePrecioDelProducto() {
        class ProductoConPrecioVariable extends Producto {
            ProductoConPrecioVariable() {
                super("p1", new Marca("m1", "Marca"), new Precio(100, "USD"), "Negro", EstadoPublicacion.NO_PUBLICADO);
            }
            void cambiarPrecio(Precio precio) { this.precio = precio; }
        }
        ProductoConPrecioVariable producto = new ProductoConPrecioVariable();
        ItemPedido item = ItemPedido.crear("i1", producto, 2);

        producto.cambiarPrecio(new Precio(300, "USD"));

        assertEquals(new Precio(200, "USD"), item.getSubtotal());
        assertEquals(new Precio(300, "USD"), producto.getPrecio());
    }
}
