package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import lombok.Getter;

public class SoporteCelular extends Producto{

    @Getter
    private String tipoSujecion;

    private SoporteCelular(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, String tipoSujecion) {
        this(id, marca, precio, color, estadoPublicacion, tipoSujecion, null);
    }

    private SoporteCelular(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, String tipoSujecion, String imagenUrl) {
        super(id, marca, precio, color, estadoPublicacion, imagenUrl);
        this.tipoSujecion = tipoSujecion;
    }

    public static SoporteCelular crear(String id, Marca marca, Precio precio, String color, String tipoSujecion){
        return crear(id, marca, precio, color, tipoSujecion, null);
    }

    public static SoporteCelular crear(String id, Marca marca, Precio precio, String color, String tipoSujecion, String imagenUrl){
        return new SoporteCelular(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, tipoSujecion, imagenUrl);
    }

    public static SoporteCelular reconstruir(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, String tipoSujecion) {
        return reconstruir(id, marca, precio, color, estadoPublicacion, tipoSujecion, null);
    }

    public static SoporteCelular reconstruir(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, String tipoSujecion, String imagenUrl) {
        SoporteCelular producto = new SoporteCelular(id, marca, precio, color, estadoPublicacion, tipoSujecion, imagenUrl);
        return producto;
    }

}
