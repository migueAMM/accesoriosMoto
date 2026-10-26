package com.uniquindio.accesoriosMoto.application.exception;

public class ContrasenaInvalidaException extends RuntimeException {
    public ContrasenaInvalidaException() {
        super("La contraseña no es compatible con el codificador");
    }
}
