package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;

import java.util.LinkedHashSet;
import java.util.Set;

public class Baul extends Producto implements ProductoCompatible {

    private final Set<Compatibilidad> compatibilidades = new LinkedHashSet<>();

    @Override
    public void indicarCompatibilidad(Compatibilidad compatibilidad) {
        validarCompatibilidad(compatibilidad);
        if(!compatibilidades.add(compatibilidad)){
            throw new ReglaDominioException("El producto ya tiene esa compatibilidad");
        }
    }

    @Override
    public void retirarCompatibilidad(Compatibilidad compatibilidad) {
        validarCompatibilidad(compatibilidad);
        if(!compatibilidades.remove(compatibilidad)){
            throw new ReglaDominioException("El producto no tiene esa compatibilidad");
        }
    }

    public void validarCompatibilidad(Compatibilidad compatibilidad){
        if(compatibilidad == null || compatibilidad.tipo() != TipoCompatibilidad.MOTO){
            throw new ReglaDominioException("El producto no admite ese tipo de compatibilidad");
        }
    }


    @Override
    public Set<Compatibilidad> getCompatibilidades() {
        return Set.copyOf(compatibilidades);
    }

    private Material material;
    private double capacidadLitros;
    private String dimensiones;
    private double capacidadMaximaCarga;


    private Baul(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Material material, double capacidadLitros, String dimensiones, double capacidadMaximaCarga) {
        super(id, marca, precio, color, estadoPublicacion);
        this.material = material;
        this.capacidadLitros = capacidadLitros;
        this.dimensiones = dimensiones;
        this.capacidadMaximaCarga = capacidadMaximaCarga;
    }

    public static Baul crear(String id, Marca marca, Material material, double capacidadLitros,
                             String dimensiones, Precio precio, double capacidadMaximaCarga, String color) {
        if (material == null) {
            throw new ReglaDominioException("El baúl debe indicar su material");
        }
        if (!Double.isFinite(capacidadLitros) || capacidadLitros <= 0) {
            throw new ReglaDominioException("Un baúl debe indicar su capacidad en litros antes de ser publicado");
        }
        if (!Double.isFinite(capacidadMaximaCarga) || capacidadMaximaCarga <= 0) {
            throw new ReglaDominioException("Un baúl debe indicar su capacidad máxima de carga antes de ser publicado");
        }
        return new Baul(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, material, capacidadLitros, dimensiones, capacidadMaximaCarga);
    }

}
