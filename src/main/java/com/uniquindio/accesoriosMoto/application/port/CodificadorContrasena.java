package com.uniquindio.accesoriosMoto.application.port;

public interface CodificadorContrasena {
    String codificar(String valor);
    boolean coincide(String valorPlano, String valorCodificado);
}