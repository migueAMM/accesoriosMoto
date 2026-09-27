package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;

public class SoporteCelular extends Producto{

    private String tipoSujecion;
    private String compatibleTamanoCelular;

    private SoporteCelular(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, String tipoSujecion, String compatibleTamanoCelular) {
        super(id, marca, precio, color, estadoPublicacion);
        this.tipoSujecion = tipoSujecion;
        this.compatibleTamanoCelular = compatibleTamanoCelular;
    }

    public static SoporteCelular crear(String id, Marca marca, Precio precio, String color, String tipoSujecion, String compatibleTamanoCelular){
        return new SoporteCelular(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, tipoSujecion, compatibleTamanoCelular);
    }

}
