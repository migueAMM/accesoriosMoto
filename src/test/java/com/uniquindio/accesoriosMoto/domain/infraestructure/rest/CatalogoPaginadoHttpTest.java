package com.uniquindio.accesoriosMoto.domain.infraestructure.rest;

import com.uniquindio.accesoriosMoto.domain.entity.Visor;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CatalogoPaginadoHttpTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ProductoRepository productos;

    private void datos() {
        for (String id : List.of("a","b","c","oculto")) {
            var visor= Visor.crear(id,new Marca("m","AGV"),new Precio(100,"COP"),"Negro","Integral",List.of("K-3"));
            if(!id.equals("oculto")) visor.publicar();
            productos.guardar(visor);
        }
    }

    @Test
    public void paginaConTodosLosFiltrosConservaOrdenMetadatosYVisibilidad() throws Exception {
        datos();

        var respuesta1 = mvc.perform(get("/api/productos").param("marca"," agv ").param("color"," NEGRO ")
                .param("precioMinimo","100").param("precioMaximo","100").param("moneda","cop")
                .param("tipoCompatibilidad","CASCO").param("modeloCompatibilidad","k-3")
                .param("pagina","0").param("tamano","2"));
        var respuesta2 = mvc.perform(get("/api/productos").param("pagina","1").param("tamano","2"));
        var respuesta3 = mvc.perform(get("/api/productos").param("pagina","5").param("tamano","2"));

        respuesta1.andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value("a")).andExpect(jsonPath("$.content[1].id").value("b"))
                .andExpect(jsonPath("$.totalElements").value(3)).andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(2)).andExpect(jsonPath("$.number").value(0));
        respuesta2.andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value("c"))
                .andExpect(jsonPath("$.content.length()").value(1)).andExpect(jsonPath("$.number").value(1));
        respuesta3.andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(3));
    }
    @Test
    public void valoresPorDefectoYLimitesSonLosDelCatalogo() throws Exception {
        String[][] parametros = {{"pagina","-1"},{"pagina","abc"},{"tamano","0"},{"tamano","51"}};
        var respuestas = new ArrayList<ResultActions>();

        var defecto = mvc.perform(get("/api/productos"));
        for (String[] par : parametros) {
            respuestas.add(mvc.perform(get("/api/productos").param(par[0],par[1])));
        }

        defecto.andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.size").value(5)).andExpect(jsonPath("$.number").value(0));
        for (var respuesta : respuestas) {
            respuesta.andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }
    }
}
