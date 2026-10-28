package com.uniquindio.accesoriosMoto.domain.infraestructure.config;

import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.infrastructure.config.DomainServiceConfig;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DomainServiceConfigTest {

    @Test
    public void servicioRegistradoConsultaElPuertoProporcionado() {
        var pedidos=mock(PedidoRepository.class);
        var servicio=new DomainServiceConfig().verificadorDeCompra(pedidos);

        var accion1Error = catchThrowable(()->servicio.verificarQuePuedeComentar("p",new Comprador("u","Ana","ana@correo.com","hash"),"v"));

        assertInstanceOf(NoSuchElementException.class, accion1Error);
        verify(pedidos).obtenerPorId("p");
    }
}
