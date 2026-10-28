package com.uniquindio.accesoriosMoto.domain.application.usecase;

import com.uniquindio.accesoriosMoto.application.usecase.RegistrarSoporteCelularUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.SoporteCelular;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.ProductoRepositoryEnMemoria;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegistrarSoporteCelularUseCaseTest {

    @Test
    public void registraSoporteConSujecion() {
        ProductoRepositoryEnMemoria repository = new ProductoRepositoryEnMemoria();
        RegistrarSoporteCelularUseCase caso = new RegistrarSoporteCelularUseCase(repository);

        SoporteCelular resultado = caso.ejecutar("s1", new Marca("m1", "Marca"), new Precio(100, "COP"), "Negro", "Manillar");
        var resultado1 = repository.obtenerPorId("s1").orElseThrow();

        assertSame(resultado, resultado1);
        assertEquals("s1", resultado.getId());
        assertFalse(resultado.estaPublicado());
        assertEquals("Manillar", ReflectionTestUtils.getField(resultado, "tipoSujecion"));
    }
}
