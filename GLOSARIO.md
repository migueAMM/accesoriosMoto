Miguel Angel Marin Montes - Diana Maria Garcia Alzate - Juan Jose Suarez

# Glosario del Lenguaje Ubicuo - Accesorios Moto

## Conceptos Centrales

### Marca.
*Definición:* Nombre comercial que identifica al fabricante o proveedor de un producto.

*Sinónimos aceptados:* comercializador.
*No usar:* Firma.

*Precondiciones:* No aplica

*Ejemplo de uso en código:*
\\\`java
accesorio.setMarca("Givi")
\\\`

---

### Talla.
*Definición:* Medida utilizada para determinar el tamaño de un producto o accesorio.

*Sinónimos aceptados:* Tamaño, medida.
*No usar:* Calibre, numero.

*Precondiciones:* El producto debe pertenecer a una categoría de indumentaria o equipamiento personal.

*Ejemplo de uso en código:*
\\\`java
chaqueta.setTalla("XL");
\\\`

---

### Protección.
*Definición:* Prenda o accesorio con un nivel de blindaje que la hace resistente a caidas, raspones. Para evitar lesiones fisicas o daños a la moto.

*Sinónimos aceptados:* Blindaje.
*No usar:* Armadura.

*Precondiciones:* No aplica.
*Ejemplo de uso en código:*
\\\`java
chaqueta.agregarProteccion(new Proteccion("Espalda", NivelCertificacion.CE_NIVEL_2));
\\\`

---

### Casco.
*Definición:* Elemento de protección personal obligatorio diseñado para proteger la cabeza del piloto ante impactos.

*Sinónimos aceptados:* Casco protector.
*No usar:* Capucha.

*Precondiciones:* se debe contar con una certificación válida.


*Ejemplo de uso en código:*
\\\`java
Casco casco = new Casco("LS2 FF806", Certificacion.ECE_2206);
\\\`

---

### Baul.
*Definición:* Contenedor rígido que sirve para almacenar o transportar carga o guardar elementos.

*Sinónimos aceptados:* Maletero, top case.
*No usar:* morral, cajon, canasta.

*Precondiciones:* Debe especificar la capacidad en litros y el material de fabricación.

*Ejemplo de uso en código:*
\\\`java
Baul baul = new Baul("Givi ", 58); // 58 L
baul.setRequiereBaseAnclaje(true);
\\\`

---

### Material.
*Definición:* Conjunto de sustancias usadas para la fabricación de un producto, como policarbonato, cuero, fibra.

*Sinónimos aceptados:* Material de fabricación.
*No usar:* Componente.

*Precondiciones:* No aplica

*Ejemplo de uso en código:*
\\\`java
casco.setMaterial("Policarbonato")
\\\`

---


## Anti-patrones (Términos a EVITAR en nuestro proyecto)

| No usar | Usar |
|---|---|
| Objeto | Producto |
| Cosa | Producto |
| Articulo | Producto |
| Usuario | Cliente |
| Tipo de producto | Categoria |
| Compra | Pedido |
| Valor | Precio |
| Forma de pago | Método de pago |
| Nivel de seguridad | Certificado |
