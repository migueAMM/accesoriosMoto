package com.uniquindio.accesoriosMoto.domain.infraestructure.security;

import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.AdministradorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.CompradorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.RolSeguridad;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.UsuarioEntity;
import com.uniquindio.accesoriosMoto.infrastructure.security.UsuariosIniciales;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class UsuariosInicialesTest {
    private UsuariosIniciales runner(UsuarioJpaDataRepository usuarios, Validator validator, String nombre, String correo, String clave) {
        var runner = new UsuariosIniciales(usuarios, new BCryptPasswordEncoder(), validator);
        ReflectionTestUtils.setField(runner,"nombre",nombre);
        ReflectionTestUtils.setField(runner,"correo",correo);
        ReflectionTestUtils.setField(runner,"contrasena",clave);
        return runner;
    }
    @Test
    public void creaAdminConHashYSiguienteInicioNoCambiaSuClave() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {

            var usuarios = mock(UsuarioJpaDataRepository.class);
            Map<String, UsuarioEntity> datos = new HashMap<>();
            when(usuarios.findByCorreo(anyString())).thenAnswer(i -> Optional.ofNullable(datos.get(i.getArgument(0))));
            when(usuarios.save(any())).thenAnswer(i -> { UsuarioEntity u=i.getArgument(0); datos.put(u.getCorreo(),u);return u; });
            var primero = runner(usuarios,factory.getValidator(),"Admin"," ADMIN@CORREO.COM ","ClaveSegura123");
            var segundo = runner(usuarios,factory.getValidator(),"Otro","admin@correo.com","ClaveDistinta123");

            primero.run();
            var original = datos.get("admin@correo.com");
            boolean claveCorrecta = new BCryptPasswordEncoder().matches("ClaveSegura123", original.getContrasena());
            segundo.run();

            assertThat(original).isInstanceOf(AdministradorEntity.class);
            assertThat(claveCorrecta).isTrue();
            assertThat(datos).hasSize(1);
            assertThat(datos.get("admin@correo.com")).isSameAs(original);
            verify(usuarios,times(1)).save(any());
        }
    }
    @Test
    public void nuncaPromueveCompradorExistenteAAdministrador() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {

            var usuarios = mock(UsuarioJpaDataRepository.class);
            var comprador = new CompradorEntity();
            comprador.setId("c1"); comprador.setCorreo("ana@correo.com");
            when(usuarios.findByCorreo("ana@correo.com")).thenReturn(Optional.of(comprador));
            var inicializador = runner(usuarios,factory.getValidator(),"Admin","ana@correo.com","ClaveSegura123");

            ThrowableAssert.ThrowingCallable accion = inicializador::run;

            assertThatThrownBy(accion).isInstanceOf(IllegalStateException.class);
            assertThat(comprador.getRol()).isEqualTo(RolSeguridad.COMPRADOR);
            verify(usuarios,never()).save(any());
        }
    }
}

