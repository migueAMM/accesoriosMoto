package com.uniquindio.accesoriosMoto.application.usecase;

import com.uniquindio.accesoriosMoto.domain.entity.Pedido;
import com.uniquindio.accesoriosMoto.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarPedidosUseCase {

    private final PedidoRepository repository;

    @Transactional(readOnly = true)
    public List<Pedido> ejecutar(String compradorId){
        return repository.listarPorComprador(compradorId);
    }
}
