package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;

import java.util.Objects;

public class Casco extends Producto{
    private String modelo;
    private Talla talla;
    private Proteccion proteccion;
    private String certificacionSeguridad;

    private Casco(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, String modelo, Talla talla, Proteccion proteccion, String certificacionSeguridad) {
        super(id, marca, precio, color, estadoPublicacion);
        this.modelo = modelo;
        this.talla = talla;
        this.proteccion = proteccion;
        this.certificacionSeguridad = certificacionSeguridad;
    }

    public static Casco crear(String id, Marca marca, String modelo, String color, Talla talla, Precio precio, Proteccion proteccion, String certificacionSeguridad) {
        if(certificacionSeguridad == null || certificacionSeguridad.isBlank()){
            throw new ReglaDominioException("Un casco debe registrar su certificación de seguridad antes de poder aparecer como disponible para la venta");
        }
        return new Casco(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, modelo, talla, proteccion, certificacionSeguridad);
    }


}
