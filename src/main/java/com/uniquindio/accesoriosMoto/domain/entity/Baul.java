package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;

import java.util.Objects;

public class Baul {
    private final String id;
    private Marca marca;
    private Material material;
    private double capacidadLitros;
    private String dimensiones;
    private Precio precio;
    private double capacidadMaximaCarga;

    public Baul(String id, Marca marca, Material material, double capacidadLitros, String dimensiones, Precio precio, double capacidadMaximaCarga) {
        this.id = id;
        this.marca = marca;
        this.material = material;
        this.capacidadLitros = capacidadLitros;
        this.dimensiones = dimensiones;
        this.precio = precio;
        this.capacidadMaximaCarga = capacidadMaximaCarga;
    }

    public static Baul crear(String id, Marca marca, Material material, int capacidadLitros, String dimensiones, Precio precio, double capacidadMaximaCarga){
        return new Baul(id, marca, material, capacidadLitros, dimensiones, precio, capacidadMaximaCarga);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof Baul)) return false;
        Baul otro = (Baul) o;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
