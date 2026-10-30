package com.uniquindio.accesoriosMoto.domain.valueObject;

import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;

import java.util.Locale;
import java.util.Set;

public record BusquedaProducto(String texto, Double precioMinimo, Double precioMaximo,
                               String moneda, String ordenarPor, boolean ascendente) {

    public static final Set<String> CAMPOS_ORDEN = Set.of("id", "precio", "marca", "color");

    public BusquedaProducto {
        texto = normalizar(texto);
        moneda = normalizar(moneda);
        ordenarPor = ordenarPor == null || ordenarPor.isBlank() ? "id" : ordenarPor.strip().toLowerCase(Locale.ROOT);
        if (!CAMPOS_ORDEN.contains(ordenarPor)) {
            throw new ReglaDominioException("Solo se puede ordenar por: id, precio, marca o color");
        }
        validarLimite(precioMinimo);
        validarLimite(precioMaximo);
        if (precioMinimo != null && precioMaximo != null && precioMinimo > precioMaximo) {
            throw new ReglaDominioException("El precio minimo no puede superar el maximo");
        }
        if ((precioMinimo != null || precioMaximo != null) && moneda == null) {
            throw new ReglaDominioException("El filtro de precio debe indicar una moneda");
        }
    }

    private static String normalizar(String s) {
        return s == null || s.isBlank() ? null : s.strip().toUpperCase(Locale.ROOT);
    }

    private static void validarLimite(Double limite) {
        if (limite != null && (!Double.isFinite(limite) || limite < 0)) {
            throw new ReglaDominioException("Los limites de precio deben ser finitos y no negativos");
        }
    }

    /** memoria de la consulta JPQL. */
    public boolean coincideCon(Producto p) {
        if (p == null || !p.estaPublicado()) return false;
        if (texto != null) {
            String marca = p.getMarca().nombre().toUpperCase(Locale.ROOT);
            String color = p.getColor() == null ? "" : p.getColor().toUpperCase(Locale.ROOT);
            if (!marca.contains(texto) && !color.contains(texto)) return false;
        }
        if (moneda != null) {
            if (!moneda.equals(p.getPrecio().moneda().strip().toUpperCase(Locale.ROOT))) return false;
            if (precioMinimo != null && p.getPrecio().monto() < precioMinimo) return false;
            if (precioMaximo != null && p.getPrecio().monto() > precioMaximo) return false;
        }
        return true;
    }
}