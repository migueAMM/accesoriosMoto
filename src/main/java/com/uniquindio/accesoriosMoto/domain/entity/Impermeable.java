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
        this(id, marca, precio, color, estadoPublicacion, talla, material, null);
    }

    private Impermeable(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla, Material material, String imagenUrl) {
        super(id, marca, precio, color, estadoPublicacion, imagenUrl);
        this.talla = talla;
        this.material = material;
    }

    public static Impermeable crear(String id, Marca marca, Precio precio, String color, Talla talla, Material material){
        return crear(id, marca, precio, color, talla, material, null);
    }

    public static Impermeable crear(String id, Marca marca, Precio precio, String color, Talla talla, Material material, String imagenUrl){
        return new Impermeable(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, talla, material, imagenUrl);
    }

    public static Impermeable reconstruir(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla,Material material) {
        return reconstruir(id, marca, precio, color, estadoPublicacion, talla, material, null);
    }

    public static Impermeable reconstruir(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla,Material material, String imagenUrl) {
        Impermeable producto = new Impermeable(id, marca, precio, color, estadoPublicacion, talla, material, imagenUrl);
        return producto;
    }
}
