package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.*;

public class Impermeable extends Producto{

    private Talla talla;
    private Material material;


    public Impermeable(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla, Material material, String color1) {
        super(id, marca, precio, color, estadoPublicacion);
        this.talla = talla;
        this.material = material;
        this.color = color1;
    }
}
