package com.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import org.atmosphere.interceptor.AtmosphereResourceStateRecovery.B;
import org.jetbrains.kotlin.it.unimi.dsi.fastutil.objects.t;

import com.example.model.Administrador;
import com.example.model.Categoria;
import com.example.model.Cupones;
import com.example.model.Producto;
import com.example.model.Proveedor;
import com.example.model.Usuario;
import com.example.service.CrudService;
import com.example.service.PdfReportService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@PageTitle("Gestión CRUD - Sistema de Inventario")
@Route("")
public class MainView extends VerticalLayout {

    private final CrudService crudService;
    private final PdfReportService pdfReportService;

    public MainView(CrudService crudService, PdfReportService pdfReportService) {
        this.crudService = crudService;
        this.pdfReportService = pdfReportService;
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        TabSheet tabs = new TabSheet();
        tabs.setWidthFull();
        tabs.add("Productos", crearSeccionProductos());
        tabs.add("Categorías", crearSeccionCategorias());
        tabs.add("Proveedores", crearSeccionProveedores());
        tabs.add("Usuarios", crearSeccionUsuarios());
        tabs.add("Cupones", crearSeccionCupones());
        tabs.add("Administradores", crearSeccionAdministrador());

        add(new H2("Sistema de Inventario - CRUD"), tabs);
    }

    // ==========================================
    // 1. PRODUCTOS
    // ==========================================
    private Component crearSeccionProductos() {
        VerticalLayout layout = new VerticalLayout();
        TextField idField = new TextField("ID");
        TextField codigoField = new TextField("Código");
        TextField nombreField = new TextField("Nombre");
        TextField categoriaField = new TextField("Categoría");
        TextField precioField = new TextField("Precio");
        TextField stockField = new TextField("Stock");

        FormLayout form = new FormLayout(idField, codigoField, nombreField, categoriaField, precioField, stockField);

        Grid<Producto> grid = new Grid<>(Producto.class, false);
        grid.addColumn(Producto::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Producto::getCodigo).setHeader("Código").setAutoWidth(true);
        grid.addColumn(Producto::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Producto::getCategoria).setHeader("Categoría").setAutoWidth(true);
        grid.addColumn(Producto::getPrecio).setHeader("Precio").setAutoWidth(true);
        grid.addColumn(Producto::getStock).setHeader("Stock").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Producto.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Producto p = e.getValue();
            if (p != null) {
                idField.setValue(p.getId() != null ? p.getId().toString() : "");
                codigoField.setValue(p.getCodigo() != null ? p.getCodigo() : "");
                nombreField.setValue(p.getNombre() != null ? p.getNombre() : "");
                categoriaField.setValue(p.getCategoria() != null ? p.getCategoria() : "");
                precioField.setValue(p.getPrecio() != null ? p.getPrecio().toString() : "");
                stockField.setValue(p.getStock() != null ? p.getStock().toString() : "");
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            Producto p = new Producto(
                codigoField.getValue(),
                nombreField.getValue(),
                categoriaField.getValue(),
                parseDouble(precioField.getValue()),
                parseInt(stockField.getValue())
            );
            crudService.guardar(p);
            refrescar.run();
            idField.setValue(p.getId() != null ? p.getId().toString() : "");
            Notification.show("Producto guardado con ID: " + p.getId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Producto p = crudService.buscarPorId(Producto.class, parseId(idField));
            if (p != null) {
                grid.asSingleSelect().setValue(p);
                Notification.show("Encontrado: " + p.getNombre());
            } else {
                Notification.show("No encontrado");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Producto p = crudService.buscarPorId(Producto.class, parseId(idField));
            if (p != null) {
                p.setCodigo(codigoField.getValue());
                p.setNombre(nombreField.getValue());
                p.setCategoria(categoriaField.getValue());
                p.setPrecio(parseDouble(precioField.getValue()));
                p.setStock(parseInt(stockField.getValue()));
                crudService.actualizar(p);
                refrescar.run();
                Notification.show("Producto actualizado");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Producto.class, parseId(idField));
            refrescar.run();
            limpiar(idField, codigoField, nombreField, categoriaField, precioField, stockField);
            Notification.show("Producto eliminado");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(idField, codigoField, nombreField, categoriaField, precioField, stockField);
            grid.asSingleSelect().clear();
        });

        StreamResource excelResource = new StreamResource("productos.xlsx", this::crearExcelProductos);
        excelResource.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        Anchor descargarExcel = new Anchor(excelResource, "Descargar Excel");
        descargarExcel.getElement().setAttribute("download", true);

        StreamResource pdfResource = new StreamResource("productos.pdf", () ->
                new ByteArrayInputStream(pdfReportService.generarReporteProductos(crudService.listarTodos(Producto.class))));
        pdfResource.setContentType("application/pdf");
        Anchor descargarPdf = new Anchor(pdfResource, "Descargar PDF");
        descargarPdf.getElement().setAttribute("download", true);

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar),
                new HorizontalLayout(descargarExcel, descargarPdf), grid);
        return layout;
    }

    private InputStream crearExcelProductos() {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Productos");
            Row encabezado = sheet.createRow(0);
            String[] columnas = {"ID", "Código", "Nombre", "Categoría", "Precio", "Stock"};
            for (int i = 0; i < columnas.length; i++) {
                encabezado.createCell(i).setCellValue(columnas[i]);
            }

            int fila = 1;
            for (Producto producto : crudService.listarTodos(Producto.class)) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(producto.getId() == null ? "" : producto.getId().toString());
                row.createCell(1).setCellValue(producto.getCodigo() == null ? "" : producto.getCodigo());
                row.createCell(2).setCellValue(producto.getNombre() == null ? "" : producto.getNombre());
                row.createCell(3).setCellValue(producto.getCategoria() == null ? "" : producto.getCategoria());
                row.createCell(4).setCellValue(producto.getPrecio() == null ? 0 : producto.getPrecio());
                row.createCell(5).setCellValue(producto.getStock() == null ? 0 : producto.getStock());
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(output);
            return new ByteArrayInputStream(output.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo generar el archivo Excel", e);
        }
    }

    // ==========================================
    // 2. CATEGORÍAS
    // ==========================================
    private Component crearSeccionCategorias() {
        VerticalLayout layout = new VerticalLayout();
        TextField idField = new TextField("ID");
        TextField nombreField = new TextField("Nombre");
        TextField descripcionField = new TextField("Descripción");

        FormLayout form = new FormLayout(idField, nombreField, descripcionField);

        Grid<Categoria> grid = new Grid<>(Categoria.class, false);
        grid.addColumn(Categoria::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Categoria::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Categoria::getDescripcion).setHeader("Descripción").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Categoria.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Categoria c = e.getValue();
            if (c != null) {
                idField.setValue(c.getId() != null ? c.getId().toString() : "");
                nombreField.setValue(c.getNombre() != null ? c.getNombre() : "");
                descripcionField.setValue(c.getDescripcion() != null ? c.getDescripcion() : "");
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            Categoria c = new Categoria(nombreField.getValue(), descripcionField.getValue());
            crudService.guardar(c);
            refrescar.run();
            idField.setValue(c.getId() != null ? c.getId().toString() : "");
            Notification.show("Categoría guardada con ID: " + c.getId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Categoria c = crudService.buscarPorId(Categoria.class, parseId(idField));
            if (c != null) {
                grid.asSingleSelect().setValue(c);
                Notification.show("Encontrada: " + c.getNombre());
            } else {
                Notification.show("No encontrada");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Categoria c = crudService.buscarPorId(Categoria.class, parseId(idField));
            if (c != null) {
                c.setNombre(nombreField.getValue());
                c.setDescripcion(descripcionField.getValue());
                crudService.actualizar(c);
                refrescar.run();
                Notification.show("Categoría actualizada");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Categoria.class, parseId(idField));
            refrescar.run();
            limpiar(idField, nombreField, descripcionField);
            Notification.show("Categoría eliminada");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(idField, nombreField, descripcionField);
            grid.asSingleSelect().clear();
        });

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar), grid);
        return layout;
    }

    // ==========================================
    // 3. PROVEEDORES
    // ==========================================
    private Component crearSeccionProveedores() {
        VerticalLayout layout = new VerticalLayout();
        TextField idField = new TextField("ID");
        TextField nombreField = new TextField("Nombre");
        TextField telefonoField = new TextField("Teléfono");
        TextField contrasenaField = new TextField("Contraseña");

        FormLayout form = new FormLayout(idField, nombreField, telefonoField, contrasenaField);

        Grid<Proveedor> grid = new Grid<>(Proveedor.class, false);
        grid.addColumn(Proveedor::getIdProveedor).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Proveedor::getNombreCompleto).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Proveedor::getTelefono).setHeader("Teléfono").setAutoWidth(true);
        grid.addColumn(Proveedor::getFechaCreacionCuenta).setHeader("Creación de cuenta").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Proveedor.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Proveedor p = e.getValue();
            if (p != null) {
                idField.setValue(p.getIdProveedor() != null ? p.getIdProveedor().toString() : "");
                nombreField.setValue(p.getNombreCompleto() != null ? p.getNombreCompleto() : "");
                telefonoField.setValue(p.getTelefono() != null ? p.getTelefono() : "");
                contrasenaField.setValue(p.getContrasena() != null ? p.getContrasena() : "");
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            Proveedor p = new Proveedor(
                contrasenaField.getValue(),
                nombreField.getValue(),
                telefonoField.getValue(),
                LocalDateTime.now()
            );
            crudService.guardar(p);
            refrescar.run();
            idField.setValue(p.getIdProveedor() != null ? p.getIdProveedor().toString() : "");
            Notification.show("Proveedor guardado con ID: " + p.getIdProveedor());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Proveedor p = crudService.buscarPorId(Proveedor.class, parseId(idField));
            if (p != null) {
                grid.asSingleSelect().setValue(p);
                Notification.show("Encontrado: " + p.getNombreCompleto());
            } else {
                Notification.show("No encontrado");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Proveedor p = crudService.buscarPorId(Proveedor.class, parseId(idField));
            if (p != null) {
                p.setNombreCompleto(nombreField.getValue());
                p.setTelefono(telefonoField.getValue());
                p.setContrasena(contrasenaField.getValue());
                crudService.actualizar(p);
                refrescar.run();
                Notification.show("Proveedor actualizado");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Proveedor.class, parseId(idField));
            refrescar.run();
            limpiar(idField, nombreField, telefonoField, contrasenaField);
            Notification.show("Proveedor eliminado");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(idField, nombreField, telefonoField, contrasenaField);
            grid.asSingleSelect().clear();
        });

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar), grid);
        return layout;
    }

    // ==========================================
    // 4. USUARIOS
    // ==========================================
    private Component crearSeccionUsuarios() {
        VerticalLayout layout = new VerticalLayout();
        TextField idField = new TextField("ID");
        TextField nombreField = new TextField("Nombre");
        TextField apellidoField = new TextField("Apellido");
        TextField emailField = new TextField("Email");
        TextField telefonoField = new TextField("Teléfono");
        TextField direccionField = new TextField("Dirección");
        TextField rolField = new TextField("Rol");
        TextField estadoField = new TextField("Estado");

        FormLayout form = new FormLayout(idField, nombreField, apellidoField, emailField, telefonoField, direccionField, rolField, estadoField);

        Grid<Usuario> grid = new Grid<>(Usuario.class, false);
        grid.addColumn(Usuario::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Usuario::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Usuario::getApellido).setHeader("Apellido").setAutoWidth(true);
        grid.addColumn(Usuario::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Usuario::getTelefono).setHeader("Teléfono").setAutoWidth(true);
        grid.addColumn(Usuario::getRol).setHeader("Rol").setAutoWidth(true);
        grid.addColumn(Usuario::getEstado).setHeader("Estado").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Usuario.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Usuario u = e.getValue();
            if (u != null) {
                idField.setValue(u.getId() != null ? u.getId().toString() : "");
                nombreField.setValue(u.getNombre() != null ? u.getNombre() : "");
                apellidoField.setValue(u.getApellido() != null ? u.getApellido() : "");
                emailField.setValue(u.getEmail() != null ? u.getEmail() : "");
                telefonoField.setValue(u.getTelefono() != null ? u.getTelefono() : "");
                direccionField.setValue(u.getDireccion() != null ? u.getDireccion() : "");
                rolField.setValue(u.getRol() != null ? u.getRol() : "");
                estadoField.setValue(u.getEstado() != null ? u.getEstado() : "");
            }
        });

        Button btnCrear = new Button("Crear", e -> {
            Usuario u = new Usuario(
                nombreField.getValue(),
                apellidoField.getValue(),
                emailField.getValue(),
                telefonoField.getValue(),
                direccionField.getValue(),
                rolField.getValue(),
                estadoField.getValue(),
                LocalDateTime.now()
            );
            crudService.guardar(u);
            refrescar.run();
            idField.setValue(u.getId() != null ? u.getId().toString() : "");
            Notification.show("Usuario guardado con ID: " + u.getId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Usuario u = crudService.buscarPorId(Usuario.class, parseId(idField));
            if (u != null) {
                grid.asSingleSelect().setValue(u);
                Notification.show("Encontrado: " + u.getNombre() + " " + u.getApellido());
            } else {
                Notification.show("No encontrado");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Usuario u = crudService.buscarPorId(Usuario.class, parseId(idField));
            if (u != null) {
                u.setNombre(nombreField.getValue());
                u.setApellido(apellidoField.getValue());
                u.setEmail(emailField.getValue());
                u.setTelefono(telefonoField.getValue());
                u.setDireccion(direccionField.getValue());
                u.setRol(rolField.getValue());
                u.setEstado(estadoField.getValue());
                crudService.actualizar(u);
                refrescar.run();
                Notification.show("Usuario actualizado");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Usuario.class, parseId(idField));
            refrescar.run();
            limpiar(idField, nombreField, apellidoField, emailField, telefonoField, direccionField, rolField, estadoField);
            Notification.show("Usuario eliminado");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", e -> {
            limpiar(idField, nombreField, apellidoField, emailField, telefonoField, direccionField, rolField, estadoField);
            grid.asSingleSelect().clear();
        });

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar), grid);
        return layout;
    }
    // ==========================================
    // 5. Cupones 
    // ==========================================
    private Component crearSeccionCupones() {
        VerticalLayout layout = new VerticalLayout();
        TextField idField = new TextField("ID");
        TextField codigoField = new TextField("Código");
        TextField porcentajeField = new TextField("Porcentaje");
        TextField usosField = new TextField("Usos");
        TextField estadoField = new TextField("Estado");
        TextField fechaCreacionField = new TextField("Fecha de Creación");

        FormLayout form = new FormLayout(idField, codigoField, porcentajeField, usosField, estadoField, fechaCreacionField);

        Grid<Cupones> grid = new Grid<>(Cupones.class, false);
        grid.addColumn(Cupones::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Cupones::getCodigo).setHeader("Código").setAutoWidth(true);
        grid.addColumn(Cupones::getPorcentaje).setHeader("Porcentaje").setAutoWidth(true);
        grid.addColumn(Cupones::getUsos).setHeader("Usos").setAutoWidth(true);
        grid.addColumn(Cupones::getEstado).setHeader("Estado").setAutoWidth(true);
        grid.addColumn(c -> c.getFechaCreacion() != null ? c.getFechaCreacion().toString() : "").setHeader("Fecha de Creación").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Cupones.class));
        refrescar.run();

        grid.asSingleSelect() .addValueChangeListener(e -> {
            Cupones c = e.getOldValue();
            if (c != null) {
                idField.setValue(c.getId() != null ? c.getId().toString() : "");
                codigoField.setValue(c.getCodigo() != null ? c.getCodigo() : "");
                porcentajeField.setValue(String.valueOf(c.getPorcentaje()));
                usosField.setValue(String.valueOf(c.getUsos()));
                estadoField.setValue(c.getEstado() != null ? c.getEstado() : "");
                fechaCreacionField.setValue(c.getFechaCreacion() != null ? c.getFechaCreacion().toString() : "");
            }
        }); 

        Button btnCrear = new Button("Crear", e -> {
            Cupones c = new Cupones(
                codigoField.getValue(),
                parseDouble(porcentajeField.getValue()),
                parseInt(usosField.getValue()),
                estadoField.getValue(),
                LocalDateTime.now()
            );
            crudService.guardar(c);
            refrescar.run();
            idField.setValue(c.getId() != null ? c.getId().toString() : "");
            Notification.show("Cupón guardado con ID: " + c.getId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Cupones c = crudService.buscarPorId(Cupones.class, parseId(idField));
            if (c != null) {
                grid.asSingleSelect().setValue(c);
                Notification.show("Encontrado: " + c.getCodigo());
            } else {
                Notification.show("No encontrado");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Cupones c = crudService.buscarPorId(Cupones.class, parseId(idField));
            if (c != null) {
                c.setCodigo(codigoField.getValue());
                c.setPorcentaje(parseDouble(porcentajeField.getValue()));
                c.setUsos(parseInt(usosField.getValue()));
                c.setEstado(estadoField.getValue());
                crudService.actualizar(c);
                refrescar.run();
                Notification.show("Cupón actualizado");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Cupones.class, parseId(idField));
            refrescar.run();
            limpiar(idField, codigoField, porcentajeField, usosField, estadoField, fechaCreacionField);
            Notification.show("Cupón eliminado");
        });

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar), grid);
        return layout;
    }
    // ==========================================
    // 6. ADMINISREADOR
    // ==========================================
    private Component crearSeccionAdministrador() {
        VerticalLayout layout = new VerticalLayout();
        TextField idField = new TextField("ID");
        TextField usuarioField = new TextField("Usuario");
        TextField contrasenaField = new TextField("Contraseña");
        TextField estadoField = new TextField("Estado");
        TextField fechaRegistroField = new TextField("Fecha de Registro");

        FormLayout form = new FormLayout(idField, usuarioField, contrasenaField, estadoField, fechaRegistroField);

        Grid<Administrador> grid = new Grid<>(Administrador.class, false);
        grid.addColumn(Administrador::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Administrador::getUsuarioAdministrador).setHeader("Usuario").setAutoWidth(true);
        grid.addColumn(Administrador::getContrasena).setHeader("Contraseña").setAutoWidth(true);
        grid.addColumn(Administrador::getEstado).setHeader("Estado").setAutoWidth(true);
        grid.addColumn(a -> a.getFechaRegistro() != null ? a.getFechaRegistro().toString() : "").setHeader("Fecha de Registro").setAutoWidth(true);

        Runnable refrescar = () -> grid.setItems(crudService.listarTodos(Administrador.class));
        refrescar.run();

        grid.asSingleSelect().addValueChangeListener(e -> {
            Administrador a = e.getValue();
            if (a != null) {
                idField.setValue(Long.toString(a.getId()));
                usuarioField.setValue(a.getUsuarioAdministrador() != null ? a.getUsuarioAdministrador() : "");
                contrasenaField.setValue(a.getContrasena() != null ? a.getContrasena() : "");
                estadoField.setValue(a.getEstado() != null ? a.getEstado() : "");
                fechaRegistroField.setValue(a.getFechaRegistro() != null ? a.getFechaRegistro().toString() : "");
            }
        });
        Button btnCrear = new Button("Crear", e -> {
            Administrador a = new Administrador(
                usuarioField.getValue(),
                contrasenaField.getValue(),
                estadoField.getValue(),
                LocalDateTime.now()
            );
            crudService.guardar(a);
            refrescar.run();
            idField.setValue(Long.toString(a.getId()));
            Notification.show("Administrador guardado con ID: " + a.getId());
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", e -> {
            Administrador a = crudService.buscarPorId(Administrador.class, parseId(idField));
            if (a != null) {
                grid.asSingleSelect().setValue(a);
                Notification.show("Encontrado: " + a.getUsuarioAdministrador());
            } else {
                Notification.show("No encontrado");
            }
        });

        Button btnActualizar = new Button("Actualizar", e -> {
            Administrador a = crudService.buscarPorId(Administrador.class, parseId(idField));
            if (a != null) {
                a.setUsuarioAdministrador(usuarioField.getValue());
                a.setContrasena(contrasenaField.getValue());
                a.setEstado(estadoField.getValue());
                crudService.actualizar(a);
                refrescar.run();
                Notification.show("Administrador actualizado");
            } else {
                Notification.show("ID no encontrado");
            }
        });

        Button btnEliminar = new Button("Eliminar", e -> {
            crudService.eliminar(Administrador.class, parseId(idField));
            refrescar.run();
            limpiar(idField, usuarioField, contrasenaField, estadoField, fechaRegistroField);
            Notification.show("Administrador eliminado");
        });

        layout.add(form, crearAcciones(btnCrear, btnConsultar, btnActualizar, btnEliminar), grid);
        return layout;
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================
    private Long parseId(TextField field) {
        try {
            return Long.parseLong(field.getValue().trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Double parseDouble(String val) {
        try {
            return Double.parseDouble(val.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Integer parseInt(String val) {
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void limpiar(TextField... campos) {
        for (TextField c : campos) {
            c.clear();
        }
    }

    private HorizontalLayout crearAcciones(Button... botones) {
        HorizontalLayout layout = new HorizontalLayout(botones);
        layout.getStyle().set("flex-wrap", "wrap");
        return layout;
    }
}
