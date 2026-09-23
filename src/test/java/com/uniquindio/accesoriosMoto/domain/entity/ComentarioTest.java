package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComentarioTest {

    private Producto productoPrueba(){
        return new Casco ("P1", new Marca("M1", "AGV"), new Precio(500000, "COP"), "negro con morado", EstadoPublicacion.PUBLICADO, "K-3", Talla.M, Proteccion.NIVEL_2, "DOT");
    }

    private Comprador compradorPrueba(){
        return new Comprador("C1", "Ana Gomez", "anag@gmail.com", "123");
    }

    @Test
    public void dosComentariosConDatosDistintosMismoIdSonIguales(){
        Producto producto = productoPrueba();
        Comprador comprador = compradorPrueba();
        Comentario original = new Comentario("COM1", comprador, producto, 5, "Excelente");
        Comentario otro = new Comentario("COM1", comprador, producto, 1, "Malo");

        assertEquals(original, otro);

    }

    @Test
    public void unComentarioConCalificacionFueraDeRango(){
        Producto producto = productoPrueba();
        Comprador comprador = compradorPrueba();

        assertThrows(ReglaDominioException.class, () -> {
            new Comentario("COM2", comprador, producto, 7, "Calificacion invalida");
        });
    }
}
