package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Casco;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrarCascoUseCase {

    private final ProductoRepository repository;

    @Transactional
    public Casco ejecutar(String id, Marca marca, String modelo, String color, Talla talla, Precio precio, Proteccion proteccion, String certificacionSeguridad){
        Casco casco = Casco.crear(id, marca, modelo, color, talla, precio, proteccion, certificacionSeguridad);
        repository.guardar(casco);
        return casco;
    }
}
