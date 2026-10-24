
package com.example.model;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
@Entity
@Table(name = "administradores")
public class Administrador {
    @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String usuarioAdministrador;
    private String contrasena;
    private String estado; 
    private LocalDateTime fechaRegistro;

      // constructor completo
    public Administrador(long id, String usuarioAdministrador, String contrasena, String estado, LocalDateTime fechaRegistro) {
        this.id = id;
        this.usuarioAdministrador = usuarioAdministrador;
        this.contrasena = contrasena;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    // constructor sin id
    public Administrador(String usuarioAdministrador, String contrasena, String estado, LocalDateTime fechaRegistro) {
    this(0, usuarioAdministrador, contrasena, estado, fechaRegistro);
    }
    // constructor vacío
    public Administrador() {
        this.fechaRegistro = LocalDateTime.now();
        this.estado = "activo"; // por defecto
    }

    // getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsuarioAdministrador() {
        return usuarioAdministrador;
    }
    

    public void setUsuarioAdministrador(String usuarioAdministrador) {
        this.usuarioAdministrador = usuarioAdministrador;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    @Override
    public String toString() {
        return "Administrador [id=" + id + ", usuarioAdministrador=" + usuarioAdministrador + ", contrasena=" + contrasena + ", estado=" + estado + ", fechaRegistro=" + fechaRegistro + "]";
    }
}

