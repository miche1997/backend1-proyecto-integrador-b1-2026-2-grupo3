package com.example.model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
@Entity
@Table(name = "proveedores")
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProveedor;
    private String nombreCompleto;
    private String telefono;
    private String contrasena;
    private LocalDateTime fechaCreacionCuenta;
    @Transient // aún no se guarda en la base de datos
    private List<Producto> listaProductos = new ArrayList<>();

    // constructor completo
    public Proveedor(Long idProveedor, String contrasena, String nombreCompleto, String telefono,
            LocalDateTime fechaCreacionCuenta) {
        this.idProveedor = idProveedor;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.fechaCreacionCuenta = fechaCreacionCuenta;
    }

    // constructor sin id
    public Proveedor(String contrasena, String nombreCompleto, String telefono, LocalDateTime fechaCreacionCuenta) {
        this(null, contrasena, nombreCompleto, telefono, fechaCreacionCuenta);
    }

    // constructor vacío
    public Proveedor() {
        this.fechaCreacionCuenta = LocalDateTime.now();
    }

    // getters y setters
    public Long getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Long idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDateTime getFechaCreacionCuenta() {
        return fechaCreacionCuenta;
    }

    public void setFechaCreacionCuenta(LocalDateTime fechaCreacionCuenta) {
        this.fechaCreacionCuenta = fechaCreacionCuenta;
    }

    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    @Override
    public String toString() {
        return "Proveedor [idProveedor=" + idProveedor + ", nombreCompleto=" + nombreCompleto + ", telefono="
                + telefono + "]";
    }
public boolean agregarProducto(Producto producto) {
        if (producto == null) {
            return false;
        }
        //producto.setIdProveedor(this.idProveedor); 
        return this.listaProductos.add(producto);
    }

    public boolean eliminarProducto(String idProducto) {
        if (idProducto == null) {
            return false;
        }
        return this.listaProductos.removeIf (p -> idProducto.equalsIgnoreCase(p.getCodigo()));
    }

}