package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.SoporteCelular;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPublicacion;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;

public class RegistrarSoporteCelularUseCase {
    private final ProductoRepository repository;

    public RegistrarSoporteCelularUseCase(ProductoRepository repository) {
        this.repository = repository;
    }

    public SoporteCelular ejecutar(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, String tipoSujecion) {
        SoporteCelular soporteCelular = SoporteCelular.crear(id, marca, precio, color, tipoSujecion);
        repository.guardar(soporteCelular);
        return soporteCelular;
    }
}
