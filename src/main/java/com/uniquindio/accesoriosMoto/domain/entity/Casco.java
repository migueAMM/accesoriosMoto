package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;

import java.util.Objects;

public class Casco {
    private final String id;
    private Marca marca;
    private String modelo;
    private String color;
    private Talla talla;
    private Precio precio;
    private Proteccion proteccion;
    private String certificacionSeguridad;
    private EstadoPublicacion estadoPublicacion;

    private Casco(String id, Marca marca, String modelo, String color, Talla talla, Precio precio, Proteccion proteccion, String certificacionSeguridad, EstadoPublicacion estadoPublicacion) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.talla = talla;
        this.precio = precio;
        this.proteccion = proteccion;
        this.certificacionSeguridad = certificacionSeguridad;
        this.estadoPublicacion = EstadoPublicacion.PUBLICADO;
    }

   public static Casco crear(String id, Marca marca, String modelo, String color, Talla talla, Precio precio, Proteccion proteccion, String certificacionSeguridad, EstadoPublicacion estadoPublicacion) {

        if (certificacionSeguridad == null || certificacionSeguridad.isBlank()){
            throw new ReglaDominioException("Un casco debe tener una certificacion de seguridad antes de ser publicado para la venta");
        }
       return new Casco(id, marca, modelo, color, talla, precio, proteccion, certificacionSeguridad, EstadoPublicacion.PUBLICADO);
   }

   public void publicar(){
        this.estadoPublicacion = EstadoPublicacion.PUBLICADO;
   }

   public void despublicar(){
        this.estadoPublicacion = EstadoPublicacion.NO_PUBLICADO;
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
