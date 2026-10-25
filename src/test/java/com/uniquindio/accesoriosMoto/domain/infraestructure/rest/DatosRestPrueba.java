package com.uniquindio.accesoriosMoto.domain.infraestructure.rest;

import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;

import java.time.LocalDateTime;
import java.util.List;

public class DatosRestPrueba {

    private DatosRestPrueba() {}

    public static Producto producto(String tipo, String id) {
        Marca marca = new Marca("m1", "Marca");
        Precio precio = new Precio(100, "COP");
        return switch (tipo) {
            case "Baul" -> Baul.crear(id, marca, Material.ABS, 30, "40x40", precio, 5, "Negro");
            case "Casco" -> Casco.crear(id, marca, "K-3", "Negro", Talla.M, precio, Proteccion.NIVEL_1, "DOT");
            case "Chaqueta" -> Chaqueta.crear(id, marca, precio, "Negro", Talla.M, Material.CUERO, Proteccion.NIVEL_1);
            case "Guantes" -> Guantes.crear(id, marca, precio, "Negro", Talla.M, Material.CUERO, Proteccion.NIVEL_1);
            case "Impermeable" -> Impermeable.crear(id, marca, precio, "Negro", Talla.M, Material.PVC);
            case "Intercomunicador" -> Intercomunicador.crear(id, marca, precio, "Negro", 100, 8, false);
            case "SoporteCelular" -> SoporteCelular.crear(id, marca, precio, "Negro", "Manillar");
            case "Visor" -> Visor.crear(id, marca, precio, "Negro", "Integral", List.of("K-3"));
            default -> throw new IllegalArgumentException(tipo);
        };
    }

    public static Comprador comprador() {
        return new Comprador("u1", "Ana", "ana@correo.com", "secreto");
    }

    public static Pedido pedido(String id) {
        return Pedido.registrar(id, comprador(), List.of(ItemPedido.crear("i1", producto("Casco", "p1"), 2)), LocalDateTime.of(2026, 10, 24, 10, 0));
    }

    public static Comentario comentario(String id) {
        Pedido pedido = pedido("ped1");
        pedido.completar();
        return Comentario.dejar(id, pedido.getComprador(), pedido, "p1", 5, "Excelente");
    }
}
