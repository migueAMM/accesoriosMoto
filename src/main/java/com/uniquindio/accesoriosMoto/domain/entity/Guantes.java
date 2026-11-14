package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import lombok.Getter;

public class Guantes extends Producto{

    @Getter
    private Talla talla;
    @Getter
    private Material material;
    @Getter
    private Proteccion proteccion;


    private Guantes(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla, Material material, Proteccion proteccion) {
        this(id, marca, precio, color, estadoPublicacion, talla, material, proteccion, null);
    }

    private Guantes(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla, Material material, Proteccion proteccion, String imagenUrl) {
        super(id, marca, precio, color, estadoPublicacion, imagenUrl);
        this.talla = talla;
        this.material = material;
        this.proteccion = proteccion;
    }

    public static Guantes crear(String id, Marca marca, Precio precio, String color, Talla talla, Material material, Proteccion proteccion) {
        return crear(id, marca, precio, color, talla, material, proteccion, null);
    }

    public static Guantes crear(String id, Marca marca, Precio precio, String color, Talla talla, Material material, Proteccion proteccion, String imagenUrl) {
        if (proteccion == null) {
            throw new ReglaDominioException("Se debe especificar la protección de los guantes");
        }
        return new Guantes(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, talla, material, proteccion, imagenUrl);
    }

    public static Guantes reconstruir(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla,Material material,Proteccion proteccion) {
        return reconstruir(id, marca, precio, color, estadoPublicacion, talla, material, proteccion, null);
    }

    public static Guantes reconstruir(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla,Material material,Proteccion proteccion, String imagenUrl) {
        Guantes producto = new Guantes(id, marca, precio, color, estadoPublicacion, talla, material, proteccion, imagenUrl);
        return producto;
    }
}
