package com.uniquindio.accesoriosMoto.domain.domain.entity;

import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import java.time.LocalDateTime;

public class ComentarioTest {

    private Pedido pedido(Comprador comprador, Producto producto) {
        Pedido pedido = Pedido.registrar("ped1", comprador, List.of(ItemPedido.crear("i1", producto, 1)), LocalDateTime.now());
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
        Pedido pedido = pedido(comprador, producto);
        Comentario original = Comentario.dejar("COM1", comprador, pedido, producto.getId(), 5, "Excelente");

        Comentario otro = Comentario.dejar("COM1", comprador, pedido, producto.getId(), 1, "Malo");

        assertEquals(original, otro);
    }

    @Test
    public void unComentarioConCalificacionFueraDeRango(){
        Producto producto = productoPrueba();
        Comprador comprador = compradorPrueba();
        Pedido pedido = pedido(comprador, producto);

        var accion1Error = catchThrowable(() -> {
            Comentario.dejar("COM2", comprador, pedido, producto.getId(), 7, "Calificacion invalida");
        });

        assertInstanceOf(ReglaDominioException.class, accion1Error);
    }

    @Test
    public void aceptaLimitesDeCalificacionYConservaDatosDeCompra() {
        Comprador comprador = compradorPrueba();
        Producto producto = productoPrueba();
        Pedido pedido = pedido(comprador, producto);
        int[] calificaciones = {1, 5};

        var comentarios = new ArrayList<Comentario>();
        for (int calificacion : calificaciones) {
            comentarios.add(Comentario.dejar("COM" + calificacion, comprador, pedido, producto.getId(), calificacion, "Mi opinión"));
        }

        for (int indice = 0; indice < calificaciones.length; indice++) {
            Comentario comentario = comentarios.get(indice);
            assertEquals(calificaciones[indice], comentario.getCalificacion());
            assertEquals("Mi opinión", comentario.getTexto());
            assertEquals(comprador, comentario.getComprador());
            assertEquals(producto, comentario.getProducto());
        }
    }

    @Test
    public void rechazaCalificacionInvalidaTextoVacioEIdentidadAusente() {
        Comprador comprador = compradorPrueba();
        Producto producto = productoPrueba();
        Pedido pedido = pedido(comprador, producto);

        var entradas1 = new int[]{0, -1, 6};
        var entradas2 = new String[]{null, "", " "};
        var entradas3 = new String[]{null, "", " "};

        var acciones = new ArrayList<Executable>();
        for (int calificacion : entradas1) {
            acciones.add(() -> Comentario.dejar("c1", comprador, pedido, producto.getId(), calificacion, "Opinión"));
        }
        for (String texto : entradas2) {
            acciones.add(() -> Comentario.dejar("c1", comprador, pedido, producto.getId(), 5, texto));
        }
        for (String id : entradas3) {
            acciones.add(() -> Comentario.dejar(id, comprador, pedido, producto.getId(), 5, "Opinión"));
        }

        for (var accion : acciones) {
            assertThrows(ReglaDominioException.class, accion);
        }
    }

    @Test
    public void rechazaCompraAjenaProductoNoCompradoYPedidoAusente() {
        Comprador comprador = compradorPrueba();
        Producto producto = productoPrueba();
        Pedido pedido = pedido(comprador, producto);
        Comprador otro = new Comprador("90738", "Juan", "juan@gmail.com", "123");

        var accion1Error = catchThrowable(() -> Comentario.dejar("c1", otro, pedido, producto.getId(), 5, "Opinión"));
        var accion2Error = catchThrowable(() -> Comentario.dejar("c1", null, pedido, producto.getId(), 5, "Opinión"));
        var accion3Error = catchThrowable(() -> Comentario.dejar("c1", comprador, pedido, "no-comprado", 5, "Opinión"));
        var accion4Error = catchThrowable(() -> Comentario.dejar("c1", comprador, pedido, null, 5, "Opinión"));
        var accion5Error = catchThrowable(() -> Comentario.dejar("c1", comprador, null, producto.getId(), 5, "Opinión"));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        assertInstanceOf(ReglaDominioException.class, accion2Error);
        assertInstanceOf(ReglaDominioException.class, accion3Error);
        assertInstanceOf(ReglaDominioException.class, accion4Error);
        assertInstanceOf(ReglaDominioException.class, accion5Error);
    }

    @Test
    public void rechazaPedidosPendientesYReembolsados() {
        Comprador comprador = compradorPrueba();
        Producto producto = productoPrueba();
        Pedido pendiente = Pedido.registrar("p2", comprador, List.of(ItemPedido.crear("i1", producto, 1)), LocalDateTime.now());

        var accion1Error = catchThrowable(() -> Comentario.dejar("c1", comprador, pendiente, producto.getId(), 5, "Opinión"));
        Pedido reembolsado = pedido(comprador, producto);
        reembolsado.solicitarReembolso("Devolución");
        var accion2Error = catchThrowable(() -> Comentario.dejar("c1", comprador, reembolsado, producto.getId(), 5, "Opinión"));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        assertInstanceOf(ReglaDominioException.class, accion2Error);
    }

    @Test
    public void permiteComentarProductoDespublicadoYCompradorConMismaIdentidad() {
        Comprador comprador = compradorPrueba();
        Producto producto = productoPrueba();
        Pedido pedido = pedido(comprador, producto);
        Comprador mismaIdentidad = new Comprador("C1", "Diana", "diana@gmail.com", "345");

        Comentario comentario = Comentario.dejar("c1", mismaIdentidad, pedido, producto.getId(), 4, "Opinión");

        assertEquals(producto, comentario.getProducto());
    }
}
