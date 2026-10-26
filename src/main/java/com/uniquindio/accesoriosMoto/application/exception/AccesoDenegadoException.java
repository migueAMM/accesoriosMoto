package com.uniquindio.accesoriosMoto.application.exception;

public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException() {
        super("No tiene permiso para realizar esta operación");
    }
}
