package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import lombok.Getter;

public class Intercomunicador extends Producto{

    @Getter
    private int alcanceMetros;
    @Getter
    private int autonomiaHoras;
    @Getter
    private boolean bluetooth;

    private Intercomunicador(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, int alcanceMetros, int autonomiaHoras, boolean bluetooth) {
        this(id, marca, precio, color, estadoPublicacion, alcanceMetros, autonomiaHoras, bluetooth, null);
    }

    private Intercomunicador(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, int alcanceMetros, int autonomiaHoras, boolean bluetooth, String imagenUrl) {
        super(id, marca, precio, color, estadoPublicacion, imagenUrl);
        this.alcanceMetros = alcanceMetros;
        this.autonomiaHoras = autonomiaHoras;
        this.bluetooth = bluetooth;
    }

    public static Intercomunicador crear(String id, Marca marca, Precio precio, String color, int alcanceMetros, int autonomiaHoras, boolean bluetooth){
        return crear(id, marca, precio, color, alcanceMetros, autonomiaHoras, bluetooth, null);
    }

    public static Intercomunicador crear(String id, Marca marca, Precio precio, String color, int alcanceMetros, int autonomiaHoras, boolean bluetooth, String imagenUrl){
        return new Intercomunicador(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, alcanceMetros, autonomiaHoras, bluetooth, imagenUrl);
    }

    public static Intercomunicador reconstruir(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, int alcanceMetros,int autonomiaHoras,boolean bluetooth) {
        return reconstruir(id, marca, precio, color, estadoPublicacion, alcanceMetros, autonomiaHoras, bluetooth, null);
    }

    public static Intercomunicador reconstruir(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, int alcanceMetros,int autonomiaHoras,boolean bluetooth, String imagenUrl) {
        Intercomunicador producto = new Intercomunicador(id, marca, precio, color, estadoPublicacion, alcanceMetros, autonomiaHoras, bluetooth, imagenUrl);
        return producto;
    }
}
