package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComentarioTest {

    private Pedido pedido(Comprador comprador, Producto producto) {
        Pedido pedido = Pedido.registrar("ped1", comprador,
                List.of(ItemPedido.crear("i1", producto, 1, new Precio(200000, "COP"))),
                LocalDateTime.now(), new Precio(200000, "COP"));
        pedido.completar();
        return pedido;
    }

    private Producto productoPrueba(){
        return Casco.crear("p1", new Marca("m1", "Shark"), "Evo Jet", "Negro", Talla.M, new Precio(200000, "COP"), Proteccion.NIVEL_3, "DOT");
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


    @Test
    void rechazaCalificacionInvalidaTextoVacioEIdentidadAusente() {
        Comprador comprador = compradorPrueba();
        Producto producto = productoPrueba();
        Pedido pedido = pedido(comprador, producto);
        for (int calificacion : new int[]{0, -1, 6}) {
            assertThrows(ReglaDominioException.class, () -> Comentario.dejar("c1", comprador, pedido, producto.getId(), calificacion, "Opinión"));
        }
        for (String texto : new String[]{null, "", " "}) {
            assertThrows(ReglaDominioException.class, () -> Comentario.dejar("c1", comprador, pedido, producto.getId(), 5, texto));
        }
        for (String id : new String[]{null, "", " "}) {
            assertThrows(ReglaDominioException.class, () -> Comentario.dejar(id, comprador, pedido, producto.getId(), 5, "Opinión"));
        }
    }

    @Test
    void rechazaCompraAjenaProductoNoCompradoYPedidoAusente() {
        Comprador comprador = compradorPrueba();
        Producto producto = productoPrueba();
        Pedido pedido = pedido(comprador, producto);
        Comprador otro = new Comprador("otro", "Otra persona", "otro@correo.com", "clave");
        assertThrows(ReglaDominioException.class, () -> Comentario.dejar("c1", otro, pedido, producto.getId(), 5, "Opinión"));
        assertThrows(ReglaDominioException.class, () -> Comentario.dejar("c1", null, pedido, producto.getId(), 5, "Opinión"));
        assertThrows(ReglaDominioException.class, () -> Comentario.dejar("c1", comprador, pedido, "no-comprado", 5, "Opinión"));
        assertThrows(ReglaDominioException.class, () -> Comentario.dejar("c1", comprador, pedido, null, 5, "Opinión"));
        assertThrows(ReglaDominioException.class, () -> Comentario.dejar("c1", comprador, null, producto.getId(), 5, "Opinión"));
    }

    @Test
    void rechazaPedidosPendientesYReembolsados() {
        Comprador comprador = compradorPrueba();
        Producto producto = productoPrueba();
        Pedido pendiente = Pedido.registrar("p2", comprador,
                List.of(ItemPedido.crear("i1", producto, 1, new Precio(200000, "COP"))), LocalDateTime.now(), new Precio(200000, "COP"));
        assertThrows(ReglaDominioException.class, () -> Comentario.dejar("c1", comprador, pendiente, producto.getId(), 5, "Opinión"));
        Pedido reembolsado = pedido(comprador, producto);
        reembolsado.solicitarReembolso("Devolución");
        assertThrows(ReglaDominioException.class, () -> Comentario.dejar("c1", comprador, reembolsado, producto.getId(), 5, "Opinión"));
    }

    @Test
    void permiteComentarProductoDespublicadoYCompradorConMismaIdentidad() {
        Comprador comprador = compradorPrueba();
        Producto producto = productoPrueba();
        Pedido pedido = pedido(comprador, producto);
        Comprador mismaIdentidad = new Comprador("C1", "Nombre actualizado", "nuevo@correo.com", "clave");
        Comentario comentario = Comentario.dejar("c1", mismaIdentidad, pedido, producto.getId(), 4, "Opinión");
        assertEquals(producto, comentario.getProducto());
    }
}
