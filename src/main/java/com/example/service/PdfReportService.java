package com.example.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.model.Producto;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class PdfReportService {

    public byte[] generarReporteProductos(List<Producto> productos) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, output);
            document.open();
            document.add(new Paragraph("Inventario de productos"));
            document.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(6);
            tabla.setWidthPercentage(100);
            String[] encabezados = {"ID", "Código", "Nombre", "Categoría", "Precio", "Stock"};
            for (String encabezado : encabezados) {
                tabla.addCell(encabezado);
            }
            for (Producto producto : productos) {
                tabla.addCell(texto(producto.getId()));
                tabla.addCell(texto(producto.getCodigo()));
                tabla.addCell(texto(producto.getNombre()));
                tabla.addCell(texto(producto.getCategoria()));
                tabla.addCell(texto(producto.getPrecio()));
                tabla.addCell(texto(producto.getStock()));
            }
            document.add(tabla);
            document.close();
            return output.toByteArray();
        } catch (DocumentException e) {
            if (document.isOpen()) {
                document.close();
            }
            throw new IllegalStateException("No se pudo generar el PDF del inventario", e);
        }
    }

    private String texto(Object valor) {
        return valor == null ? "" : valor.toString();
    }
}
