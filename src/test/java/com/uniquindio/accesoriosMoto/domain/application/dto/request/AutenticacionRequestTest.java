package com.uniquindio.accesoriosMoto.domain.application.dto.request;

import com.uniquindio.accesoriosMoto.application.dto.request.LoginRequest;
import com.uniquindio.accesoriosMoto.application.dto.request.RegistrarCompradorRequest;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AutenticacionRequestTest {

    @Test
    public void validaNombreCorreoYLongitudDeContrasena() {
        var factory = Validation.buildDefaultValidatorFactory();
        var validator = factory.getValidator();
        var valido = new RegistrarCompradorRequest("Ana", " ana@correo.com ", "12345678");
        var invalidos = new RegistrarCompradorRequest[]{
                new RegistrarCompradorRequest("", "ana@correo.com", "12345678"),
                new RegistrarCompradorRequest("Ana", "incorrecto", "12345678"),
                new RegistrarCompradorRequest("Ana", "ana@correo.com", "1234567"),
                new RegistrarCompradorRequest("Ana", "ana@correo.com", "a".repeat(65)),
                new RegistrarCompradorRequest("Ana", null, null)};

        var resultadoValido = validator.validate(valido);
        var resultadosInvalidos = java.util.Arrays.stream(invalidos).map(validator::validate).toList();
        factory.close();

        assertThat(resultadoValido).isEmpty();
        resultadosInvalidos.forEach(resultado -> assertThat(resultado).isNotEmpty());
    }

    @Test
    public void loginExigeCorreoYContrasenaSinModificarLaClave() {
        var factory = Validation.buildDefaultValidatorFactory();
        var validator = factory.getValidator();
        var valido = new LoginRequest("ana@correo.com", " clave ");
        var invalido = new LoginRequest("incorrecto", "");

        var resultadoValido = validator.validate(valido);
        var clave = valido.contrasena();
        var resultadoInvalido = validator.validate(invalido);
        factory.close();

        assertThat(resultadoValido).isEmpty();
        assertThat(clave).isEqualTo(" clave ");
        assertThat(resultadoInvalido).isNotEmpty();
    }
}