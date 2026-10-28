package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Intercomunicador;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrarIntercomunicadorUseCase {

    private final ProductoRepository repository;

    @Transactional
    public Intercomunicador ejecutar(String id, Marca marca, Precio precio, String color, int alcanceMetros, int autonomiaHoras,boolean bluetooth) {
        Intercomunicador intercomunicador = Intercomunicador.crear(id, marca, precio, color, alcanceMetros, autonomiaHoras, bluetooth);
        repository.guardar(intercomunicador);
        return intercomunicador;
    }
}
