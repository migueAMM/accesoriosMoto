package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Casco;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RegistrarCascoUseCase {
    private final ProductoRepository repository;

    public RegistrarCascoUseCase(ProductoRepository repository) {
        this.repository = repository;
    }

    public Casco ejecutar(String id, Marca marca, String modelo, String color, Talla talla, Precio precio, Proteccion proteccion, String certificacionSeguridad){
        Casco casco = Casco.crear(id, marca, modelo, color, talla, precio, proteccion, certificacionSeguridad);
        repository.guardar(casco);
        return casco;
    }
}
