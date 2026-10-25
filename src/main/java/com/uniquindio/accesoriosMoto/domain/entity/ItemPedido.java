package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import lombok.Getter;

import java.util.Objects;

public class ItemPedido {

    @Getter
    private final String id;
    @Getter
    private Producto producto;
    @Getter
    private int cantidad;
    @Getter
    private Precio subtotal;

    private ItemPedido (String id, Producto producto, int cantidad, Precio subtotal) {
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public static ItemPedido crear(String id, Producto producto, int cantidad) {
        if (id == null || id.isBlank()) {
            throw new ReglaDominioException("El ítem debe tener un identificador válido");
        }
        if (producto == null) {
            throw new ReglaDominioException("El ítem debe indicar un producto");
        }
        if (cantidad <= 0) {
            throw new ReglaDominioException("La cantidad de un Item de pedido debe ser mayor a cero");
        }
        return new ItemPedido(id, producto, cantidad, producto.getPrecio().multiplicarPor(cantidad));
    }

    public static ItemPedido reconstruir(String id, Producto producto, int cantidad, Precio subtotal) {
        return new ItemPedido(id, producto, cantidad, subtotal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemPedido)) return false;
        return id.equals(((ItemPedido) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
