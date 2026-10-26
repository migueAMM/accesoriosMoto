package com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.domain.entity.Usuario;
import com.uniquindio.accesoriosMoto.domain.repository.UsuarioRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.UsuarioPersistenceMapper;
import org.springframework.dao.DataIntegrityViolationException;
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
            String mensaje = e.getMostSpecificCause().getMessage();
            if (mensaje != null && mensaje.toLowerCase(Locale.ROOT).contains("uk_usuarios_correo")) {
                throw new CorreoDuplicadoException();
            }
            throw e;
        }
    }
}
