package com.uniquindio.accesoriosMoto.application.exception;

public class CorreoDuplicadoException extends RuntimeException {

    public CorreoDuplicadoException() {
        super("El correo ya está registrado");
    }
}
