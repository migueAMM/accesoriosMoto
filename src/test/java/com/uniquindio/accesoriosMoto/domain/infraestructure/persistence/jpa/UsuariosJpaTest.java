package com.uniquindio.accesoriosMoto.domain.infraestructure.persistence.jpa;


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
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@DataJpaTest(showSql=false)
@Import({
        UsuarioRepositoryJpa.class,
        UsuarioPersistenceMapper.class,
        CompradorPersistenceMapper.class,
        CompradorRepositoryJpa.class})

public class UsuariosJpaTest {

    @Autowired
    UsuarioRepository usuarios;
    @Autowired
    CompradorRepository compradores;
    @Autowired
    EntityManager em;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    public void ambosTiposSeRecuperanSinDuplicarCredenciales() {

        String hash = encoder.encode("ClaveSegura123");

        usuarios.guardar(new Comprador("c1", "Ana", "ana@correo.com", hash));
        usuarios.guardar(new Administrador("a1", "Admin", "admin@correo.com", hash));
        em.flush();
        em.clear();
        var resultado1 = usuarios.obtenerPorCorreo(" ANA@CORREO.COM ").orElseThrow();
        var resultado2 = usuarios.obtenerPorCorreo("admin@correo.com").orElseThrow();
        var resultado3 = compradores.obtenerPorId("c1").orElseThrow().getContrasena();
        var resultado4 = compradores.obtenerPorId("a1");
        var resultado5 = em.createNativeQuery("select count(*) from usuarios").getSingleResult();
        var resultado6 = usuarios.obtenerPorCorreo("nadie@correo.com");

        assertThat(resultado1).isInstanceOf(Comprador.class);
        assertThat(resultado2).isInstanceOf(Administrador.class);
        assertThat(resultado3).isEqualTo(hash);
        assertThat(resultado4).isEmpty();
        assertThat(resultado5).isEqualTo(2L);
        assertThat(resultado6).isEmpty();
    }

    @Test
    public void correoEsUnicoEntreCompradorYAdministradorEnLaBase() {
        String hash = encoder.encode("ClaveSegura123");
        usuarios.guardar(new Comprador("c1", "Ana", "ana@gmail.com", hash));

        var accion1Error = catchThrowable(() -> usuarios.guardar(new Administrador("a1", "Admin", "ANA@GMAIL.COM", hash)));

        assertThat(accion1Error)
                .isInstanceOf(DuplicateKeyException.class);
    }
}
