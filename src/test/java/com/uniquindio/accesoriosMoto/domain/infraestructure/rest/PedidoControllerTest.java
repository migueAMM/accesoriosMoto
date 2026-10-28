package com.uniquindio.accesoriosMoto.domain.infraestructure.rest;

import com.uniquindio.accesoriosMoto.application.usecase.CompletarPedidoUseCase;
import com.uniquindio.accesoriosMoto.application.usecase.ListarPedidosUseCase;
import com.uniquindio.accesoriosMoto.application.usecase.RegistrarPedidoUseCase;
import com.uniquindio.accesoriosMoto.application.usecase.SolicitarReembolsoUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.infrastructure.rest.PedidoController;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.PedidoMapper;
import com.uniquindio.accesoriosMoto.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
@Import({PedidoMapper.class, SecurityConfig.class})
@WithMockUser(username="u1", roles="COMPRADOR")

public class PedidoControllerTest {
    @MockitoBean
    private JwtDecoder jwtDecoder;
    @MockitoBean
    private CompletarPedidoUseCase completarPedidoUseCase;
    @MockitoBean
    private ListarPedidosUseCase listarPedidosUseCase;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RegistrarPedidoUseCase registrarPedidoUseCase;
    @MockitoBean
    private SolicitarReembolsoUseCase solicitarReembolsoUseCase;

    @Test
    public void registraPedidoConIdGeneradoEItemsSinImportesDelCliente() throws Exception {
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        List<RegistrarPedidoUseCase.ItemSolicitado> items = List.of(new RegistrarPedidoUseCase.ItemSolicitado("p1", 2));

        when(registrarPedidoUseCase.ejecutar(anyString(), eq("u1"), eq(items)))
                .thenAnswer(invocacion -> DatosRestPrueba.pedido(invocacion.getArgument(0, String.class)));

        var solicitud1 = post("/api/pedidos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"compradorId\":\"u1\",\"items\":[{\"productoId\":\"p1\",\"cantidad\":2}]}");

        var respuesta1 = mockMvc.perform(solicitud1);
        MvcResult resultado = respuesta1.andReturn();

        respuesta1.andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("http://localhost/api/pedidos/[^/]+")))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.fecha").exists())
                .andExpect(jsonPath("$.totalMonto").value(200))
                .andExpect(jsonPath("$.totalMoneda").value("COP"))
                .andExpect(jsonPath("$.items[0].productoId").value("p1"))
                .andExpect(jsonPath("$.items[0].cantidad").value(2))
                .andExpect(jsonPath("$.items[0].subtotalMonto").value(200))
                .andExpect(jsonPath("$.comprador").doesNotExist())
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.items[0].producto").doesNotExist());
        verify(registrarPedidoUseCase).ejecutar(id.capture(), eq("u1"), eq(items));
        assertNotNull(id.getValue());
        assertFalse(id.getValue().isBlank());
        assertEquals("http://localhost/api/pedidos/" + id.getValue(), resultado.getResponse().getHeader("Location"));
    }

    @Test
    public void validaPedidoEItemsAnidadosAntesDeInvocarUseCase() throws Exception {
        String[] cuerpos = new String[]{"{}", "{\"compradorId\":\"u1\",\"items\":[]}", "{\"compradorId\":\"u1\",\"items\":[{\"productoId\":\"p1\",\"cantidad\":0}]}"};
        var respuestas = new java.util.ArrayList<org.springframework.test.web.servlet.ResultActions>();

        for (String json : cuerpos) {
            respuestas.add(mockMvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON).content(json)));
        }

        for (var respuesta : respuestas) {
            respuesta.andExpect(status().isBadRequest());
        }

        verifyNoInteractions(registrarPedidoUseCase);
    }

    @Test
    public void solicitaReembolsoConIdDeUrlYMotivoDelBody() throws Exception {
        Pedido pedido = DatosRestPrueba.pedido("ped1");
        pedido.completar();
        pedido.solicitarReembolso("Dañado");

        doNothing().when(solicitarReembolsoUseCase).ejecutar("ped1", "u1", "Dañado");

        var solicitud1 = put("/api/pedidos/ped1/reembolso").contentType(MediaType.APPLICATION_JSON)
                .content("{\"motivo\":\"Dañado\"}");

        var respuesta1 = mockMvc.perform(solicitud1);

        respuesta1.andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(solicitarReembolsoUseCase).ejecutar("ped1", "u1", "Dañado");
    }

    @Test
    public void rechazaReembolsoSinMotivo() throws Exception {
        String[] cuerpos = new String[]{"{}", "{\"motivo\":\" \"}"};
        var respuestas = new ArrayList<ResultActions>();

        for (String json : cuerpos) {
            respuestas.add(mockMvc.perform(put("/api/pedidos/ped1/reembolso").contentType(MediaType.APPLICATION_JSON).content(json)));
        }

        for (var respuesta : respuestas) {
            respuesta.andExpect(status().isBadRequest());
        }

        verifyNoInteractions(solicitarReembolsoUseCase);
    }
}