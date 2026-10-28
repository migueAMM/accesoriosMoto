package com.uniquindio.accesoriosMoto.domain.infraestructure.rest.exception;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.infrastructure.rest.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GlobalExceptionHandlerTest {
    @RestController
    static class Controlador {
        @GetMapping("/error-prueba/{tipo}") public void error(@PathVariable("tipo") String tipo) {
            switch(tipo) {
                case "dominio" -> throw new ReglaDominioException("Regla inválida");
                case "ausente" -> throw new NoSuchElementException("Ausente");
                case "correo" -> throw new DuplicateKeyException("El correo ya está registrado");
                case "ownership" -> throw new AccessDeniedException("No tiene permiso para realizar esta operación");
                case "permiso" -> throw new AccessDeniedException("Denegado");
                default -> throw new BadCredentialsException("No filtrar este detalle");
            }
        }
        @GetMapping("/numero-prueba") public int numero(@RequestParam("numero") int numero) { return numero; }
    }
    @ParameterizedTest
    @CsvSource({"dominio,400","ausente,404","correo,409","ownership,403","permiso,403"})
    void mantieneEstadoYCuerpoGlobal(String tipo,int estado) throws Exception {
        // Arrange
        var mvcPreparado = MockMvcBuilders.standaloneSetup(new Controlador()).setControllerAdvice(new GlobalExceptionHandler()).build();
        var solicitud1 = get("/error-prueba/"+tipo);

        // Act
        var respuesta1 = mvcPreparado
                .perform(solicitud1);

        // Assert
        respuesta1.andExpect(status().is(estado)).andExpect(jsonPath("$.mensaje").isNotEmpty()).andExpect(jsonPath("$.status").value(estado)).andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
    @Test
    void formatoInvalidoEs400ConRespuestaControlada() throws Exception {
        // Arrange
        var mvcPreparado = MockMvcBuilders.standaloneSetup(new Controlador()).setControllerAdvice(new GlobalExceptionHandler()).build();
        var solicitud1 = get("/numero-prueba").param("numero","abc");

        // Act
        var respuesta1 = mvcPreparado
                .perform(solicitud1);

        // Assert
        respuesta1.andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensaje").value("Solicitud inválida"));
    }
}
