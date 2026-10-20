package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Guantes;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;

public class RegistrarGuantesUseCase {
    private final ProductoRepository repository;

    public RegistrarGuantesUseCase(ProductoRepository repository) {
        this.repository = repository;
    }

    public Guantes ejecutar(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, Talla talla, Material material, Proteccion proteccion) {
        Guantes guantes = Guantes.crear(id, marca, precio, color, talla, material, proteccion);
        repository.guardar(guantes);
        return guantes;
    }
}
