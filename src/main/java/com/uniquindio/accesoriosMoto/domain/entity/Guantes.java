package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;

public class Guantes extends Producto{

    private Talla talla;
    private Material material;
    private Proteccion proteccion;


    public Guantes(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla, Material material, Proteccion proteccion) {
        super(id, marca, precio, color, estadoPublicacion);
        this.talla = talla;
        this.material = material;
        this.proteccion = proteccion;
    }

    public static Guantes crear(String id, Marca marca, Precio precio, String color, Talla talla, Material material, Proteccion proteccion){
        if(proteccion == null){
            throw new ReglaDominioException("Se debe especificar la talla de los guantes");
        }
        return new Guantes(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, talla, material, proteccion);
    }
}
