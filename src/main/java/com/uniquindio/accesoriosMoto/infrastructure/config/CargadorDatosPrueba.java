package com.uniquindio.accesoriosMoto.infrastructure.config;

import com.uniquindio.accesoriosMoto.domain.entity.*;
import com.uniquindio.accesoriosMoto.domain.repository.ProductoRepository;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.ProductoJpaDataRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.AdministradorEntity;
import com.uniquindio.accesoriosMoto.infrastructure.persistence.jpa.entity.CompradorEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.datos-prueba.enabled", havingValue = "true", matchIfMissing = true)
public class CargadorDatosPrueba implements CommandLineRunner {

    private final UsuarioJpaDataRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ProductoJpaDataRepository productoJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        cargarUsuarios();
        cargarProductos();
    }

    private void cargarUsuarios() {
        if (usuarioRepository.findByCorreo("admin@moto.com").isEmpty()) {
            AdministradorEntity admin = new AdministradorEntity();
            admin.setId(UUID.randomUUID().toString());
            admin.setNombre("Admin Tienda");
            admin.setCorreo("admin@moto.com");
            admin.setContrasena(passwordEncoder.encode("admin123"));
            usuarioRepository.save(admin);
            log.info("Usuario administrador de prueba creado: admin@moto.com / admin123");
        }

        if (usuarioRepository.findByCorreo("comprador@moto.com").isEmpty()) {
            CompradorEntity comprador = new CompradorEntity();
            comprador.setId(UUID.randomUUID().toString());
            comprador.setNombre("Pepito Motero");
            comprador.setCorreo("comprador@moto.com");
            comprador.setContrasena(passwordEncoder.encode("comprador123"));
            usuarioRepository.save(comprador);
            log.info("Usuario comprador de prueba creado: comprador@moto.com / comprador123");
        }
    }

    private void cargarProductos() {
        if (productoJpaRepository.count() > 0) {
            log.info("Ya existen productos en la base de datos, omitiendo carga inicial de productos.");
            return;
        }

        Marca agv = new Marca("m-agv", "AGV");
        Marca alpinestars = new Marca("m-alp", "Alpinestars");
        Marca fox = new Marca("m-fox", "Fox Racing");
        Marca givi = new Marca("m-giv", "Givi");
        Marca shaft = new Marca("m-sha", "Shaft");
        Marca cardo = new Marca("m-car", "Cardo");
        Marca quadlock = new Marca("m-qua", "Quad Lock");

        // 1. Casco
        Casco casco = Casco.crear(
                "prod-casco-1",
                agv,
                "K3 SV",
                "Negro Mate",
                Talla.M,
                new Precio(850000, "COP"),
                Proteccion.NIVEL_1,
                "ECE 22.05 / DOT"
        );
        casco.publicar();
        productoRepository.guardar(casco);

        // 2. Chaqueta
        Chaqueta chaqueta = Chaqueta.crear(
                "prod-chaqueta-1",
                alpinestars,
                new Precio(1200000, "COP"),
                "Negro / Rojo",
                Talla.L,
                Material.CUERO,
                Proteccion.NIVEL_2
        );
        chaqueta.publicar();
        productoRepository.guardar(chaqueta);

        // 3. Guantes
        Guantes guantes = Guantes.crear(
                "prod-guantes-1",
                fox,
                new Precio(150000, "COP"),
                "Negro",
                Talla.M,
                Material.TEXTIL,
                Proteccion.NIVEL_1
        );
        guantes.publicar();
        productoRepository.guardar(guantes);

        // 4. Baul con compatibilidad
        Baul baul = Baul.crear(
                "prod-baul-1",
                givi,
                Material.ABS,
                45,
                "45x40x30",
                new Precio(450000, "COP"),
                10,
                "Negro"
        );
        baul.indicarCompatibilidad(new Compatibilidad(TipoCompatibilidad.MOTO, "Yamaha MT-07"));
        baul.publicar();
        productoRepository.guardar(baul);

        // 5. Impermeable
        Impermeable impermeable = Impermeable.crear(
                "prod-imper-1",
                shaft,
                new Precio(120000, "COP"),
                "Amarillo Fluor",
                Talla.L,
                Material.PVC
        );
        impermeable.publicar();
        productoRepository.guardar(impermeable);

        // 6. Intercomunicador
        Intercomunicador intercom = Intercomunicador.crear(
                "prod-inter-1",
                cardo,
                new Precio(950000, "COP"),
                "Negro",
                1200,
                13,
                true
        );
        intercom.publicar();
        productoRepository.guardar(intercom);

        // 7. Soporte Celular
        SoporteCelular soporte = SoporteCelular.crear(
                "prod-soporte-1",
                quadlock,
                new Precio(180000, "COP"),
                "Negro",
                "Manillar"
        );
        soporte.publicar();
        productoRepository.guardar(soporte);

        // 8. Visor compatible
        Visor visor = Visor.crear(
                "prod-visor-1",
                agv,
                new Precio(140000, "COP"),
                "Humo Oscuro",
                "Antifog Pinlock",
                List.of("K3 SV")
        );
        visor.publicar();
        productoRepository.guardar(visor);

        log.info("Catálogo inicial de accesorios para moto cargado exitosamente con 8 productos.");
    }
}
