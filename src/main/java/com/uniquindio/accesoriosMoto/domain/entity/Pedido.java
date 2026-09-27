package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.EstadoPedido;
import com.uniquindio.accesoriosMoto.domain.valueObject.Precio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Pedido {

    private final String id;
    private Comprador comprador;
    private List<Producto> productos;
    private EstadoPedido estadoPedido;
    private LocalDateTime fecha;
    private Precio total;

    public Pedido(String id, Comprador comprador, List<Producto> productos, EstadoPedido estadoPedido, LocalDateTime fecha, Precio total) {
        this.id = id;
        this.comprador = comprador;
        this.productos = productos;
        this.estadoPedido = estadoPedido;
        this.fecha = fecha;
        this.total = total;
    }

    public void solicitarReembolso(String motivo){
        if (this.estadoPedido != EstadoPedido.COMPLETADA){
            throw new ReglaDominioException("Solo se puede solicitar reembolso de un pedido completado");
        }
        this.estadoPedido = EstadoPedido.REEMBOLSADA;
    }

    public EstadoPedido getEstadoPedido(){
        return estadoPedido;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido)) return false;
        return id.equals(((Pedido) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public void validarParaRegistro() {
        if (this.productos == null || this.productos.isEmpty()) {
            throw new ReglaDominioException("Un pedido no puede registrarse sin productos");
        }
        if (this.total == null || this.total.esNegativoOCero()) {
            throw new ReglaDominioException("Un pedido debe tener un total válido");
        }
    }

    public static Pedido registrar(String id, Comprador comprador, List<Producto> productos, LocalDateTime fecha, Precio total){
        Pedido pedido = new Pedido(id, comprador, productos, EstadoPedido.PENDIENTE, fecha, total);
        pedido.validarParaRegistro();
        return pedido;
    }

    public String getId() {
        return id;
    }

    public Comprador getComprador() {
        return comprador;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Precio getTotal() {
        return total;
    }
}
