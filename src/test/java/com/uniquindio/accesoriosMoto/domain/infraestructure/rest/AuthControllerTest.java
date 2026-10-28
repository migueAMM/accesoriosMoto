package com.uniquindio.accesoriosMoto.domain.infraestructure.rest;

import com.uniquindio.accesoriosMoto.application.dto.request.RegistrarCompradorRequest;
import com.uniquindio.accesoriosMoto.application.usecase.RegistrarCompradorUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.infrastructure.rest.AuthController;
import com.uniquindio.accesoriosMoto.infrastructure.rest.exception.GlobalExceptionHandler;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.UsuarioMapper;
import com.uniquindio.accesoriosMoto.infrastructure.security.AutenticacionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest {

    private final AutenticacionService autenticacion = mock(AutenticacionService.class);
    private final RegistrarCompradorUseCase registro = mock(RegistrarCompradorUseCase.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private AuthController controller() {
        return new AuthController(autenticacion, registro, encoder, new UsuarioMapper());
    }

    @Test
    public void registroEntregaHashAlCasoDeUsoSinExponerContrasenaPlana() {
        when(registro.ejecutar(anyString(), anyString(), anyString(), anyString())).thenAnswer(i -> new Comprador(i.getArgument(0), i.getArgument(1), i.getArgument(2), i.getArgument(3)));

        var respuesta = controller().registrar(new RegistrarCompradorRequest("Ana", "ana@gmail.com", "ClaveSegura123"));

        assertEquals(201, respuesta.getStatusCode().value());
        verify(registro).ejecutar(anyString(), eq("Ana"), eq("ana@gmail.com"),
                argThat(hash -> !hash.equals("ClaveSegura123") && encoder.matches("ClaveSegura123", hash)));
        verifyNoInteractions(autenticacion);
    }
    @Test
    public void registroRechazaClaveIncompatibleConBCryptSinGuardar() {

        var accion1Error = org.assertj.core.api.Assertions.catchThrowable(() -> controller().registrar(new RegistrarCompradorRequest("Ana", "ana@gmail.com", "ñ".repeat(40))));

        org.junit.jupiter.api.Assertions.assertInstanceOf(ResponseStatusException.class, accion1Error);
        verifyNoInteractions(registro);
    }

    @Test
    public void loginUsaHandlerLocalSinRevelarMotivoTecnico() throws Exception {
        when(autenticacion.login(any())).thenThrow(new BadCredentialsException("Detalle privado"));

        var mvcPreparado = MockMvcBuilders.standaloneSetup(controller()).setControllerAdvice(new GlobalExceptionHandler()).build();
        var solicitud1 = post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\":\"ana@gmail.com\",\"contrasena\":\"Incorrecta123\"}");

        var respuesta1 = mvcPreparado
                .perform(solicitud1);

        respuesta1.andExpect(status().isUnauthorized()).andExpect(jsonPath("$.error").value("Usuario o contraseña incorrectos"))
                .andExpect(jsonPath("$.mensaje").doesNotExist());
    }
    @Test
    public void registroValidaAntesDeCodificarOGuardar() throws Exception {
        var mvcPreparado = MockMvcBuilders.standaloneSetup(controller()).setControllerAdvice(new GlobalExceptionHandler()).build();
        var solicitud1 = post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Ana\",\"correo\":\"ana@gmail.com\",\"contrasena\":\"corta\"}");

        var respuesta1 = mvcPreparado
                .perform(solicitud1);

        respuesta1.andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
        verifyNoInteractions(registro);
    }
}
