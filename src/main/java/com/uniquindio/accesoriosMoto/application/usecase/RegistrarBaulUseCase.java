package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Baul;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Material;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrarBaulUseCase {

    private final ProductoRepository repository;

    @Transactional
    public Baul ejecutar(String id, Marca marca, Material material, double capacidadLitros, String dimensiones, Precio precio, double capacidadMaximaCarga, String color) {
        Baul baul = Baul.crear(id, marca, material, capacidadLitros, dimensiones, precio, capacidadMaximaCarga, color);
        repository.guardar(baul);
        return baul;
    }
}
