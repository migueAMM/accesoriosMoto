package com.uniquindio.accesoriosMoto.domain.valueObject;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.entity.ProductoCompatible;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;

import java.util.Locale;

public record FiltroProducto(String marca, String color, Double precioMinimo, Double precioMaximo, String moneda, Compatibilidad compatibilidad) {

    public FiltroProducto{
        marca = normalizar(marca);
        color = normalizar(color);
        moneda = normalizar(moneda);
        validarLimite(precioMinimo);
        validarLimite(precioMaximo);
        if (precioMinimo != null && precioMaximo != null && precioMinimo > precioMaximo){
            throw new ReglaDominioException("El precio minimo no puede superar el maximo");
        }
        if((precioMinimo != null || precioMaximo != null) && moneda == null){
            throw new ReglaDominioException("El filtro de precio debe indicar una moneda");
        }
    }

    private static String normalizar(String texto){
        return texto == null || texto.isBlank() ? null : texto.strip().toUpperCase(Locale.ROOT);
    }

    private static void validarLimite(Double limite){
        if(limite != null && (!Double.isFinite(limite) || limite < 0)){
            throw new ReglaDominioException("Los limites de precio deben ser finitos y no negativos");
        }
    }

    public boolean coincideCon(Producto producto) {
        if (producto == null || !producto.estaPublicado()) return false;
        if (marca != null && (producto.getMarca() == null || !marca.equals(normalizar(producto.getMarca().nombre())))) return false;
        if (color != null && !color.equals(normalizar(producto.getColor()))) return false;
        if (moneda != null) {
            Precio precio = producto.getPrecio();
            if (precio == null || !moneda.equals(normalizar(precio.moneda()))) return false;
            if (precioMinimo != null && !(precio.monto() >= precioMinimo)) return false;
            if (precioMaximo != null && !(precio.monto() <= precioMaximo)) return false;
        }
        return compatibilidad == null || (producto instanceof ProductoCompatible compatible && compatible.getCompatibilidades().contains(compatibilidad));
    }
}
