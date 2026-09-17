package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;

import java.util.Objects;

public abstract class Producto {

    protected final String id;
    protected Marca marca;
    protected Precio precio;
    protected String color;
    protected EstadoPublicacion estadoPublicacion;

    protected Producto(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion) {
        this.id = id;
        this.marca = marca;
        this.precio = precio;
        this.color = color;
        this.estadoPublicacion = estadoPublicacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return id.equals(producto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
