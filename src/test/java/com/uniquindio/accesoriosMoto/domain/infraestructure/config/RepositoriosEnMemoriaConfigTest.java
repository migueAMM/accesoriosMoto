package com.uniquindio.accesoriosMoto.domain.infraestructure.config;

import com.uniquindio.accesoriosMoto.application.usecase.BuscarProductoUseCase;
import com.uniquindio.accesoriosMoto.application.usecase.PublicarProductoUseCase;
import com.uniquindio.accesoriosMoto.application.usecase.RegistrarBaulUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.Baul;
import com.uniquindio.accesoriosMoto.domain.repository.ComentarioRepository;
import com.uniquindio.accesoriosMoto.domain.repository.CompradorRepository;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.FiltroProducto;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.infrastructure.config.RepositorioEnMemoriaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class RepositoriosEnMemoriaConfigTest {

    @Test
    void registraUnaSolaImplementacionSingletonPorPuerto() {
        try (var contexto = new AnnotationConfigApplicationContext(RepositorioEnMemoriaConfig.class)) {
            for (Class<?> puerto : new Class<?>[]{ProductoRepository.class, PedidoRepository.class,
                    CompradorRepository.class, ComentarioRepository.class}) {
                assertEquals(1, contexto.getBeansOfType(puerto).size());
                assertSame(contexto.getBean(puerto), contexto.getBean(puerto));
            }
        }
    }

    @Test
    void casosDeUsoCompartenElMismoCatalogoEnMemoria() {
        try (var contexto = new AnnotationConfigApplicationContext(RepositorioEnMemoriaConfig.class,
                RegistrarBaulUseCase.class, PublicarProductoUseCase.class, BuscarProductoUseCase.class)) {
            Baul baul = contexto.getBean(RegistrarBaulUseCase.class).ejecutar("b1", new Marca("m1", "Givi"),
                    Material.ABS, 30, "40x40", new Precio(100, "COP"), 5, "Negro");

            contexto.getBean(PublicarProductoUseCase.class).ejecutar("b1");

            assertSame(baul, contexto.getBean(ProductoRepository.class).obtenerPorId("b1").orElseThrow());
            assertEquals(List.of(baul), contexto.getBean(BuscarProductoUseCase.class)
                    .ejecutar(new FiltroProducto(null, null, null, null, null, null)));
        }
    }
}
