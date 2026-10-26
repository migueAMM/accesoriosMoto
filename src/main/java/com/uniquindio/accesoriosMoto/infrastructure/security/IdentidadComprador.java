package com.uniquindio.accesoriosMoto.infrastructure.security;

import com.uniquindio.accesoriosMoto.application.exception.AccesoDenegadoException;
import org.springframework.security.core.Authentication;

public final class IdentidadComprador {
    private IdentidadComprador() {}
    public static String verificar(Authentication autenticacion, String compradorSolicitado) {
        if (autenticacion == null || !autenticacion.getName().equals(compradorSolicitado)) {
            throw new AccesoDenegadoException();
        }
        return autenticacion.getName();
    }
}
