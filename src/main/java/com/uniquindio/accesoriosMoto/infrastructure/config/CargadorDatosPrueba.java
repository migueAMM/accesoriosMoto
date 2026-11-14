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
        actualizarSemillasExistentes();
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
            log.info("Usuario administrador creado: admin@moto.com / admin123");
        }

        if (usuarioRepository.findByCorreo("comprador@moto.com").isEmpty()) {
            CompradorEntity comprador = new CompradorEntity();
            comprador.setId(UUID.randomUUID().toString());
            comprador.setNombre("Pepito Motero");
            comprador.setCorreo("comprador@moto.com");
            comprador.setContrasena(passwordEncoder.encode("comprador123"));
            usuarioRepository.save(comprador);
            log.info("Usuario comprador creado: comprador@moto.com / comprador123");
        }
    }

    private void actualizarSemillasExistentes() {
        actualizarImagenSiCorresponde("seed-casco-1", "/productos/casco-agv-k3-negro.jpg");
        actualizarImagenSiCorresponde("seed-casco-2", "/productos/casco-ls2-blanco.jpg");
        actualizarImagenSiCorresponde("seed-chaqueta-1", "/productos/chaqueta-alpinestars-cuero.jpg");
        actualizarImagenSiCorresponde("seed-guantes-1", "/productos/guantes-dainese-negro.jpg");
        actualizarImagenSiCorresponde("seed-impermeable-1", "/productos/impermeable-rainwear-azul.jpg");
        actualizarImagenSiCorresponde("seed-intercom-1", "/productos/intercomunicador-cardo-gris.jpg");
        actualizarImagenSiCorresponde("seed-soporte-1", "/productos/soporte-quadlock-negro.jpg");
        actualizarImagenSiCorresponde("seed-baul-1", "/productos/baul-givi-negro.jpg");
        actualizarImagenSiCorresponde("seed-visor-1", "/productos/visor-agv-transparente.jpg");
    }

    private void actualizarImagenSiCorresponde(String id, String imagenUrl) {
        productoRepository.obtenerPorId(id).ifPresent(p -> {
            p.setImagenUrl(imagenUrl);
            productoRepository.guardar(p);
        });
    }

    private void guardar(Producto p) {
        p.publicar();
        productoRepository.guardar(p);
    }

    private void cargarProductos() {
        log.info("Sembrando/actualizando catálogo de 160 productos (20 por categoría) con imágenes reales de tienda en fondo blanco...");

        Marca agv = new Marca("m-agv", "AGV");
        Marca ls2 = new Marca("m-ls2", "LS2");
        Marca shoei = new Marca("m-shoei", "Shoei");
        Marca hjc = new Marca("m-hjc", "HJC");
        Marca bell = new Marca("m-bell", "Bell");
        Marca shark = new Marca("m-shark", "Shark");
        Marca alpinestars = new Marca("m-alp", "Alpinestars");
        Marca dainese = new Marca("m-dai", "Dainese");
        Marca revit = new Marca("m-rev", "Revit");
        Marca givi = new Marca("m-giv", "Givi");
        Marca shad = new Marca("m-shad", "Shad");
        Marca kappa = new Marca("m-kap", "Kappa");
        Marca rainwear = new Marca("m-rai", "Rainwear");
        Marca cyclone = new Marca("m-cyc", "Cyclone");
        Marca cardo = new Marca("m-car", "Cardo");
        Marca sena = new Marca("m-sen", "Sena");
        Marca freedconn = new Marca("m-fcon", "FreedConn");
        Marca quadlock = new Marca("m-qua", "Quad Lock");
        Marca ram = new Marca("m-ram", "RAM Mounts");
        Marca motowolf = new Marca("m-mw", "Motowolf");

        cargarCascos(agv, ls2, shoei, hjc, bell, shark);
        cargarChaquetas(alpinestars, dainese, revit);
        cargarGuantes(alpinestars, dainese, revit, motowolf);
        cargarBaules(givi, shad, kappa);
        cargarImpermeables(rainwear, cyclone, alpinestars, motowolf);
        cargarIntercomunicadores(cardo, sena, freedconn);
        cargarSoportesCelular(quadlock, ram, motowolf);
        cargarVisores(agv, ls2, shoei, hjc, bell, shark);

        log.info("Carga de catálogo finalizada exitosamente. Total productos en base de datos: {}", productoJpaRepository.count());
    }

    private void cargarCascos(Marca agv, Marca ls2, Marca shoei, Marca hjc, Marca bell, Marca shark) {
        guardar(Casco.crear("casco-01", agv, "K3 SV", "Negro Mate", Talla.M, new Precio(850000, "COP"), Proteccion.NIVEL_2, "ECE 22.05 / DOT", "/productos/casco-agv-k3-negro.jpg"));
        guardar(Casco.crear("casco-02", ls2, "Rookie FF352", "Blanco Puro", Talla.L, new Precio(420000, "COP"), Proteccion.NIVEL_1, "ECE 22.05", "/productos/casco-ls2-blanco.jpg"));
        guardar(Casco.crear("casco-03", agv, "Pista GP RR", "Carbono Rojo", Talla.S, new Precio(4200000, "COP"), Proteccion.NIVEL_5, "FIM / ECE 22.06", "/productos/casco-carbono-rojo.jpg"));
        guardar(Casco.crear("casco-04", shoei, "X-Spirit 3", "Negro Racing", Talla.M, new Precio(3200000, "COP"), Proteccion.NIVEL_4, "Snell M2020 / ECE 22.05", "/productos/casco-agv-k3-negro.jpg"));
        guardar(Casco.crear("casco-05", hjc, "RPHA 11 Pro", "Blanco Perla", Talla.L, new Precio(1950000, "COP"), Proteccion.NIVEL_3, "DOT / ECE 22.05", "/productos/casco-ls2-blanco.jpg"));
        guardar(Casco.crear("casco-06", ls2, "Stream II FF808", "Titanio Mate", Talla.XL, new Precio(530000, "COP"), Proteccion.NIVEL_2, "ECE 22.06", "/productos/casco-scorpion-exo.jpg"));
        guardar(Casco.crear("casco-07", bell, "Qualifier DLX", "Negro Satinado", Talla.M, new Precio(790000, "COP"), Proteccion.NIVEL_2, "DOT / ECE", "/productos/casco-agv-k3-negro.jpg"));
        guardar(Casco.crear("casco-08", shark, "Spartan GT Carbon", "Carbono Rojo Skin", Talla.L, new Precio(1750000, "COP"), Proteccion.NIVEL_3, "ECE 22.05", "/productos/casco-carbono-rojo.jpg"));
        guardar(Casco.crear("casco-09", shoei, "Glamster", "Negro Clásico", Talla.M, new Precio(2300000, "COP"), Proteccion.NIVEL_3, "ECE 22.05", "/productos/casco-scorpion-exo.jpg"));
        guardar(Casco.crear("casco-10", hjc, "i90 Modular", "Blanco Glaciar", Talla.XL, new Precio(920000, "COP"), Proteccion.NIVEL_2, "DOT / ECE", "/productos/casco-ls2-blanco.jpg"));
        guardar(Casco.crear("casco-11", agv, "K1 S", "Negro / Rojo Corsa", Talla.XS, new Precio(820000, "COP"), Proteccion.NIVEL_2, "ECE 22.06", "/productos/casco-carbono-rojo.jpg"));
        guardar(Casco.crear("casco-12", ls2, "Rapid FF353", "Negro Brillante", Talla.S, new Precio(370000, "COP"), Proteccion.NIVEL_1, "ECE 22.05", "/productos/casco-agv-k3-negro.jpg"));
        guardar(Casco.crear("casco-13", bell, "Eliminator", "Negro Mate Total", Talla.L, new Precio(1350000, "COP"), Proteccion.NIVEL_3, "DOT / ECE", "/productos/casco-scorpion-exo.jpg"));
        guardar(Casco.crear("casco-14", shark, "Skwal 2 Carbon", "Carbono Rojo Sport", Talla.M, new Precio(1190000, "COP"), Proteccion.NIVEL_2, "ECE 22.05", "/productos/casco-carbono-rojo.jpg"));
        guardar(Casco.crear("casco-15", shoei, "NXR 2", "Blanco Nieve", Talla.M, new Precio(2750000, "COP"), Proteccion.NIVEL_4, "ECE 22.06", "/productos/casco-ls2-blanco.jpg"));
        guardar(Casco.crear("casco-16", hjc, "CS-15", "Negro Mate", Talla.S, new Precio(490000, "COP"), Proteccion.NIVEL_1, "ECE 22.05", "/productos/casco-agv-k3-negro.jpg"));
        guardar(Casco.crear("casco-17", ls2, "Explorer Carbon MX701", "Fibra de Carbono Rojo", Talla.XL, new Precio(1580000, "COP"), Proteccion.NIVEL_4, "ECE 22.06", "/productos/casco-carbono-rojo.jpg"));
        guardar(Casco.crear("casco-18", agv, "AX9 Carbon", "Negro Aventura", Talla.L, new Precio(2450000, "COP"), Proteccion.NIVEL_3, "ECE 22.05", "/productos/casco-scorpion-exo.jpg"));
        guardar(Casco.crear("casco-19", bell, "Custom 500", "Blanco Vintage", Talla.M, new Precio(680000, "COP"), Proteccion.NIVEL_1, "DOT", "/productos/casco-ls2-blanco.jpg"));
        guardar(Casco.crear("casco-20", shark, "Evo-ES Modular", "Gris Oscuro Mate", Talla.L, new Precio(1390000, "COP"), Proteccion.NIVEL_2, "ECE 22.05", "/productos/casco-scorpion-exo.jpg"));
    }

    private void cargarChaquetas(Marca alpinestars, Marca dainese, Marca revit) {
        guardar(Chaqueta.crear("chaqueta-01", alpinestars, new Precio(890000, "COP"), "Negro / Rojo Deportivo", Talla.L, Material.TEXTIL, Proteccion.NIVEL_2, "/productos/chaqueta-textil-roja.jpg"));
        guardar(Chaqueta.crear("chaqueta-02", dainese, new Precio(1850000, "COP"), "Negro Mate", Talla.M, Material.CUERO, Proteccion.NIVEL_3, "/productos/chaqueta-alpinestars-cuero.jpg"));
        guardar(Chaqueta.crear("chaqueta-03", revit, new Precio(1650000, "COP"), "Gris Touring / Flúor", Talla.L, Material.TEXTIL, Proteccion.NIVEL_3, "/productos/chaqueta-touring-gris.jpg"));
        guardar(Chaqueta.crear("chaqueta-04", alpinestars, new Precio(1450000, "COP"), "Marrón Vintage Café Racer", Talla.M, Material.CUERO, Proteccion.NIVEL_2, "/productos/chaqueta-cuero-marron.jpg"));
        guardar(Chaqueta.crear("chaqueta-05", dainese, new Precio(2600000, "COP"), "Rojo / Negro Corsa", Talla.M, Material.CUERO, Proteccion.NIVEL_5, "/productos/chaqueta-textil-roja.jpg"));
        guardar(Chaqueta.crear("chaqueta-06", revit, new Precio(680000, "COP"), "Gris Aventura / Neón", Talla.S, Material.TEXTIL, Proteccion.NIVEL_2, "/productos/chaqueta-touring-gris.jpg"));
        guardar(Chaqueta.crear("chaqueta-07", alpinestars, new Precio(1750000, "COP"), "Negro Total", Talla.XL, Material.CUERO, Proteccion.NIVEL_3, "/productos/chaqueta-alpinestars-cuero.jpg"));
        guardar(Chaqueta.crear("chaqueta-08", dainese, new Precio(1550000, "COP"), "Marrón Caramelo", Talla.L, Material.CUERO, Proteccion.NIVEL_2, "/productos/chaqueta-cuero-marron.jpg"));
        guardar(Chaqueta.crear("chaqueta-09", revit, new Precio(2900000, "COP"), "Gris Glaciar Touring", Talla.XL, Material.NYLON, Proteccion.NIVEL_5, "/productos/chaqueta-touring-gris.jpg"));
        guardar(Chaqueta.crear("chaqueta-10", alpinestars, new Precio(920000, "COP"), "Negro / Rojo Sport", Talla.S, Material.TEXTIL, Proteccion.NIVEL_2, "/productos/chaqueta-textil-roja.jpg"));
        guardar(Chaqueta.crear("chaqueta-11", dainese, new Precio(790000, "COP"), "Negro Aire Perforado", Talla.M, Material.TEXTIL, Proteccion.NIVEL_1, "/productos/chaqueta-alpinestars-cuero.jpg"));
        guardar(Chaqueta.crear("chaqueta-12", revit, new Precio(1480000, "COP"), "Tabaco Marrón", Talla.L, Material.CUERO, Proteccion.NIVEL_2, "/productos/chaqueta-cuero-marron.jpg"));
        guardar(Chaqueta.crear("chaqueta-13", alpinestars, new Precio(2100000, "COP"), "Negro / Blanco Racing", Talla.L, Material.CUERO, Proteccion.NIVEL_4, "/productos/chaqueta-alpinestars-cuero.jpg"));
        guardar(Chaqueta.crear("chaqueta-14", dainese, new Precio(2450000, "COP"), "Gris Reflectivo / Neón", Talla.XL, Material.TEXTIL, Proteccion.NIVEL_4, "/productos/chaqueta-touring-gris.jpg"));
        guardar(Chaqueta.crear("chaqueta-15", revit, new Precio(720000, "COP"), "Rojo Deportivo", Talla.XL, Material.POLIESTER, Proteccion.NIVEL_2, "/productos/chaqueta-textil-roja.jpg"));
        guardar(Chaqueta.crear("chaqueta-16", alpinestars, new Precio(1380000, "COP"), "Marrón Envejecido", Talla.L, Material.CUERO, Proteccion.NIVEL_2, "/productos/chaqueta-cuero-marron.jpg"));
        guardar(Chaqueta.crear("chaqueta-17", dainese, new Precio(1100000, "COP"), "Gris Titanio / Flúor", Talla.XL, Material.TEXTIL, Proteccion.NIVEL_3, "/productos/chaqueta-touring-gris.jpg"));
        guardar(Chaqueta.crear("chaqueta-18", revit, new Precio(590000, "COP"), "Negro Clásico", Talla.XL, Material.POLIESTER, Proteccion.NIVEL_1, "/productos/chaqueta-alpinestars-cuero.jpg"));
        guardar(Chaqueta.crear("chaqueta-19", alpinestars, new Precio(1950000, "COP"), "Gris Asfalto Touring", Talla.L, Material.NYLON, Proteccion.NIVEL_4, "/productos/chaqueta-touring-gris.jpg"));
        guardar(Chaqueta.crear("chaqueta-20", dainese, new Precio(1680000, "COP"), "Cuero Marrón Vintage", Talla.M, Material.CUERO, Proteccion.NIVEL_3, "/productos/chaqueta-cuero-marron.jpg"));
    }

    private void cargarGuantes(Marca alpinestars, Marca dainese, Marca revit, Marca motowolf) {
        guardar(Guantes.crear("guantes-01", alpinestars, new Precio(260000, "COP"), "Negro", Talla.M, Material.CUERO, Proteccion.NIVEL_1, "/productos/guantes-dainese-negro.jpg"));
        guardar(Guantes.crear("guantes-02", dainese, new Precio(620000, "COP"), "Negro / Rojo", Talla.L, Material.CUERO, Proteccion.NIVEL_3, "/productos/guantes-racing-rojo.jpg"));
        guardar(Guantes.crear("guantes-03", alpinestars, new Precio(890000, "COP"), "Rojo / Blanco Racing", Talla.M, Material.KEVLAR, Proteccion.NIVEL_5, "/productos/guantes-racing-rojo.jpg"));
        guardar(Guantes.crear("guantes-04", revit, new Precio(140000, "COP"), "Gris Titanio", Talla.S, Material.TEXTIL, Proteccion.NIVEL_1, "/productos/guantes-dainese-negro.jpg"));
        guardar(Guantes.crear("guantes-05", motowolf, new Precio(95000, "COP"), "Negro Mate", Talla.XL, Material.POLIESTER, Proteccion.NIVEL_1, "/productos/guantes-dainese-negro.jpg"));
        guardar(Guantes.crear("guantes-06", dainese, new Precio(580000, "COP"), "Rojo Corsa / Carbono", Talla.L, Material.CUERO, Proteccion.NIVEL_4, "/productos/guantes-racing-rojo.jpg"));
        guardar(Guantes.crear("guantes-07", alpinestars, new Precio(950000, "COP"), "Rojo Carrera Pro", Talla.M, Material.CUERO, Proteccion.NIVEL_5, "/productos/guantes-racing-rojo.jpg"));
        guardar(Guantes.crear("guantes-08", revit, new Precio(480000, "COP"), "Negro Carbono", Talla.L, Material.CUERO, Proteccion.NIVEL_3, "/productos/guantes-dainese-negro.jpg"));
        guardar(Guantes.crear("guantes-09", dainese, new Precio(320000, "COP"), "Negro / Gris", Talla.S, Material.TEXTIL, Proteccion.NIVEL_1, "/productos/guantes-dainese-negro.jpg"));
        guardar(Guantes.crear("guantes-10", alpinestars, new Precio(290000, "COP"), "Rojo Deportivo", Talla.M, Material.TEXTIL, Proteccion.NIVEL_2, "/productos/guantes-racing-rojo.jpg"));
        guardar(Guantes.crear("guantes-11", revit, new Precio(490000, "COP"), "Negro Cuero", Talla.L, Material.CUERO_SINTETICO, Proteccion.NIVEL_2, "/productos/guantes-dainese-negro.jpg"));
        guardar(Guantes.crear("guantes-12", motowolf, new Precio(130000, "COP"), "Negro Urbano", Talla.XL, Material.TEXTIL, Proteccion.NIVEL_2, "/productos/guantes-dainese-negro.jpg"));
        guardar(Guantes.crear("guantes-13", dainese, new Precio(210000, "COP"), "Negro Clásico", Talla.XS, Material.CUERO, Proteccion.NIVEL_1, "/productos/guantes-dainese-negro.jpg"));
        guardar(Guantes.crear("guantes-14", alpinestars, new Precio(440000, "COP"), "Rojo / Negro Carbono", Talla.M, Material.CUERO, Proteccion.NIVEL_3, "/productos/guantes-racing-rojo.jpg"));
        guardar(Guantes.crear("guantes-15", revit, new Precio(180000, "COP"), "Negro / Rojo", Talla.S, Material.TEXTIL, Proteccion.NIVEL_1, "/productos/guantes-racing-rojo.jpg"));
        guardar(Guantes.crear("guantes-16", dainese, new Precio(370000, "COP"), "Negro / Titanio", Talla.L, Material.CUERO, Proteccion.NIVEL_2, "/productos/guantes-dainese-negro.jpg"));
        guardar(Guantes.crear("guantes-17", motowolf, new Precio(115000, "COP"), "Negro Protección", Talla.XL, Material.TEXTIL, Proteccion.NIVEL_2, "/productos/guantes-dainese-negro.jpg"));
        guardar(Guantes.crear("guantes-18", alpinestars, new Precio(420000, "COP"), "Rojo Pista", Talla.M, Material.CUERO, Proteccion.NIVEL_3, "/productos/guantes-racing-rojo.jpg"));
        guardar(Guantes.crear("guantes-19", revit, new Precio(195000, "COP"), "Rojo / Blanco", Talla.S, Material.TEXTIL, Proteccion.NIVEL_1, "/productos/guantes-racing-rojo.jpg"));
        guardar(Guantes.crear("guantes-20", alpinestars, new Precio(1100000, "COP"), "Negro GP Pro Carbon", Talla.L, Material.KEVLAR, Proteccion.NIVEL_5, "/productos/guantes-dainese-negro.jpg"));
    }

    private void cargarBaules(Marca givi, Marca shad, Marca kappa) {
        Baul b1 = Baul.crear("baul-01", givi, Material.ABS, 37, "49x44x33", new Precio(420000, "COP"), 5.0, "Negro", "/productos/baul-givi-negro.jpg");
        b1.indicarCompatibilidad(new Compatibilidad(TipoCompatibilidad.MOTO, "Yamaha MT-03 / MT-07"));
        guardar(b1);

        Baul b2 = Baul.crear("baul-02", shad, Material.ABS, 48, "61x46x31", new Precio(780000, "COP"), 8.0, "Negro Titanio", "/productos/baul-givi-negro.jpg");
        b2.indicarCompatibilidad(new Compatibilidad(TipoCompatibilidad.MOTO, "KTM Duke 200 / 390"));
        guardar(b2);

        Baul b3 = Baul.crear("baul-03", givi, Material.ALUMINIO, 58, "55x45x32", new Precio(2200000, "COP"), 10.0, "Plata Aluminio", "/productos/baul-aluminio-aventura.jpg");
        b3.indicarCompatibilidad(new Compatibilidad(TipoCompatibilidad.MOTO, "BMW R1250GS / F850GS"));
        guardar(b3);

        guardar(Baul.crear("baul-04", kappa, Material.POLICARBONATO, 49, "50x42x34", new Precio(650000, "COP"), 7.0, "Negro Mate", "/productos/baul-givi-negro.jpg"));
        guardar(Baul.crear("baul-05", givi, Material.ALUMINIO, 45, "47x38x31", new Precio(2500000, "COP"), 10.0, "Plata Aluminio Aventura", "/productos/baul-aluminio-aventura.jpg"));
        guardar(Baul.crear("baul-06", shad, Material.ABS, 39, "51x43x32", new Precio(490000, "COP"), 6.0, "Negro Carbono", "/productos/baul-givi-negro.jpg"));
        guardar(Baul.crear("baul-07", givi, Material.ABS, 47, "59x45x32", new Precio(1150000, "COP"), 10.0, "Negro Reflector Rojo", "/productos/baul-givi-negro.jpg"));
        guardar(Baul.crear("baul-08", kappa, Material.PLASTICO, 30, "40x41x30", new Precio(260000, "COP"), 3.0, "Negro Brillante", "/productos/baul-givi-negro.jpg"));
        guardar(Baul.crear("baul-09", shad, Material.PLASTICO, 29, "38x40x30", new Precio(280000, "COP"), 3.0, "Negro", "/productos/baul-givi-negro.jpg"));
        guardar(Baul.crear("baul-10", givi, Material.ALUMINIO, 46, "46x41x34", new Precio(1950000, "COP"), 10.0, "Plata Cepillado Aventura", "/productos/baul-aluminio-aventura.jpg"));
        guardar(Baul.crear("baul-11", shad, Material.ABS, 58, "61x48x31", new Precio(1450000, "COP"), 10.0, "Negro / Carbono", "/productos/baul-givi-negro.jpg"));
        guardar(Baul.crear("baul-12", kappa, Material.ABS, 53, "59x45x32", new Precio(890000, "COP"), 10.0, "Negro Texturado", "/productos/baul-givi-negro.jpg"));
        guardar(Baul.crear("baul-13", givi, Material.ABS, 50, "56x44x34", new Precio(1600000, "COP"), 8.0, "Blanco / Negro", "/productos/baul-givi-negro.jpg"));
        guardar(Baul.crear("baul-14", givi, Material.ABS, 43, "54x45x32", new Precio(520000, "COP"), 5.0, "Negro Satinado", "/productos/baul-givi-negro.jpg"));
        guardar(Baul.crear("baul-15", shad, Material.ABS, 40, "49x42x32", new Precio(590000, "COP"), 6.0, "Negro Mate", "/productos/baul-givi-negro.jpg"));
        guardar(Baul.crear("baul-16", kappa, Material.PLASTICO, 35, "49x41x33", new Precio(340000, "COP"), 4.0, "Negro", "/productos/baul-givi-negro.jpg"));
        guardar(Baul.crear("baul-17", givi, Material.ALUMINIO, 56, "60x48x33", new Precio(1750000, "COP"), 10.0, "Plata Aluminio Trekker", "/productos/baul-aluminio-aventura.jpg"));
        guardar(Baul.crear("baul-18", shad, Material.ABS, 33, "43x42x31", new Precio(360000, "COP"), 4.0, "Negro Urbano", "/productos/baul-givi-negro.jpg"));
        guardar(Baul.crear("baul-19", shad, Material.ALUMINIO, 48, "49x42x32", new Precio(2100000, "COP"), 10.0, "Plata Terra Aventura", "/productos/baul-aluminio-aventura.jpg"));
        guardar(Baul.crear("baul-20", kappa, Material.ABS, 40, "53x41x34", new Precio(610000, "COP"), 7.0, "Negro Ahumado", "/productos/baul-givi-negro.jpg"));
    }

    private void cargarImpermeables(Marca rainwear, Marca cyclone, Marca alpinestars, Marca motowolf) {
        guardar(Impermeable.crear("imper-01", rainwear, new Precio(120000, "COP"), "Azul Rey", Talla.L, Material.PVC, "/productos/impermeable-rainwear-azul.jpg"));
        guardar(Impermeable.crear("imper-02", cyclone, new Precio(180000, "COP"), "Amarillo Flúor Alta Visibilidad", Talla.M, Material.NYLON, "/productos/impermeable-fluor.jpg"));
        guardar(Impermeable.crear("imper-03", rainwear, new Precio(240000, "COP"), "Verde Neón Reflectivo", Talla.XL, Material.POLIESTER, "/productos/impermeable-fluor.jpg"));
        guardar(Impermeable.crear("imper-04", alpinestars, new Precio(420000, "COP"), "Amarillo Flúor / Negro", Talla.L, Material.NYLON, "/productos/impermeable-fluor.jpg"));
        guardar(Impermeable.crear("imper-05", cyclone, new Precio(250000, "COP"), "Azul Oscuro Impermeable", Talla.M, Material.POLIESTER, "/productos/impermeable-rainwear-azul.jpg"));
        guardar(Impermeable.crear("imper-06", motowolf, new Precio(160000, "COP"), "Amarillo Neón Touring", Talla.S, Material.NYLON, "/productos/impermeable-fluor.jpg"));
        guardar(Impermeable.crear("imper-07", cyclone, new Precio(140000, "COP"), "Azul Marino Clásico", Talla.L, Material.PVC, "/productos/impermeable-rainwear-azul.jpg"));
        guardar(Impermeable.crear("imper-08", rainwear, new Precio(210000, "COP"), "Naranja Flúor", Talla.M, Material.POLIESTER, "/productos/impermeable-fluor.jpg"));
        guardar(Impermeable.crear("imper-09", rainwear, new Precio(95000, "COP"), "Azul Noche", Talla.XL, Material.PVC, "/productos/impermeable-rainwear-azul.jpg"));
        guardar(Impermeable.crear("imper-10", cyclone, new Precio(260000, "COP"), "Azul Marino", Talla.S, Material.NYLON, "/productos/impermeable-rainwear-azul.jpg"));
        guardar(Impermeable.crear("imper-11", rainwear, new Precio(85000, "COP"), "Amarillo Neón Ligero", Talla.XS, Material.GOMA, "/productos/impermeable-fluor.jpg"));
        guardar(Impermeable.crear("imper-12", cyclone, new Precio(195000, "COP"), "Naranja / Amarillo Flúor", Talla.L, Material.NYLON, "/productos/impermeable-fluor.jpg"));
        guardar(Impermeable.crear("imper-13", motowolf, new Precio(175000, "COP"), "Verde Neón Reflectivo", Talla.M, Material.POLIESTER, "/productos/impermeable-fluor.jpg"));
        guardar(Impermeable.crear("imper-14", alpinestars, new Precio(390000, "COP"), "Amarillo Alta Visibilidad", Talla.XL, Material.NYLON, "/productos/impermeable-fluor.jpg"));
        guardar(Impermeable.crear("imper-15", rainwear, new Precio(380000, "COP"), "Amarillo Flúor Pro", Talla.L, Material.NYLON, "/productos/impermeable-fluor.jpg"));
        guardar(Impermeable.crear("imper-16", rainwear, new Precio(110000, "COP"), "Azul Petróleo", Talla.M, Material.PVC, "/productos/impermeable-rainwear-azul.jpg"));
        guardar(Impermeable.crear("imper-17", cyclone, new Precio(220000, "COP"), "Rojo / Amarillo Flúor", Talla.L, Material.POLIESTER, "/productos/impermeable-fluor.jpg"));
        guardar(Impermeable.crear("imper-18", motowolf, new Precio(280000, "COP"), "Verde Flúor Impermeable", Talla.XL, Material.NYLON, "/productos/impermeable-fluor.jpg"));
        guardar(Impermeable.crear("imper-19", cyclone, new Precio(190000, "COP"), "Azul Eléctrico", Talla.M, Material.POLIESTER, "/productos/impermeable-rainwear-azul.jpg"));
        guardar(Impermeable.crear("imper-20", motowolf, new Precio(150000, "COP"), "Amarillo Seguridad Vial", Talla.S, Material.NYLON, "/productos/impermeable-fluor.jpg"));
    }

    private void cargarIntercomunicadores(Marca cardo, Marca sena, Marca freedconn) {
        guardar(Intercomunicador.crear("inter-01", cardo, new Precio(1350000, "COP"), "Negro Carbono", 1600, 13, true, "/productos/intercomunicador-cardo-gris.jpg"));
        guardar(Intercomunicador.crear("inter-02", sena, new Precio(1420000, "COP"), "Negro Satinado", 2000, 14, true, "/productos/intercom-sena-jogdial.jpg"));
        guardar(Intercomunicador.crear("inter-03", freedconn, new Precio(390000, "COP"), "Negro / Rojo Display", 1200, 11, true, "/productos/intercom-freedconn-lcd.jpg"));
        guardar(Intercomunicador.crear("inter-04", cardo, new Precio(790000, "COP"), "Negro Mate", 1200, 13, true, "/productos/intercomunicador-cardo-gris.jpg"));
        guardar(Intercomunicador.crear("inter-05", sena, new Precio(1390000, "COP"), "Negro Titanio", 2000, 13, true, "/productos/intercom-sena-jogdial.jpg"));
        guardar(Intercomunicador.crear("inter-06", freedconn, new Precio(460000, "COP"), "Negro / Plata", 1500, 15, true, "/productos/intercom-freedconn-lcd.jpg"));
        guardar(Intercomunicador.crear("inter-07", cardo, new Precio(420000, "COP"), "Negro Clásico", 600, 13, true, "/productos/intercomunicador-cardo-gris.jpg"));
        guardar(Intercomunicador.crear("inter-08", sena, new Precio(1150000, "COP"), "Gris Grafito", 1600, 12, true, "/productos/intercom-sena-jogdial.jpg"));
        guardar(Intercomunicador.crear("inter-09", freedconn, new Precio(520000, "COP"), "Negro Mate LCD", 1500, 16, true, "/productos/intercom-freedconn-lcd.jpg"));
        guardar(Intercomunicador.crear("inter-10", cardo, new Precio(1220000, "COP"), "Negro y Plata", 1600, 13, true, "/productos/intercomunicador-cardo-gris.jpg"));
        guardar(Intercomunicador.crear("inter-11", sena, new Precio(890000, "COP"), "Negro", 2000, 11, true, "/productos/intercom-sena-jogdial.jpg"));
        guardar(Intercomunicador.crear("inter-12", freedconn, new Precio(340000, "COP"), "Negro / Azul", 1000, 10, true, "/productos/intercom-freedconn-lcd.jpg"));
        guardar(Intercomunicador.crear("inter-13", cardo, new Precio(1180000, "COP"), "Negro Texturado", 1600, 13, true, "/productos/intercomunicador-cardo-gris.jpg"));
        guardar(Intercomunicador.crear("inter-14", sena, new Precio(590000, "COP"), "Negro Mate", 900, 12, true, "/productos/intercom-sena-jogdial.jpg"));
        guardar(Intercomunicador.crear("inter-15", freedconn, new Precio(680000, "COP"), "Negro Cámara 2K", 1000, 9, true, "/productos/intercom-freedconn-lcd.jpg"));
        guardar(Intercomunicador.crear("inter-16", cardo, new Precio(580000, "COP"), "Gris Oscuro", 800, 13, true, "/productos/intercomunicador-cardo-gris.jpg"));
        guardar(Intercomunicador.crear("inter-17", sena, new Precio(980000, "COP"), "Titanio Metálico", 2000, 13, true, "/productos/intercom-sena-jogdial.jpg"));
        guardar(Intercomunicador.crear("inter-18", freedconn, new Precio(290000, "COP"), "Negro Antichoque", 800, 12, true, "/productos/intercom-freedconn-lcd.jpg"));
        guardar(Intercomunicador.crear("inter-19", cardo, new Precio(950000, "COP"), "Negro Grafito", 1600, 13, true, "/productos/intercomunicador-cardo-gris.jpg"));
        guardar(Intercomunicador.crear("inter-20", sena, new Precio(750000, "COP"), "Negro Brillante", 900, 12, true, "/productos/intercom-sena-jogdial.jpg"));
    }

    private void cargarSoportesCelular(Marca quadlock, Marca ram, Marca motowolf) {
        String[] sujeciones = {"Manillar", "Espejo Retrovisor", "Horquilla", "Tija Central"};
        for (int i = 1; i <= 20; i++) {
            String id = String.format("soporte-%02d", i);
            Marca marca = (i % 3 == 0) ? ram : ((i % 2 == 0) ? motowolf : quadlock);
            String sujecion = sujeciones[(i - 1) % sujeciones.length];
            double precio = 65000 + (i * 12000);
            guardar(SoporteCelular.crear(id, marca, new Precio(precio, "COP"), "Negro", sujecion, "/productos/soporte-quadlock-negro.jpg"));
        }
    }

    private void cargarVisores(Marca agv, Marca ls2, Marca shoei, Marca hjc, Marca bell, Marca shark) {
        guardar(Visor.crear("visor-01", agv, new Precio(140000, "COP"), "Transparente Pinlock", "Antifog Clear 100%", List.of("K3 SV", "K1 S", "K5 S"), "/productos/visor-agv-transparente.jpg"));
        guardar(Visor.crear("visor-02", ls2, new Precio(175000, "COP"), "Iridium Azul Espejado", "Iridium Racing Blue", List.of("FF353", "Rapid", "Stream II"), "/productos/visor-iridium-azul.jpg"));
        guardar(Visor.crear("visor-03", shoei, new Precio(240000, "COP"), "Iridium Oro Espejado", "Iridium Gold Mirror", List.of("NXR 2", "X-Spirit 3", "Glamster"), "/productos/visor-iridium-oro.jpg"));
        guardar(Visor.crear("visor-04", hjc, new Precio(160000, "COP"), "Humo Oscuro 80%", "Dark Smoke UV400", List.of("RPHA 11 Pro", "i90", "CS-15"), "/productos/visor-humo-oscuro.jpg"));
        guardar(Visor.crear("visor-05", bell, new Precio(190000, "COP"), "Iridium Azul Espejado", "Iridium Deep Blue", List.of("Qualifier DLX", "Eliminator", "Custom 500"), "/productos/visor-iridium-azul.jpg"));
        guardar(Visor.crear("visor-06", shark, new Precio(185000, "COP"), "Iridium Oro Espejado", "Iridium Solar Gold", List.of("Spartan GT", "Skwal 2", "Evo-ES"), "/productos/visor-iridium-oro.jpg"));
        guardar(Visor.crear("visor-07", agv, new Precio(155000, "COP"), "Humo Oscuro 80%", "Dark Smoke Racing", List.of("Pista GP RR", "Corsa R", "K3 SV"), "/productos/visor-humo-oscuro.jpg"));
        guardar(Visor.crear("visor-08", ls2, new Precio(130000, "COP"), "Transparente Pinlock", "Clear Anti-Scratch", List.of("FF808", "FF352", "Explorer MX701"), "/productos/visor-agv-transparente.jpg"));
        guardar(Visor.crear("visor-09", shoei, new Precio(260000, "COP"), "Iridium Azul Espejado", "Spectra Blue Mirror", List.of("NXR 2", "X-Fourteen"), "/productos/visor-iridium-azul.jpg"));
        guardar(Visor.crear("visor-10", hjc, new Precio(210000, "COP"), "Iridium Oro Espejado", "Gold Mirror High Vision", List.of("RPHA 70", "RPHA 11 Pro"), "/productos/visor-iridium-oro.jpg"));
        guardar(Visor.crear("visor-11", bell, new Precio(150000, "COP"), "Humo Oscuro 80%", "Dark Smoke Tinted", List.of("Qualifier", "Star DLX"), "/productos/visor-humo-oscuro.jpg"));
        guardar(Visor.crear("visor-12", shark, new Precio(140000, "COP"), "Transparente Pinlock", "Total Vision Clear", List.of("Skwal 2", "D-Skwal 2"), "/productos/visor-agv-transparente.jpg"));
        guardar(Visor.crear("visor-13", agv, new Precio(220000, "COP"), "Iridium Azul Espejado", "Iridium Blue Race 3", List.of("Pista GP RR", "K3 SV"), "/productos/visor-iridium-azul.jpg"));
        guardar(Visor.crear("visor-14", ls2, new Precio(165000, "COP"), "Iridium Oro Espejado", "Iridium Gold Rainbow", List.of("FF800 Storm", "FF808"), "/productos/visor-iridium-oro.jpg"));
        guardar(Visor.crear("visor-15", shoei, new Precio(230000, "COP"), "Humo Oscuro 80%", "Dark Smoke CWR-F2", List.of("NXR 2", "X-Spirit 3"), "/productos/visor-humo-oscuro.jpg"));
        guardar(Visor.crear("visor-16", hjc, new Precio(145000, "COP"), "Transparente Pinlock", "Clear HJ-26 Pinlock", List.of("RPHA 11", "RPHA 70"), "/productos/visor-agv-transparente.jpg"));
        guardar(Visor.crear("visor-17", bell, new Precio(205000, "COP"), "Iridium Azul Espejado", "Blue Mirror ClickRelease", List.of("Qualifier DLX"), "/productos/visor-iridium-azul.jpg"));
        guardar(Visor.crear("visor-18", shark, new Precio(195000, "COP"), "Iridium Oro Espejado", "Gold Chrome Optical Class 1", List.of("Spartan GT"), "/productos/visor-iridium-oro.jpg"));
        guardar(Visor.crear("visor-19", agv, new Precio(175000, "COP"), "Humo Oscuro 80%", "Dark Smoke GT4", List.of("K5 S", "K3 SV", "K1 S"), "/productos/visor-humo-oscuro.jpg"));
        guardar(Visor.crear("visor-20", ls2, new Precio(135000, "COP"), "Transparente Pinlock", "Max Vision Clear", List.of("Rapid FF353", "Stream"), "/productos/visor-agv-transparente.jpg"));
    }
}
