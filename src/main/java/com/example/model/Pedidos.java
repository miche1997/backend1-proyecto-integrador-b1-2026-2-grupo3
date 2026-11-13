package com.example.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedidos")
public class Pedidos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;
    private int numeroPedido;
    private LocalDate fechaPedido;

    // Relación N:1 -> un usuario puede hacer muchos pedidos
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Relación N:1 -> un producto puede aparecer en muchos pedidos
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // Relación N:1 (opcional) -> un cupón puede aplicarse en muchos pedidos
    @ManyToOne
    @JoinColumn(name = "cupon_id")
    private Cupones cupon;

    // Constructor vacío (obligatorio para JPA)
    public Pedidos() {
        this.fechaPedido = LocalDate.now();
    }

    public Pedidos(Long idPedido, int numeroPedido, LocalDate fechaPedido, Usuario usuario, Producto producto, Cupones cupon) {
        this.idPedido = idPedido;
        this.numeroPedido = numeroPedido;
        this.fechaPedido = fechaPedido;
        this.usuario = usuario;
        this.producto = producto;
        this.cupon = cupon;
    }

    public Pedidos(int numeroPedido, LocalDate fechaPedido, Usuario usuario, Producto producto, Cupones cupon) {
        this(null, numeroPedido, fechaPedido, usuario, producto, cupon);
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public int getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(int numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public LocalDate getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDate fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Cupones getCupon() {
        return cupon;
    }

    public void setCupon(Cupones cupon) {
        this.cupon = cupon;
    }

    @Override
    public String toString() {
        return "Pedidos [idPedido=" + idPedido + ", numeroPedido=" + numeroPedido + ", fechaPedido=" + fechaPedido
                + ", usuario=" + (usuario != null ? usuario.getId() : null)
                + ", producto=" + (producto != null ? producto.getId() : null)
                + ", cupon=" + (cupon != null ? cupon.getId() : null) + "]";
    }
}
