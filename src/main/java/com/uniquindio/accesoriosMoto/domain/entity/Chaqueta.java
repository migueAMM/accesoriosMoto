package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;

public class Chaqueta extends Producto{

    private Talla talla;
    private Material material;
    private Proteccion proteccion;


    public Chaqueta(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla, Material material, Proteccion proteccion) {
        super(id, marca, precio, color, estadoPublicacion);
        this.talla = talla;
        this.material = material;
        this.proteccion = proteccion;
    }

    public static Chaqueta crear(String id, Marca marca, Precio precio, String color, Talla talla, Material material, Proteccion proteccion){
        if(proteccion == null){
            throw new ReglaDominioException("se debe especificar la talla de la chaqueta");
        }
        return new Chaqueta(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, talla, material, proteccion);
    }

}
