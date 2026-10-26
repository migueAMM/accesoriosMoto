package com.uniquindio.accesoriosMoto.infrastructure.security;

public final class IdentidadComprador {
    private IdentidadComprador() {}
    public static String verificar(Authentication autenticacion, String compradorSolicitado) {
        if (autenticacion == null || !autenticacion.getName().equals(compradorSolicitado)) {
            throw new AccesoDenegadoException();
        }
        return autenticacion.getName();
    }
}
