package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Visor;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.Marca;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrarVisorUseCase {

    private final ProductoRepository repository;

    @Transactional
    public Visor ejecutar(String id, Marca marca, Precio precio, String color, String tipoVisor, List<String> modelosCascoCompatibles) {
        Visor visor = Visor.crear(id, marca, precio, color, tipoVisor, modelosCascoCompatibles);
        repository.guardar(visor);
        return visor;
    }
}
