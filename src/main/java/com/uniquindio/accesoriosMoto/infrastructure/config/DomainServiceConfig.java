package com.uniquindio.accesoriosMoto.infrastructure.config;

import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.service.VerificadorDeCompra;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {
    @Bean
    public VerificadorDeCompra verificadorDeCompra(PedidoRepository pedidos) {
        return new VerificadorDeCompra(pedidos);
    }
}
