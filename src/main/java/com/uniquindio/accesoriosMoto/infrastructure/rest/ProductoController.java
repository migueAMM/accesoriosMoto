package com.uniquindio.accesoriosMoto.infrastructure.rest;

import com.uniquindio.accesoriosMoto.application.dto.request.*;
import com.uniquindio.accesoriosMoto.application.dto.response.PageResponse;
import com.uniquindio.accesoriosMoto.application.dto.response.ProductoResponse;
import com.uniquindio.accesoriosMoto.application.dto.response.ProductoVentasResponse;
import com.uniquindio.accesoriosMoto.application.usecase.*;
import com.uniquindio.accesoriosMoto.domain.entity.Producto;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;
import com.uniquindio.accesoriosMoto.infrastructure.rest.mapper.ProductoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final RegistrarBaulUseCase registrarBaulUseCase;
    private final RegistrarCascoUseCase registrarCascoUseCase;
    private final RegistrarChaquetaUseCase registrarChaquetaUseCase;
    private final RegistrarGuantesUseCase registrarGuantesUseCase;
    private final RegistrarImpermeableUseCase registrarImpermeableUseCase;
    private final RegistrarIntercomunicadorUseCase registrarIntercomunicadorUseCase;
    private final RegistrarSoporteCelularUseCase registrarSoporteCelularUseCase;
    private final RegistrarVisorUseCase registrarVisorUseCase;
    private final PublicarProductoUseCase publicarProductoUseCase;
    private final DespublicarProductoUseCase despublicarProductoUseCase;
    private final GestionarCompatibilidadUseCase gestionarCompatibilidadUseCase;
    private final BuscarProductoUseCase buscarProductoUseCase;
    private final ProductoMapper mapper;
    private final EliminarProductoUseCase eliminarProductoUseCase;

    @PostMapping("/baules")
    public ResponseEntity<ProductoResponse> registrarBaul(@Valid @RequestBody RegistrarBaulRequest request) {
        Producto producto = registrarBaulUseCase.ejecutar(
                UUID.randomUUID().toString(),
                new Marca(request.marcaId(), request.marcaNombre()), request.material(), request.capacidadLitros(),
                request.dimensiones(), new Precio(request.precioMonto(), request.precioMoneda()), request.capacidadMaximaCarga(), request.color());
        return creado(producto);
    }

    @PostMapping("/cascos")
    public ResponseEntity<ProductoResponse> registrarCasco(@Valid @RequestBody RegistrarCascoRequest request) {
        Producto producto = registrarCascoUseCase.ejecutar(
                UUID.randomUUID().toString(),
                new Marca(request.marcaId(), request.marcaNombre()), request.modelo(), request.color(),
                request.talla(), new Precio(request.precioMonto(), request.precioMoneda()), request.proteccion(), request.certificacionSeguridad());
        return creado(producto);
    }

    @PostMapping("/chaquetas")
    public ResponseEntity<ProductoResponse> registrarChaqueta(@Valid @RequestBody RegistrarChaquetaRequest request) {
        Producto producto = registrarChaquetaUseCase.ejecutar(
                UUID.randomUUID().toString(),
                new Marca(request.marcaId(), request.marcaNombre()), new Precio(request.precioMonto(), request.precioMoneda()), request.color(), request.talla(), request.material(), request.proteccion());
        return creado(producto);
    }

    @PostMapping("/guantes")
    public ResponseEntity<ProductoResponse> registrarGuantes(@Valid @RequestBody RegistrarGuantesRequest request) {
        Producto producto = registrarGuantesUseCase.ejecutar(
                UUID.randomUUID().toString(),
                new Marca(request.marcaId(), request.marcaNombre()), new Precio(request.precioMonto(), request.precioMoneda()), request.color(), request.talla(), request.material(), request.proteccion());
        return creado(producto);
    }

    @PostMapping("/impermeables")
    public ResponseEntity<ProductoResponse> registrarImpermeable(@Valid @RequestBody RegistrarImpermeableRequest request) {
        Producto producto = registrarImpermeableUseCase.ejecutar(
                UUID.randomUUID().toString(),
                new Marca(request.marcaId(), request.marcaNombre()), new Precio(request.precioMonto(), request.precioMoneda()), request.color(), request.talla(), request.material());
        return creado(producto);
    }

    @PostMapping("/intercomunicadores")
    public ResponseEntity<ProductoResponse> registrarIntercomunicador(@Valid @RequestBody RegistrarIntercomunicadorRequest request) {
        Producto producto = registrarIntercomunicadorUseCase.ejecutar(
                UUID.randomUUID().toString(),
                new Marca(request.marcaId(), request.marcaNombre()), new Precio(request.precioMonto(), request.precioMoneda()), request.color(), request.alcanceMetros(), request.autonomiaHoras(),
                request.bluetooth());
        return creado(producto);
    }

    @PostMapping("/soportes-celular")
    public ResponseEntity<ProductoResponse> registrarSoporteCelular(@Valid @RequestBody RegistrarSoporteCelularRequest request) {
        Producto producto = registrarSoporteCelularUseCase.ejecutar(
                UUID.randomUUID().toString(),
                new Marca(request.marcaId(), request.marcaNombre()), new Precio(request.precioMonto(), request.precioMoneda()), request.color(), request.tipoSujecion());
        return creado(producto);
    }

    @PostMapping("/visores")
    public ResponseEntity<ProductoResponse> registrarVisor(@Valid @RequestBody RegistrarVisorRequest request) {
        Producto producto = registrarVisorUseCase.ejecutar(
                UUID.randomUUID().toString(),
                new Marca(request.marcaId(), request.marcaNombre()), new Precio(request.precioMonto(), request.precioMoneda()), request.color(), request.tipoVisor(),
                request.modelosCascoCompatibles());
        return creado(producto);
    }

    @PutMapping("/{id}/publicar")
    public ResponseEntity<ProductoResponse> publicar(@PathVariable("id") String id) {
        return ResponseEntity.ok(mapper.toResponse(publicarProductoUseCase.ejecutar(id)));
    }

    @PutMapping("/{id}/despublicar")
    public ResponseEntity<ProductoResponse> despublicar(@PathVariable("id") String id) {
        return ResponseEntity.ok(mapper.toResponse(despublicarProductoUseCase.ejecutar(id)));
    }

    @PutMapping("/{id}/compatibilidades")
    public ResponseEntity<ProductoResponse> indicarCompatibilidad(
            @PathVariable("id") String id, @Valid @RequestBody GestionarCompatibilidadRequest request) {
        Producto producto = gestionarCompatibilidadUseCase.indicar(id, request.tipo(), request.modelo());
        return ResponseEntity.ok(mapper.toResponse(producto));
    }

    @DeleteMapping("/{id}/compatibilidades")
    public ResponseEntity<ProductoResponse> retirarCompatibilidad(
            @PathVariable("id") String id,
            @RequestParam("tipo") TipoCompatibilidad tipo,
            @RequestParam("modelo") String modelo) {
        Producto producto = gestionarCompatibilidadUseCase.retirar(id, tipo, modelo);
        return ResponseEntity.ok(mapper.toResponse(producto));
    }

    @GetMapping
    public PageResponse<ProductoResponse> buscar(
            @RequestParam(name = "marca", required = false) String marca,
            @RequestParam(name = "color", required = false) String color,
            @RequestParam(name = "precioMinimo", required = false) Double precioMinimo,
            @RequestParam(name = "precioMaximo", required = false) Double precioMaximo,
            @RequestParam(name = "moneda", required = false) String moneda,
            @RequestParam(name = "tipoCompatibilidad", required = false) TipoCompatibilidad tipoCompatibilidad,
            @RequestParam(name = "modeloCompatibilidad", required = false) String modeloCompatibilidad,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina,
            @RequestParam(name = "tamano", defaultValue = "5") int tamano) {
        Compatibilidad compatibilidad = tipoCompatibilidad == null && modeloCompatibilidad == null
                ? null : new Compatibilidad(tipoCompatibilidad, modeloCompatibilidad);
        FiltroProducto filtro = new FiltroProducto(marca, color, precioMinimo, precioMaximo, moneda, compatibilidad);
        return PageResponse.desde(buscarProductoUseCase.buscar(filtro, pagina, tamano), ProductoResponse::desde);
    }

    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable("id") String id) {
        return ProductoResponse.desde(buscarProductoUseCase.obtener(id));
    }

    @GetMapping("/mas-vendidos")
    public List<ProductoVentasResponse> masVendidos(
            @RequestParam(name = "limite", defaultValue = "10") int limite) {
        return buscarProductoUseCase.masVendidos(limite).stream().map(ProductoVentasResponse::desde).toList();
    }
    private ResponseEntity<ProductoResponse> creado(Producto producto) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/api/productos").replaceQuery(null)
                .path("/{id}").buildAndExpand(producto.getId()).toUri();
        return ResponseEntity.created(location).body(mapper.toResponse(producto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") String id){
        eliminarProductoUseCase.eliminar(id);
        return ResponseEntity.ok().build();

    }
//Paginacion
@GetMapping("/buscar")
public PageResponse<ProductoResponse> buscarPorTexto(
        @RequestParam(name = "texto", required = false) String texto,
        @RequestParam(name = "precioMinimo", required = false) Double precioMinimo,
        @RequestParam(name = "precioMaximo", required = false) Double precioMaximo,
        @RequestParam(name = "moneda", required = false) String moneda,
        @RequestParam(name = "ordenarPor", defaultValue = "id") String ordenarPor,
        @RequestParam(name = "direccion", defaultValue = "asc") String direccion,
        @RequestParam(name = "pagina", defaultValue = "0") int pagina,
        @RequestParam(name = "tamano", defaultValue = "5") int tamano) {
    var busqueda = new BusquedaProducto(texto, precioMinimo, precioMaximo, moneda,
            ordenarPor, !"desc".equalsIgnoreCase(direccion));
    return PageResponse.desde(buscarProductoUseCase.buscarPorTexto(busqueda, pagina, tamano), ProductoResponse::desde);
}
}
