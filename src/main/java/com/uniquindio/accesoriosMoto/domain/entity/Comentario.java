package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;

import java.util.Objects;

public class Comentario {

    private final String id;
    private Comprador comprador;
    private Producto producto;
    private int calificacion;
    private String texto;
    private String respuestaVendedor;

    public Comentario(String id, Comprador comprador, Producto producto, int calificacion, String texto) {

        if (calificacion < 1 || calificacion > 5) {
            throw new ReglaDominioException("La calificación debe estar entre 1 y 5.");
        }

        this.id = id;
        this.comprador = comprador;
        this.producto = producto;
        this.calificacion = calificacion;
        this.texto = texto;
        this.respuestaVendedor = null;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comentario)) return false;
        return id.equals(((Comentario) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
