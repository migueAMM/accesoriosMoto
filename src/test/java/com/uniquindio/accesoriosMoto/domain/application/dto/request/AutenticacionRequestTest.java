package com.uniquindio.accesoriosMoto.domain.application.dto.request;

import com.uniquindio.accesoriosMoto.application.dto.request.LoginRequest;
import com.uniquindio.accesoriosMoto.application.dto.request.RegistrarCompradorRequest;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AutenticacionRequestTest {
    @Test
    void validaNombreCorreoYLongitudDeContrasena() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertThat(validator.validate(new RegistrarCompradorRequest("Ana", " ana@correo.com ", "12345678"))).isEmpty();
            for (RegistrarCompradorRequest request : new RegistrarCompradorRequest[]{
                    new RegistrarCompradorRequest("", "ana@correo.com", "12345678"),
                    new RegistrarCompradorRequest("Ana", "incorrecto", "12345678"),
                    new RegistrarCompradorRequest("Ana", "ana@correo.com", "1234567"),
                    new RegistrarCompradorRequest("Ana", "ana@correo.com", "a".repeat(65)),
                    new RegistrarCompradorRequest("Ana", null, null)}) {
                assertThat(validator.validate(request)).isNotEmpty();
            }
        }
    }
    @Test void loginExigeCorreoYContrasenaSinModificarLaClave() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertThat(validator.validate(new LoginRequest("ana@correo.com", " clave "))).isEmpty();
            assertThat(new LoginRequest("ana@correo.com", " clave ").contrasena()).isEqualTo(" clave ");
            assertThat(validator.validate(new LoginRequest("incorrecto", ""))).isNotEmpty();
        }
    }
}
