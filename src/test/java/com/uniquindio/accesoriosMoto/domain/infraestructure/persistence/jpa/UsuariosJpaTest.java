package com.uniquindio.accesoriosMoto.domain.infraestructure.persistence.jpa;

import com.uniquindio.accesoriosMoto.application.exception.CorreoDuplicadoException;
import com.uniquindio.accesoriosMoto.domain.entity.Administrador;
import com.uniquindio.accesoriosMoto.domain.entity.Comprador;
import com.uniquindio.accesoriosMoto.domain.repository.CompradorRepository;
import com.uniquindio.accesoriosMoto.domain.repository.UsuarioRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.CompradorRepositoryJpa;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.UsuarioRepositoryJpa;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.CompradorPersistenceMapper;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.mapper.UsuarioPersistenceMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(showSql=false)
@Import({UsuarioRepositoryJpa.class, UsuarioPersistenceMapper.class, CompradorPersistenceMapper.class, CompradorRepositoryJpa.class})
class UsuariosJpaTest {
    @Autowired
    UsuarioRepository usuarios;
    @Autowired
    CompradorRepository compradores;
    @Autowired
    EntityManager em;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void ambosTiposSeRecuperanSinDuplicarCredenciales() {
        String hash = encoder.encode("ClaveSegura123");
        usuarios.guardar(new Comprador("c1", "Ana", "ana@correo.com", hash));
        usuarios.guardar(new Administrador("a1", "Admin", "admin@correo.com", hash));
        em.flush(); em.clear();
        assertThat(usuarios.obtenerPorCorreo(" ANA@CORREO.COM ").orElseThrow()).isInstanceOf(Comprador.class);
        assertThat(usuarios.obtenerPorCorreo("admin@correo.com").orElseThrow()).isInstanceOf(Administrador.class);
        assertThat(compradores.obtenerPorId("c1").orElseThrow().getContrasena()).isEqualTo(hash);
        assertThat(compradores.obtenerPorId("a1")).isEmpty();
        assertThat(em.createNativeQuery("select count(*) from usuarios").getSingleResult()).isEqualTo(2L);
        assertThat(usuarios.obtenerPorCorreo("nadie@correo.com")).isEmpty();
    }
    @Test void correoEsUnicoEntreCompradorYAdministradorEnLaBase() {
        String hash = encoder.encode("ClaveSegura123");
        usuarios.guardar(new Comprador("c1", "Ana", "ana@correo.com", hash));
        assertThatThrownBy(() -> usuarios.guardar(new Administrador("a1", "Admin", "ANA@CORREO.COM", hash)))
                .isInstanceOf(CorreoDuplicadoException.class);
    }
}
