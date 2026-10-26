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

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties="spring.h2.console.enabled=true")
class ConsolaH2HttpIntegrationTest {
    @Value("${local.server.port}") private int puerto;

    @Test
    void ofreceLaConsolaH2EnElServidor() throws Exception {
        // Arrange
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + "/h2-console/"))
                .timeout(Duration.ofSeconds(20)).GET().build();

        // Act
        var respuesta = cliente.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(respuesta.statusCode()).isBetween(200, 399);
    }
}
