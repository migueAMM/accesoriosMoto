# accesoriosMoto

Este proyecto corresponde a una aplicación para la gestión y venta de accesorios para moto.
El catalogo incluye cascos, baúles, chaquetas, guantes, visores, impermeables, intercomunicadores y soportes para celular. 
Los compradores pueden consultar productos, realizar pedidos, comentar compras completadas y solicitar reembolsos. 
el administrador gestiona el catálogo y completa pedidos.

## Integrantes
- Miguel Angel Marin Montes.
- Juan Jose Suarez Moncaleano
- Diana Maria Garcia Alzate

## Cómo ejecutar el proyecto

Necesitamos Java 21. Gradle viene incluido en el proyecto.

Backend: http://localhost:8080. No hay un puerto personalizado en `application.properties`; se usa el puerto 8080 de Spring Boot.

## Configuración

`JWT_SECRET` es la clave de firma del JWT. Los tokens duran 30 minutos y contienen `sub` (ID del usuario), `correo`, `rol`, `iat`, `exp` e `iss`; no incluyen contraseñas ni hashes.

No hay otras variables obligatorias con la configuración predeterminada. Si se activa `ADMIN_INICIAL_ENABLED=true`, también deben definirse `ADMIN_INICIAL_NOMBRE`, `ADMIN_INICIAL_CORREO` y `ADMIN_INICIAL_CONTRASENA`. Ese administrador adicional usa los datos configurados en el entorno.

## Usuarios de prueba

`CargadorDatosPrueba` está activo por defecto (`DATOS_PRUEBA_ENABLED=true`) y crea estos usuarios si sus correos todavía no existen:

| Usuario | Correo | Contraseña de prueba | Rol |
|---|---|---|---|
| Administrador | admin@moto.com | admin123 | ADMINISTRADOR |
| Comprador | comprador@moto.com | comprador123 | COMPRADOR |

Son cuentas de demostración definidas en el código.

## Swagger

Abrir http://localhost:8080/swagger-ui/index.html. Primero ejecutar `POST /api/auth/login`, copiar el campo `token` y pegarlo en **Authorize** del esquema `bearer-jwt`. Se pega solo el JWT, sin escribir otra vez `Bearer`; Swagger agrega el encabezado `Authorization: Bearer <token>`.

## Endpoints principales

Las rutas públicas no requieren token. Las demás necesitan el JWT del rol indicado. En pedidos y comentarios, el comprador solo puede operar sobre sus propios datos.

### Autenticación

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| POST | /api/auth/registro | Registrar un comprador | Público |
| POST | /api/auth/login | Iniciar sesión y obtener JWT | Público |

### Productos

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| GET | /api/productos | Catálogo publicado con filtros y paginación | Público |
| GET | /api/productos/buscar | Buscar por texto, precio, orden y página | Público |
| GET | /api/productos/{id} | Consultar un producto publicado | Público |
| GET | /api/productos/mas-vendidos | Ranking por unidades vendidas | Público |
| POST | /api/productos/cascos | Registrar casco | ADMINISTRADOR |
| POST | /api/productos/baules | Registrar baúl | ADMINISTRADOR |
| POST | /api/productos/chaquetas | Registrar chaqueta | ADMINISTRADOR |
| POST | /api/productos/guantes | Registrar guantes | ADMINISTRADOR |
| POST | /api/productos/visores | Registrar visor | ADMINISTRADOR |
| POST | /api/productos/impermeables | Registrar impermeable | ADMINISTRADOR |
| POST | /api/productos/intercomunicadores | Registrar intercomunicador | ADMINISTRADOR |
| POST | /api/productos/soportes-celular | Registrar soporte celular | ADMINISTRADOR |
| PUT | /api/productos/{id}/publicar | Poner un producto a la venta | ADMINISTRADOR |
| PUT | /api/productos/{id}/despublicar | Retirarlo de la venta sin borrarlo | ADMINISTRADOR |
| PUT | /api/productos/{id}/compatibilidades | Indicar compatibilidad de baúl o visor | ADMINISTRADOR |
| DELETE | /api/productos/{id}/compatibilidades | Retirar compatibilidad mediante tipo y modelo | ADMINISTRADOR |
| DELETE | /api/productos/{id} | Eliminar producto | ADMINISTRADOR |

En `/buscar`, los parámetros son `texto`, `precioMinimo`, `precioMaximo`, `moneda`, `ordenarPor`, `direccion`, `pagina` y `tamano`. El ranking recibe `limite` (10 por defecto, máximo 20).

### Pedidos

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| POST | /api/pedidos | Registrar un pedido propio | COMPRADOR |
| GET | /api/pedidos | Listar los pedidos del usuario autenticado | COMPRADOR |
| PUT | /api/pedidos/{id}/completar | Completar un pedido | ADMINISTRADOR |
| PUT | /api/pedidos/{id}/reembolso | Solicitar reembolso de un pedido propio | COMPRADOR |

### Comentarios

| Método | Ruta | Descripción | Acceso |
|---|---|---|---|
| POST | /api/comentarios | Calificar y comentar un producto comprado | COMPRADOR |

## Base de datos

Usamos H2 en archivo, con URL JDBC `jdbc:h2:file:./data/accesoriosMoto;DB_CLOSE_ON_EXIT=FALSE`, usuario `sa` y contraseña vacía. Los datos quedan en `data/` y se conservan al reiniciar. Hibernate actualiza el esquema con `ddl-auto=update`.

Para acceder a la consola, arrancar con el perfil `dev`:

```powershell
.\gradlew bootRun --args="--spring.profiles.active=dev"
```

Abrir http://localhost:8080/h2-console y usar la URL JDBC anterior. Aunque la consola está habilitada en las propiedades generales, la seguridad permite abrirla únicamente con `dev`. `data.sql` agrega productos de forma idempotente; el cargador Java crea usuarios y agrega su catálogo solo si no hay productos.


## Pruebas

Para ejecutar todas las pruebas:

```powershell
.\gradlew clean test
```


## Seguimiento de pruebas avanzadas

### 0. Pruebas del repositorio

En esta parte quisimos comprobar que la información realmente
se guardara y se recuperara bien desde H2.
Para eso usamos un casco con datos conocidos y luego verificamos
que valores como la marca, el precio, la moneda y el estado siguieran
iguales al recuperarlo. También hicimos un ranking de productos
más vendidos para comprobar una consulta con agrupación y orden.
Preparamos datos con resultados conocidos para poder revisar
fácilmente si el orden era correcto.


### 1. Endpoint con seguridad

El error 401 aparece cuando una persona intenta entrar a una
ruta protegida sin estar autenticada, y el 403 aparece
cuando sí está autenticada, pero no tiene el rol necesario.
Para probarlo usamos el endpoint que permite publicar productos,
que solo puede usar un administrador. Simulamos los roles con `jwt()`.


### 2. @DataJpaTest vs @SpringBootTest

`@DataJpaTest` se enfoca principalmente en la
parte de persistencia, por eso resulta útil para probar
repositorios y entidades de una forma más rápida. En cambio,
`@SpringBootTest` carga prácticamente toda la aplicación y sirve
cuando queremos comprobar varias capas trabajando juntas.
En nuestro caso tuvimos que importar algunos adapters y mappers
porque `@DataJpaTest` no los carga automáticamente.


### 3. Prueba de integración

La prueba de integración sirvió para comprobar que varias
partes del proyecto funcionaran juntas y no solamente de forma
aislada. La petición pasa por el Controller,
luego por el caso de uso, el repositorio y finalmente llega a H2.
Probamos un flujo donde se registra un producto, se publica y
luego se consulta. A diferencia de una prueba unitaria, aquí no
simulamos las capas principales porque queríamos comprobar el
recorrido real de la información.
