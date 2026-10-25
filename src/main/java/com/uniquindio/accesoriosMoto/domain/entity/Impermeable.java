package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import lombok.Getter;

public class Impermeable extends Producto{

    @Getter
    private Talla talla;
    @Getter
    private Material material;


    private Impermeable(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla, Material material) {
        super(id, marca, precio, color, estadoPublicacion);
        this.talla = talla;
        this.material = material;
    }

    public static Impermeable crear(String id, Marca marca, Precio precio, String color, Talla talla, Material material){
        return new Impermeable(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, talla, material);

    }

    public static Impermeable reconstruir(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla,Material material) {
        Impermeable producto = new Impermeable(id, marca, precio, color, estadoPublicacion, talla, material);
        return producto;
    }
}
