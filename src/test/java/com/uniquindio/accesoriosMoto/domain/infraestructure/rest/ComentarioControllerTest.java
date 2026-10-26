package com.uniquindio.accesoriosMoto.domain.infraestructure.rest;

import com.uniquindio.accesoriosMoto.application.usecase.DejarComentarioUseCase;
import com.uniquindio.accesoriosMoto.infrastructure.rest.ComentarioController;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.ComentarioMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComentarioController.class)
@Import({ComentarioMapper.class, com.uniquindio.accesoriosMoto.infrastructure.security.SecurityConfig.class})
@org.springframework.security.test.context.support.WithMockUser(username="u1", roles="COMPRADOR")
class ComentarioControllerTest {
    @MockitoBean
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DejarComentarioUseCase dejarComentarioUseCase;

    @Test
    void creaComentarioConIdGeneradoYRespuestaSinDatosSensibles() throws Exception {
        when(dejarComentarioUseCase.ejecutar(anyString(), eq("u1"), eq("ped1"), eq("p1"), eq(5), eq("Excelente")))
                .thenAnswer(invocacion -> DatosRestPrueba.comentario(invocacion.getArgument(0, String.class)));

        MvcResult resultado = mockMvc.perform(post("/api/comentarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"compradorId\":\"u1\",\"pedidoId\":\"ped1\",\"productoId\":\"p1\",\"calificacion\":5,\"texto\":\"Excelente\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("http://localhost/api/comentarios/[^/]+")))
                .andExpect(jsonPath("$.productoId").value("p1"))
                .andExpect(jsonPath("$.calificacion").value(5))
                .andExpect(jsonPath("$.texto").value("Excelente"))
                .andExpect(jsonPath("$.comprador").doesNotExist())
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.producto").doesNotExist()).andReturn();

        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        verify(dejarComentarioUseCase).ejecutar(id.capture(), eq("u1"), eq("ped1"), eq("p1"), eq(5), eq("Excelente"));
        assertNotNull(id.getValue());
        assertFalse(id.getValue().isBlank());
        assertEquals("http://localhost/api/comentarios/" + id.getValue(), resultado.getResponse().getHeader("Location"));
    }

    @Test
    void rechazaComentarioInvalidoAntesDeInvocarUseCase() throws Exception {
        for (String json : new String[]{"{}",
                "{\"compradorId\":\"u1\",\"pedidoId\":\"ped1\",\"productoId\":\"p1\",\"calificacion\":6,\"texto\":\"Excelente\"}",
                "{\"compradorId\":\"u1\",\"pedidoId\":\"ped1\",\"productoId\":\"p1\",\"calificacion\":5,\"texto\":\" \"}"}) {
            mockMvc.perform(post("/api/comentarios").contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isBadRequest());
        }

        verifyNoInteractions(dejarComentarioUseCase);
    }
}