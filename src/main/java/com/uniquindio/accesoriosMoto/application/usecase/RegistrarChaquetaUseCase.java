package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Chaqueta;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import org.springframework.stereotype.Service;

@Service
public class RegistrarChaquetaUseCase {
    private final ProductoRepository repository;

    public RegistrarChaquetaUseCase(ProductoRepository repository) {
        this.repository = repository;
    }

    public Chaqueta ejecutar(String id, Marca marca, Precio precio, String color, Talla talla, Material material, Proteccion proteccion) {
        Chaqueta chaqueta = Chaqueta.crear(id, marca, precio, color, talla, material, proteccion);
        repository.guardar(chaqueta);
        return chaqueta;
    }
}

