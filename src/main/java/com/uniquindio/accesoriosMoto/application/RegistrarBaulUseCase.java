package com.uniquindio.accesoriosMoto.application;

import com.uniquindio.accesoriosMoto.domain.entity.Baul;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;

public class RegistrarBaulUseCase {
    private final ProductoRepository repository;

    public RegistrarBaulUseCase(ProductoRepository repository) {
        this.repository = repository;
    }

    public Baul ejecutar(String id, Marca marca, Material material, String color, int capacidadLitros, double capacidadMaximaCarga, String dimensiones, Precio precio){
        Baul baul = Baul.crear(id, marca, material, color, capacidadLitros, capacidadMaximaCarga, dimensiones, precio);
        repository.guardar(baul);
        return baul;
    }
}
