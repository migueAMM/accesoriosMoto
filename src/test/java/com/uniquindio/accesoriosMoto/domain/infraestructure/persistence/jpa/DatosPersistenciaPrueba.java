package com.uniquindio.accesoriosMoto.domain.infraestructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;

import java.util.List;
import java.util.stream.Stream;

public final class DatosPersistenciaPrueba {

    private DatosPersistenciaPrueba() {}

    public static Stream<Producto> productos() {
        Marca marca = new Marca("marca", "Marca de prueba");
        Precio precio = new Precio(500000, "COP");
        Baul baul = Baul.crear("baul", marca, Material.ABS, 40, "40x30x30", precio, 8, "Negro");
        baul.indicarCompatibilidad(new Compatibilidad(TipoCompatibilidad.MOTO, "MT-07"));

        return Stream.of(
                baul,
                Casco.crear("casco", marca, "K3", "Negro", Talla.M, precio, Proteccion.NIVEL_1, "DOT"),
                Chaqueta.crear("chaqueta", marca, precio, "Negro", Talla.L, Material.CUERO, Proteccion.NIVEL_2),
                Guantes.crear("guantes", marca, precio, "Negro", Talla.S, Material.TEXTIL, Proteccion.NIVEL_1),
                Impermeable.crear("impermeable", marca, precio, "Azul", Talla.XL, Material.PVC),
                Intercomunicador.crear("intercomunicador", marca, precio, "Gris", 1000, 12, true),
                SoporteCelular.crear("soporte", marca, precio, "Negro", "Manillar"),
                Visor.crear("visor", marca, precio, "Transparente", "Solar", List.of("K3")));
    }
    public static Producto casco() {
        return productos().filter(Casco.class::isInstance).findFirst().orElseThrow();
    }
}
