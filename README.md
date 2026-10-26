# accesoriosMoto

Este proyecto corresponde al backend de una aplicación para la gestión y venta de accesorios para moto. 

Para ejecutar todas las pruebas desde la raíz del proyecto se usa:

```bash
./gradlew test
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
parte de persistenciam por eso resulta útil para probar 
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

