package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import lombok.Getter;

import java.util.Objects;

public abstract class Producto {

    @Getter
    protected final String id;
    @Getter
    protected Marca marca;
    @Getter
    protected Precio precio;
    @Getter
    protected String color;
    @Getter
    protected EstadoPublicacion estadoPublicacion;

    protected Producto(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion) {
        if (id == null || id.isBlank()){
            throw new ReglaDominioException("El producto debe tener un identificador valido");
        }
        if (marca == null || precio == null){
            throw new ReglaDominioException("El producto debe tener una marca y un precio");
        }
        this.id = id;
        this.marca = marca;
        this.precio = precio;
        this.color = color;
        this.estadoPublicacion = estadoPublicacion;
    }

    public void publicar(){
        if (estaEliminado()){
            throw new ReglaDominioException("Un producto eliminado no puede publicarse");
        }
        if (this.estadoPublicacion == EstadoPublicacion.PUBLICADO){
            throw new ReglaDominioException("El producto ya esta publicado");
        }

        this.estadoPublicacion = EstadoPublicacion.PUBLICADO;
    }

    public void despublicar(){
        if (estaEliminado()){
            throw new ReglaDominioException("Un producto eliminado no puede despublicarse");
        }
        if (this.estadoPublicacion == EstadoPublicacion.NO_PUBLICADO){
            throw new ReglaDominioException("El producto ya esta despublicado");
        }

        this.estadoPublicacion = EstadoPublicacion.NO_PUBLICADO;
    }

    /** Borrado lógico: el producto pasa a ELIMINADO y no se puede reactivar. */
    public void eliminar(){
        if (estaEliminado()){
            throw new ReglaDominioException("El producto ya esta eliminado");
        }

        this.estadoPublicacion = EstadoPublicacion.ELIMINADO;
    }

    public boolean estaEliminado() {
        return this.estadoPublicacion == EstadoPublicacion.ELIMINADO;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
