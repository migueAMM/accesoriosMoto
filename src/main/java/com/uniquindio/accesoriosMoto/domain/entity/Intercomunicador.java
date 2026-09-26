package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;

public class Intercomunicador extends Producto{

    private int alcanceMetros;
    private int autonomiaHoras;
    private boolean bluetooth;

    private Intercomunicador(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, int alcanceMetros, int autonomiaHoras, boolean bluetooth) {
        super(id, marca, precio, color, estadoPublicacion);
        this.alcanceMetros = alcanceMetros;
        this.autonomiaHoras = autonomiaHoras;
        this.bluetooth = bluetooth;
    }

    public static Intercomunicador crear(String id, Marca marca, Precio precio, String color, int alcanceMetros, int autonomiaHoras, boolean bluetooth){
        return new Intercomunicador(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, alcanceMetros, autonomiaHoras, bluetooth);
    }
}
