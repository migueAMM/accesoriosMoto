package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.Casco;
import com.uniquindio.accesoriosMoto.domain.entity.ItemPedido;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.function.Executable;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ItemPedidoTest {

    private Producto producto() {
        return Casco.crear("p1", new Marca("m1", "AGV"), "K-3", "Negro", Talla.M, new Precio(500000, "COP"), Proteccion.NIVEL_1, "DOT");
    }

    @Test
    public void calculaSubtotalConCantidadYPrecioDelProducto() {

        ItemPedido item = ItemPedido.crear("i1", producto(), 2);

        assertEquals(new Precio(1000000, "COP"), item.getSubtotal());
        assertEquals(2, item.getCantidad());
    }

    @Test
    public void rechazaCantidadCeroONegativa() {
        Producto producto = producto();

        var entradas1 = new int[]{0, -1};

        var acciones = new ArrayList<Executable>();
        for (int cantidad : entradas1) {
            acciones.add(() -> ItemPedido.crear("i1", producto, cantidad));
        }

        for (var accion : acciones) {
            assertThrows(ReglaDominioException.class, accion);
        }
    }

    @Test
    public void rechazaProductoNulo() {

        var accion1Error = catchThrowable(() -> ItemPedido.crear("i1", null, 1));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
    }

    @Test
    public void rechazaIdentificadorInvalido() {
        Producto producto = producto();

        var entradas1 = new String[]{null, "", " "};

        var acciones = new ArrayList<Executable>();
        for (String id : entradas1) {
            acciones.add(() -> ItemPedido.crear(id, producto, 1));
        }

        for (var accion : acciones) {
            assertThrows(ReglaDominioException.class, accion);
        }
    }

    @Test
    public void conservaSubtotalHistoricoAunqueCambiePrecioDelProducto() {
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