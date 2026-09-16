package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.domain.valueObject.Proteccion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Talla;

import java.util.Objects;

public class Casco {
    private final String id;
    private Marca marca;
    private String modelo;
    private String color;
    private Talla talla;
    private Precio precio;
    private Proteccion proteccion;

    private Casco(String id, Marca marca, String modelo, String color, Talla talla, Precio precio, Proteccion proteccion) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.talla = talla;
        this.precio = precio;
        this.proteccion = proteccion;
    }


   public static Casco crear(String id, Marca marca, String modelo, String color, Talla talla, Precio precio, Proteccion proteccion) {
       return new Casco(id, marca, modelo, color, talla, precio, proteccion);
   }

   @Override
   public boolean equals(Object o) {
       if (this == o) return true;
       if (o == null || getClass() != o.getClass()) return false;
       Casco casco = (Casco) o;
       return id.equals(casco.id);
   }

   @Override
   public int hashCode() {
       return Objects.hash(id);
   }


}
