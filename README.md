# accesoriosMoto

Backend del catálogo de accesorios para moto. Las pruebas usan H2 en memoria y una clave JWT exclusiva de tests.
Para ejecutar la suite desde la raíz:


## Seguimiento de pruebas avanzadas

La Parte 0 está en `ProductoRepositoryIntegrationTest`: guardar/recuperar un casco y consultar el ranking real en H2.
El ranking suma unidades de pedidos `COMPLETADA`, estado que asigna `Pedido.completar()`; excluye los demás estados.
Los datos conocidos producen A=5, B=3 y C=1; la consulta ordena por unidades DESC e ID ASC para desempatar.
La Parte 4 está en `ProductoControllerTest` y `PublicarProductoUseCaseTest`: 404, regla rota con 400 y verificaciones `never()`.

### 1. Endpoint con seguridad
Un 401 significa que no hay una autenticación válida para entrar a la ruta protegida.
Un 403 significa que el usuario está autenticado, pero no tiene el rol necesario.
En `SeguridadProductoEndpointTest` probamos `PUT /api/productos/{id}/publicar` y esperamos 200 solo para administrador.
Usamos `@SpringBootTest` y MockMvc para cargar nuestra configuración completa, incluidos los filtros de seguridad.
Con `jwt()` simulamos un usuario ya autenticado; aquí no hacemos login porque ese flujo tiene otras pruebas.
Las authorities son exactamente `ROLE_COMPRADOR` y `ROLE_ADMINISTRADOR`, como en nuestro SecurityConfig.
Lo más delicado fue separar permisos de reglas del producto: el caso 200 necesita un producto existente y todavía no publicado.

### 2. @DataJpaTest vs @SpringBootTest
`@DataJpaTest` es un slice: carga entidades y repositorios JPA, sin levantar toda la capa web.
`@SpringBootTest` carga la aplicación completa; sirve para comprobar cómo se conectan las capas.
El slice tiene menor alcance y normalmente arranca más rápido; reutilizamos `RepositoriosJpaTest`.
Sus transacciones se revierten por defecto; en el test completo declaramos `@Transactional` para obtener rollback.
Lo más delicado fue incluir con `@Import` nuestros adapters y mappers, que el slice no descubre automáticamente.
En Spring Boot 4 el import es `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`.
Usamos `spring-boot-starter-data-jpa-test`, que ya estaba declarado en el proyecto.
`@AutoConfigureTestDatabase` controla el reemplazo del datasource (por defecto NON_TEST); comprobamos H2 en memoria, nunca el archivo de desarrollo.

### 3. Prueba de integración
Una prueba unitaria aísla una clase; una integración comprueba varias piezas trabajando juntas.
Nuestro recorrido es Controller → UseCase → Repository → H2, con los DTOs y mappers reales.
Reutilizamos `ProductoHttpH2IntegrationTest` con MockMvc para registrar un casco, publicarlo y consultar el catálogo.
No simulamos controller, caso de uso ni persistencia; `jwt()` solo prepara la identidad autorizada.
La ventaja es detectar fallos de conexión entre capas y de mapeo que una prueba unitaria puede ocultar.
El costo es levantar más componentes y una base de datos, por lo que tarda más que un test aislado.
Lo más delicado fue hacer flush/clear para leer desde H2 y usar MockMvc para que `@Transactional` revierta las escrituras del flujo.

