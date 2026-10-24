package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Intercomunicador;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;

public class RegistrarIntercomunicadorUseCase {
    private final ProductoRepository repository;

    public RegistrarIntercomunicadorUseCase(ProductoRepository repository) {
        this.repository = repository;
    }

    public Intercomunicador ejecutar(String id, Marca marca, Precio precio, String color, int alcanceMetros, int autonomiaHoras,boolean bluetooth) {
        Intercomunicador intercomunicador = Intercomunicador.crear(id, marca, precio, color, alcanceMetros, autonomiaHoras, bluetooth);
        repository.guardar(intercomunicador);
        return intercomunicador;
    }
}
