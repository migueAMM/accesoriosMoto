package com.uniquindio.accesoriosMoto.domain.infraestructure.rest;

import com.uniquindio.accesoriosMoto.application.dto.request.ItemPedidoRequest;
import com.uniquindio.accesoriosMoto.application.usecase.RegistrarPedidoUseCase;
import com.uniquindio.accesoriosMoto.application.usecase.SolicitarReembolsoUseCase;
import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.infrastructure.rest.PedidoController;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.PedidoMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
@Import(PedidoMapper.class)
class PedidoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RegistrarPedidoUseCase registrarPedidoUseCase;
    @MockitoBean
    private SolicitarReembolsoUseCase solicitarReembolsoUseCase;

    @Test
    void registraPedidoConIdGeneradoEItemsSinImportesDelCliente() throws Exception {
        List<ItemPedidoRequest> items = List.of(new ItemPedidoRequest("p1", 2));
        when(registrarPedidoUseCase.ejecutar(anyString(), eq("u1"), eq(items)))
                .thenAnswer(invocacion -> DatosRestPrueba.pedido(invocacion.getArgument(0, String.class)));

        MvcResult resultado = mockMvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"compradorId\":\"u1\",\"items\":[{\"productoId\":\"p1\",\"cantidad\":2}]}"))
                .andExpect(status().isCreated())
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
                .andExpect(jsonPath("$.items[0].producto").doesNotExist()).andReturn();

        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);
        verify(registrarPedidoUseCase).ejecutar(id.capture(), eq("u1"), eq(items));
        assertNotNull(id.getValue());
        assertFalse(id.getValue().isBlank());
        assertEquals("http://localhost/api/pedidos/" + id.getValue(), resultado.getResponse().getHeader("Location"));
    }

    @Test
    void validaPedidoEItemsAnidadosAntesDeInvocarUseCase() throws Exception {
        for (String json : new String[]{"{}", "{\"compradorId\":\"u1\",\"items\":[]}",
                "{\"compradorId\":\"u1\",\"items\":[{\"productoId\":\"p1\",\"cantidad\":0}]}"}) {
            mockMvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isBadRequest());
        }

        verifyNoInteractions(registrarPedidoUseCase);
    }

    @Test
    void solicitaReembolsoConIdDeUrlYMotivoDelBody() throws Exception {
        Pedido pedido = DatosRestPrueba.pedido("ped1");
        pedido.completar();
        pedido.solicitarReembolso("Dañado");
        when(solicitarReembolsoUseCase.ejecutar("ped1", "Dañado")).thenReturn(pedido);

        mockMvc.perform(put("/api/pedidos/ped1/reembolso").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\":\"Dañado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ped1"))
                .andExpect(jsonPath("$.estado").value("REEMBOLSADA"))
                .andExpect(jsonPath("$.totalMonto").value(200));

        verify(solicitarReembolsoUseCase).ejecutar("ped1", "Dañado");
    }

    @Test
    void rechazaReembolsoSinMotivo() throws Exception {
        for (String json : new String[]{"{}", "{\"motivo\":\" \"}"}) {
            mockMvc.perform(put("/api/pedidos/ped1/reembolso").contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isBadRequest());
        }

        verifyNoInteractions(solicitarReembolsoUseCase);
    }
}
