package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.domain.entity.Usuario;
import com.uniquindio.accesoriosMoto.domain.repository.UsuarioRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.UsuarioPersistenceMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Repository
@Transactional(readOnly=true)
public class UsuarioRepositoryJpa implements UsuarioRepository {

    private final UsuarioJpaDataRepository repository;

    private final UsuarioPersistenceMapper mapper;

    public UsuarioRepositoryJpa(UsuarioJpaDataRepository repository, UsuarioPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return repository.findByCorreo(correo.strip().toLowerCase(Locale.ROOT)).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void guardar(Usuario usuario) {
        try {
            repository.saveAndFlush(mapper.toEntity(usuario));
        } catch (DataIntegrityViolationException e) {
            for (Throwable causa = e; causa != null; causa = causa.getCause()) {
                if (causa instanceof org.hibernate.exception.ConstraintViolationException restriccion) {
                    String nombre = restriccion.getConstraintName();
                    if (nombre != null) {
                        nombre = nombre.replace("\"", "").toUpperCase(Locale.ROOT).split("\\s+", 2)[0];
                        if (nombre.startsWith("PUBLIC.")) nombre = nombre.substring(7);
                        if (nombre.equals("UK_USUARIOS_CORREO") || nombre.startsWith("UK_USUARIOS_CORREO_INDEX_")) {
                            throw new DuplicateKeyException("El correo ya está registrado", e);
                        }
                    }
                }
            }
            throw e;
        }
    }
}
