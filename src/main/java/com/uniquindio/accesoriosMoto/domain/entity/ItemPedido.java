package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.application.usecase.PublicarProductoUseCase;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;

import java.util.Objects;

public class ItemPedido {
    private final String id;
    private Producto producto;
    private int cantidad;
    private Precio subtotal;

    protected ItemPedido (String id, Producto producto, int cantidad, Precio subtotal) {
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public String getId() {
        return id;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Precio getSubtotal() {
        return subtotal;
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
