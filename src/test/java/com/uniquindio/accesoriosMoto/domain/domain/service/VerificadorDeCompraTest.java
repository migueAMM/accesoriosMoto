package com.uniquindio.accesoriosMoto.domain.domain.service;

import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.entity.ItemPedido;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.entity.Visor;
import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.service.VerificadorDeCompra;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class VerificadorDeCompraTest {
    private final Comprador comprador = new Comprador("u1","Ana","ana@correo.com","hash");
    private Pedido pedido(boolean completar) {
        var visor= Visor.crear("v1",new Marca("m","AGV"),new Precio(100,"COP"),"Negro","Integral", List.of());
        var pedido=Pedido.registrar("p1",comprador,List.of(ItemPedido.crear("i1",visor,1)), LocalDateTime.now());
        if(completar) pedido.completar();
        return pedido;
    }

    @Test
    public void verificaCompraDelPedidoIndicadoSinCrearNiGuardarComentario() {

        var repo=mock(PedidoRepository.class);
        var pedido=pedido(true);
        when(repo.obtenerPorId("p1")).thenReturn(Optional.of(pedido));

        var accion1Error = catchThrowable(()->new VerificadorDeCompra(repo).verificarQuePuedeComentar("p1",comprador,"v1"));

        assertNull(accion1Error);
        assertEquals(EstadoPedido.COMPLETADA, pedido.getEstadoPedido());
        verify(repo).obtenerPorId("p1");
        verify(repo,never()).registrar(any());
    }

    @Test
    public void pedidoAusenteNoSeSustituyePorOtraCompra() {

        var repo=mock(PedidoRepository.class);

        var accion1Error = catchThrowable(()->new VerificadorDeCompra(repo).verificarQuePuedeComentar("ausente",comprador,"v1"));

        assertInstanceOf(NoSuchElementException.class, accion1Error);
    }

    @Test
    public void dominioConservaInvarianteDeCompradorSinDependerDeSpring() {

        var repo=mock(PedidoRepository.class);
        when(repo.obtenerPorId("p1")).thenReturn(Optional.of(pedido(true)));

        var accion1Error = catchThrowable(()->new VerificadorDeCompra(repo).verificarQuePuedeComentar(
                "p1",new Comprador("otro","Otro","otro@correo.com","hash"),"v1"));


        assertInstanceOf(ReglaDominioException.class, accion1Error);
    }

    @Test
    public void dominioSigueRechazandoEstadoIncompatibleYProductoAjeno() {

        var repo=mock(PedidoRepository.class);
        when(repo.obtenerPorId("p1")).thenReturn(Optional.of(pedido(false))).thenReturn(Optional.of(pedido(true)));
        var servicio=new VerificadorDeCompra(repo);

        var accion1Error = catchThrowable(()->servicio.verificarQuePuedeComentar("p1",comprador,"v1"));
        var accion2Error = catchThrowable(()->servicio.verificarQuePuedeComentar("p1",comprador,"otro"));

        assertInstanceOf(ReglaDominioException.class, accion1Error);
        assertInstanceOf(ReglaDominioException.class, accion2Error);
    }
}
