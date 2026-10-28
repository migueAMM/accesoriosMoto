package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.SoporteCelular;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrarSoporteCelularUseCase {
    private final ProductoRepository repository;

    @Transactional
    public SoporteCelular ejecutar(String id, Marca marca, Precio precio, String color, String tipoSujecion) {
        SoporteCelular soporteCelular = SoporteCelular.crear(id, marca, precio, color, tipoSujecion);
        repository.guardar(soporteCelular);
        return soporteCelular;
    }
}
