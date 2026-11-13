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
        String[] encabezados = {"ID", "Código", "Nombre", "Categoría", "Precio", "Stock"};
        List<List<Object>> filas = productos.stream()
                .map(p -> List.<Object>of(texto(p.getId()), texto(p.getCodigo()), texto(p.getNombre()),
                        texto(p.getCategoria()), texto(p.getPrecio()), texto(p.getStock())))
                .toList();
        return generarReporte("Inventario de productos", encabezados, filas);
    }

    /**
     * Genera un PDF con una tabla a partir de encabezados y filas genéricas.
     */
    public byte[] generarReporte(String titulo, String[] encabezados, List<List<Object>> filas) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, output);
            document.open();
            document.add(new Paragraph(titulo));
            document.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(encabezados.length);
            tabla.setWidthPercentage(100);
            for (String encabezado : encabezados) {
                tabla.addCell(encabezado);
            }
            for (List<Object> fila : filas) {
                for (Object valor : fila) {
                    tabla.addCell(texto(valor));
                }
            }
            document.add(tabla);
            document.close();
            return output.toByteArray();
        } catch (DocumentException e) {
            if (document.isOpen()) {
                document.close();
            }
            throw new IllegalStateException("No se pudo generar el PDF: " + titulo, e);
        }
    }

    private String texto(Object valor) {
        return valor == null ? "" : valor.toString();
    }
}
