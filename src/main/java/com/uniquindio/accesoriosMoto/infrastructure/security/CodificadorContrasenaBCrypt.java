package com.uniquindio.accesoriosMoto.infrastructure.security;

import org.springframework.stereotype.Component;

@Component
public class CodificadorContrasenaBCrypt implements CodificadorContrasena {
    private final PasswordEncoder encoder;
    public CodificadorContrasenaBCrypt(PasswordEncoder encoder) { this.encoder = encoder; }
    @Override
    public String codificar(String valor) {
        try {
            return encoder.encode(valor);
        } catch (IllegalArgumentException e) {
            throw new com.uniquindio.accesoriosMoto.application.exception.ContrasenaInvalidaException();
        }
    }
    @Override
    public boolean coincide(String valorPlano, String valorCodificado) {
        try {
            return valorPlano != null && valorCodificado != null && encoder.matches(valorPlano, valorCodificado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
