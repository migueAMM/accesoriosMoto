package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;

import java.util.Objects;

public class Baul {
    private final String id;
    private Marca marca;
    private Material material;
    private int capacidadLitros;
    private String dimensiones;
    private Precio precio;

    public Baul(String id, Marca marca, Material material, int capacidadLitros, String dimensiones, Precio precio) {
        this.id = id;
        this.marca = marca;
        this.material = material;
        this.capacidadLitros = capacidadLitros;
        this.dimensiones = dimensiones;
        this.precio = precio;
    }

    public static Baul crear(String id, Marca marca, Material material, int capacidadLitros, String dimensiones, Precio precio){
        return new Baul(id, marca, material, capacidadLitros, dimensiones, precio);
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
