package com.uniquindio.accesoriosMoto.domain.infraestructure.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties="spring.h2.console.enabled=true")

public class ConsolaH2HttpIntegrationTest {
    @Value("${local.server.port}") private int puerto;

    @Test
    public void ofreceLaConsolaH2EnElServidor() throws Exception {
        HttpClient cliente = newHttpClient();
        HttpRequest request = newBuilder(URI.create("http://localhost:" + puerto + "/h2-console/"))
                .timeout(Duration.ofSeconds(20)).GET().build();

        var respuesta = cliente.send(request, ofString());

        assertThat(respuesta.statusCode()).isBetween(200, 399);
    }
}
