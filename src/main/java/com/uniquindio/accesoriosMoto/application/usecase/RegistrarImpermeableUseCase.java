package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Impermeable;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrarImpermeableUseCase {

    private final ProductoRepository repository;

    @Transactional
    public Impermeable ejecutar(String id, Marca marca, Precio precio, String color, Talla talla, Material material) {
        Impermeable impermeable = Impermeable.crear(id, marca, precio, color, talla, material);
        repository.guardar(impermeable);
        return impermeable;
    }
}
