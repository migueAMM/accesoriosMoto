-- CASCOS
INSERT INTO productos (id, tipo_producto, marca_id, marca_nombre, precio_monto, precio_moneda, color, estado_publicacion, imagen_url,
                       casco_modelo, casco_talla, casco_proteccion, casco_certificacion_seguridad)
SELECT 'seed-casco-1', 'CASCO', 'm-agv', 'AGV', 850000, 'COP', 'Negro', 'PUBLICADO', '/productos/casco-agv-k3-negro.jpg', 'K3', 'M', 'NIVEL_3', 'DOT'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM productos WHERE id = 'seed-casco-1');

INSERT INTO productos (id, tipo_producto, marca_id, marca_nombre, precio_monto, precio_moneda, color, estado_publicacion, imagen_url,
                       casco_modelo, casco_talla, casco_proteccion, casco_certificacion_seguridad)
SELECT 'seed-casco-2', 'CASCO', 'm-ls2', 'LS2', 520000, 'COP', 'Blanco', 'PUBLICADO', '/productos/casco-ls2-blanco.jpg', 'FF806', 'L', 'NIVEL_2', 'ECE 22.06'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM productos WHERE id = 'seed-casco-2');

-- CHAQUETA
INSERT INTO productos (id, tipo_producto, marca_id, marca_nombre, precio_monto, precio_moneda, color, estado_publicacion, imagen_url,
                       chaqueta_talla, chaqueta_material, chaqueta_proteccion)
SELECT 'seed-chaqueta-1', 'CHAQUETA', 'm-alpine', 'Alpinestars', 780000, 'COP', 'Negro', 'PUBLICADO', '/productos/chaqueta-alpinestars-cuero.jpg', 'L', 'CUERO', 'NIVEL_3'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM productos WHERE id = 'seed-chaqueta-1');

-- GUANTES
INSERT INTO productos (id, tipo_producto, marca_id, marca_nombre, precio_monto, precio_moneda, color, estado_publicacion, imagen_url,
                       guantes_talla, guantes_material, guantes_proteccion)
SELECT 'seed-guantes-1', 'GUANTES', 'm-dainese', 'Dainese', 240000, 'COP', 'Negro', 'PUBLICADO', '/productos/guantes-dainese-negro.jpg', 'M', 'TEXTIL', 'NIVEL_2'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM productos WHERE id = 'seed-guantes-1');

-- IMPERMEABLE
INSERT INTO productos (id, tipo_producto, marca_id, marca_nombre, precio_monto, precio_moneda, color, estado_publicacion, imagen_url,
                       impermeable_talla, impermeable_material)
SELECT 'seed-impermeable-1', 'IMPERMEABLE', 'm-rain', 'Rainwear', 90000, 'COP', 'Azul', 'PUBLICADO', '/productos/impermeable-rainwear-azul.jpg', 'XL', 'PVC'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM productos WHERE id = 'seed-impermeable-1');

-- INTERCOMUNICADOR
INSERT INTO productos (id, tipo_producto, marca_id, marca_nombre, precio_monto, precio_moneda, color, estado_publicacion, imagen_url,
                       intercomunicador_alcance_metros, intercomunicador_autonomia_horas, intercomunicador_bluetooth)
SELECT 'seed-intercom-1', 'INTERCOMUNICADOR', 'm-cardo', 'Cardo', 450000, 'COP', 'Gris', 'PUBLICADO', '/productos/intercomunicador-cardo-gris.jpg', 1000, 12, TRUE
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM productos WHERE id = 'seed-intercom-1');

-- SOPORTE CELULAR
INSERT INTO productos (id, tipo_producto, marca_id, marca_nombre, precio_monto, precio_moneda, color, estado_publicacion, imagen_url,
                       soportecelular_tipo_sujecion)
SELECT 'seed-soporte-1', 'SOPORTE_CELULAR', 'm-quad', 'Quad Lock', 60000, 'COP', 'Negro', 'PUBLICADO', '/productos/soporte-quadlock-negro.jpg', 'Manillar'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM productos WHERE id = 'seed-soporte-1');

-- BAÚL (+ compatibilidad con moto)
INSERT INTO productos (id, tipo_producto, marca_id, marca_nombre, precio_monto, precio_moneda, color, estado_publicacion, imagen_url,
                       baul_material, baul_capacidad_litros, baul_dimensiones, baul_capacidad_maxima_carga)
SELECT 'seed-baul-1', 'BAUL', 'm-givi', 'Givi', 380000, 'COP', 'Negro', 'PUBLICADO', '/productos/baul-givi-negro.jpg', 'ABS', 45, '50x40x30', 8
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM productos WHERE id = 'seed-baul-1');

INSERT INTO baul_compatibilidades (producto_id, tipo, modelo)
SELECT 'seed-baul-1', 'MOTO', 'MT-07'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM baul_compatibilidades WHERE producto_id = 'seed-baul-1' AND modelo = 'MT-07');

-- VISOR (+ compatibilidad con casco)
INSERT INTO productos (id, tipo_producto, marca_id, marca_nombre, precio_monto, precio_moneda, color, estado_publicacion, imagen_url,
                       visor_tipo_visor)
SELECT 'seed-visor-1', 'VISOR', 'm-agv', 'AGV', 150000, 'COP', 'Transparente', 'PUBLICADO', '/productos/visor-agv-transparente.jpg', 'Solar'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM productos WHERE id = 'seed-visor-1');

INSERT INTO visor_compatibilidades (producto_id, tipo, modelo)
SELECT 'seed-visor-1', 'CASCO', 'K3'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM visor_compatibilidades WHERE producto_id = 'seed-visor-1' AND modelo = 'K3');
