package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import lombok.Getter;

import java.util.Objects;

public class Comentario {

    @Getter
    private final String id;
    @Getter
    private Comprador comprador;
    @Getter
    private Producto producto;
    @Getter
    private int calificacion;
    @Getter
    private String texto;
    private String respuestaVendedor;

    public Comentario(String id, Comprador comprador, Producto producto, int calificacion, String texto) {
        if (id == null || id.isBlank()) {
            throw new ReglaDominioException("El comentario debe tener un identificador");
        }
        if (calificacion < 1 || calificacion > 5) {
            throw new ReglaDominioException("La calificación debe estar entre 1 y 5.");
        }
        if (texto == null || texto.isBlank()) {
            throw new ReglaDominioException("El texto del comentario no puede estar vacío");
        }

        this.id = id;
        this.comprador = comprador;
        this.producto = producto;
        this.calificacion = calificacion;
        this.texto = texto;
        this.respuestaVendedor = null;

    }

    public static Comentario dejar(String id, Comprador comprador, Pedido pedido, String idProducto, int calificacion, String texto){
        if (pedido == null){
            throw new ReglaDominioException("Se requiere un pedido para verificar la compra");
        }
        Producto producto = pedido.obtenerProductoParaComentar(comprador, idProducto);
        return new Comentario(id, pedido.getComprador(), producto, calificacion, texto);
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
