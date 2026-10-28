package com.uniquindio.accesoriosMoto.domain.infraestructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.UsuarioRepositoryJpa;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.CompradorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.UsuarioPersistenceMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UsuarioRepositoryJpaConflictoTest {
    private UsuarioRepositoryJpa prepararAdapter(DataIntegrityViolationException error, Comprador comprador) {
        var data=mock(UsuarioJpaDataRepository.class);
        var mapper=mock(UsuarioPersistenceMapper.class);
        var entity=new CompradorEntity();
        when(mapper.toEntity(comprador)).thenReturn(entity);
        when(data.saveAndFlush(entity)).thenThrow(error);
        return new UsuarioRepositoryJpa(data,mapper);
    }
    @Test
    public void noClasificaPorTextoAunqueElMensajeMencioneCorreo() {
        var error = new DataIntegrityViolationException("uk_usuarios_correo");
        var comprador = new Comprador("c1","Ana","ana@gmail.com","hash");
        var adapter = prepararAdapter(error, comprador);

        var accionError = catchThrowable(() -> adapter.guardar(comprador));

        assertSame(error, assertInstanceOf(DataIntegrityViolationException.class, accionError));
    }

    @Test
    public void otraRestriccionNoSeConvierteEnConflictoDeCorreo() {
        var causa = new ConstraintViolationException("Otro conflicto",new SQLException("Sin detalle","23505"),"UK_OTRA_RESTRICCION");
        var error = new DataIntegrityViolationException("Integridad", causa);
        var comprador = new Comprador("c1","Ana","ana@gmail.com","hash");
        var adapter = prepararAdapter(error, comprador);

        var accionError = catchThrowable(() -> adapter.guardar(comprador));

        assertSame(error, assertInstanceOf(DataIntegrityViolationException.class, accionError));
    }
}
