package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Guantes;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrarGuantesUseCase {

    private final ProductoRepository repository;

    @Transactional
    public Guantes ejecutar(String id, Marca marca, Precio precio, String color, Talla talla, Material material, Proteccion proteccion) {
        Guantes guantes = Guantes.crear(id, marca, precio, color, talla, material, proteccion);
        repository.guardar(guantes);
        return guantes;
    }
}
