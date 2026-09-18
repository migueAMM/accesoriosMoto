package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;

public class Impermeable extends Producto{

    private Talla talla;
    private Material material;


    public Impermeable(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla, Material material) {
        super(id, marca, precio, color, estadoPublicacion);
        this.talla = talla;
        this.material = material;
    }

    public static Impermeable crear(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla, Material material){
        return new Impermeable(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, talla, material);

    }
}
