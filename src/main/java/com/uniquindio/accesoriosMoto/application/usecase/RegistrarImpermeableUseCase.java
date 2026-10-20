package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Impermeable;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;

public class RegistrarImpermeableUseCase {
    private final ProductoRepository repository;

    public RegistrarImpermeableUseCase(ProductoRepository repository) {
        this.repository = repository;
    }

    public Impermeable ejecutar(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla, Material material) {
        Impermeable impermeable = Impermeable.crear(id, marca, precio, color, talla, material);
        repository.guardar(impermeable);
        return impermeable;
    }
}
