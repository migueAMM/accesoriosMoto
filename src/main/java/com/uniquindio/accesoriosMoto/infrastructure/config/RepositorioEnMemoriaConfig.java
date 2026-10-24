package com.uniquindio.accesoriosMoto.infrastructure.config;

import com.uniquindio.accesoriosMoto.domain.repository.ComentarioRepository;
import com.uniquindio.accesoriosMoto.domain.repository.CompradorRepository;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.ComentarioRepositoryEnMemoria;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.CompradorRepositoryEnMemoria;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.PedidoRepositoryEnMemoria;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.ProductoRepositoryEnMemoria;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositorioEnMemoriaConfig {
    @Bean
    public ProductoRepository productoRepository() {
        return new ProductoRepositoryEnMemoria();
    }

    @Bean
    public PedidoRepository pedidoRepository() {
        return new PedidoRepositoryEnMemoria();
    }

    @Bean
    public CompradorRepository compradorRepository() {
        return new CompradorRepositoryEnMemoria();
    }

    @Bean
    public ComentarioRepository comentarioRepository() {
        return new ComentarioRepositoryEnMemoria();
    }
}
