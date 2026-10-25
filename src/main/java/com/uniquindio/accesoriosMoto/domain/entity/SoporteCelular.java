package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import lombok.Getter;

public class SoporteCelular extends Producto{

    @Getter
    private String tipoSujecion;
    @Getter
    private String compatibleTamanoCelular;

    private SoporteCelular(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, String tipoSujecion) {
        super(id, marca, precio, color, estadoPublicacion);
        this.tipoSujecion = tipoSujecion;
    }

    public static SoporteCelular crear(String id, Marca marca, Precio precio, String color, String tipoSujecion){
        return new SoporteCelular(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, tipoSujecion);
    }

    public static SoporteCelular reconstruir(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, String tipoSujecion) {
        SoporteCelular producto = new SoporteCelular(id, marca, precio, color, estadoPublicacion, tipoSujecion);
        return producto;
    }

}
