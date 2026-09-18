package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;

import java.util.Objects;

public class Baul extends Producto {
    private Material material;
    private double capacidadLitros;
    private String dimensiones;
    private double capacidadMaximaCarga;


    public Baul(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Material material, double capacidadLitros, String dimensiones, double capacidadMaximaCarga) {
        super(id, marca, precio, color, estadoPublicacion);
        this.material = material;
        this.capacidadLitros = capacidadLitros;
        this.dimensiones = dimensiones;
        this.capacidadMaximaCarga = capacidadMaximaCarga;
    }

    public static Baul crear(String id, Marca marca, Material material, double capacidadLitros, String dimensiones, Precio precio, double capacidadMaximaCarga, String color) {
        return new Baul(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, material, capacidadLitros, dimensiones, capacidadMaximaCarga);
    }

}
