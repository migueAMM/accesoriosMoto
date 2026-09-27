package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;

import java.util.List;

public class Visor extends Producto{

    private String tipoVisor;
    private List<String> modelosCascoCompatibles;

    private Visor(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, String tipoVisor, List<String> modelosCascoCompatibles) {
        super(id, marca, precio, color, estadoPublicacion);
        this.tipoVisor = tipoVisor;
        this.modelosCascoCompatibles = modelosCascoCompatibles;
    }

    public static Visor crear(String id, Marca marca, Precio precio, String color, String tipoVisor, List<String> modelosCascoCompatibles) {
        return new Visor(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, tipoVisor, modelosCascoCompatibles);
    }
}
