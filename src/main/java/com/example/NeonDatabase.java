package com.example;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Traduce la cadena de conexión de Neon (formato postgresql://usuario:clave@host/db)
 * a las propiedades JDBC que usa Spring Boot.
 *
 * La URL se busca primero en las variables de entorno y luego en los archivos
 * .env.local / .env que genera "neon deploy" / "neon env pull".
 * Se prefiere DATABASE_URL_UNPOOLED (conexión directa): la aplicación ya tiene su
 * propio pool (HikariCP) y Hibernate modifica el esquema al iniciar (ddl-auto=update).
 */
public final class NeonDatabase {

    private static final List<String> VARIABLES = List.of("DATABASE_URL_UNPOOLED", "DATABASE_URL");
    private static final List<String> ARCHIVOS = List.of(".env.local", ".env");

    private NeonDatabase() {
    }

    /**
     * Propiedades NEON_JDBC_URL, NEON_DB_USERNAME y NEON_DB_PASSWORD,
     * o un mapa vacío si no se encontró ninguna URL de Neon.
     */
    public static Map<String, Object> propiedades() {
        return buscarUrl().map(NeonDatabase::aJdbc).orElseGet(Map::of);
    }

    private static Optional<String> buscarUrl() {
        for (String variable : VARIABLES) {
            String valor = System.getenv(variable);
            if (valor != null && !valor.isBlank()) {
                return Optional.of(valor);
            }
        }
        for (String archivo : ARCHIVOS) {
            Map<String, String> valores = leerArchivoEnv(Path.of(archivo));
            for (String variable : VARIABLES) {
                String valor = valores.get(variable);
                if (valor != null && !valor.isBlank()) {
                    return Optional.of(valor);
                }
            }
        }
        return Optional.empty();
    }

    private static Map<String, String> leerArchivoEnv(Path archivo) {
        Map<String, String> valores = new HashMap<>();
        if (!Files.isRegularFile(archivo)) {
            return valores;
        }
        try {
            for (String linea : Files.readAllLines(archivo)) {
                linea = linea.strip();
                int igual = linea.indexOf('=');
                if (linea.isEmpty() || linea.startsWith("#") || igual < 0) {
                    continue;
                }
                String clave = linea.substring(0, igual).strip();
                String valor = linea.substring(igual + 1).strip();
                if (valor.length() >= 2 && (valor.startsWith("\"") || valor.startsWith("'"))
                        && valor.endsWith(valor.substring(0, 1))) {
                    valor = valor.substring(1, valor.length() - 1);
                }
                valores.put(clave, valor);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + archivo, e);
        }
        return valores;
    }

    private static Map<String, Object> aJdbc(String url) {
        URI uri = URI.create(url);
        String usuario = "";
        String clave = "";
        if (uri.getRawUserInfo() != null) {
            String[] partes = uri.getRawUserInfo().split(":", 2);
            usuario = URLDecoder.decode(partes[0], StandardCharsets.UTF_8);
            clave = partes.length > 1 ? URLDecoder.decode(partes[1], StandardCharsets.UTF_8) : "";
        }

        StringBuilder jdbc = new StringBuilder("jdbc:postgresql://").append(uri.getHost());
        if (uri.getPort() != -1) {
            jdbc.append(':').append(uri.getPort());
        }
        jdbc.append(uri.getRawPath());
        if (uri.getRawQuery() != null) {
            // El driver JDBC llama "channelBinding" al parámetro "channel_binding" de libpq
            jdbc.append('?').append(uri.getRawQuery().replace("channel_binding=", "channelBinding="));
        }

        return Map.of(
                "NEON_JDBC_URL", jdbc.toString(),
                "NEON_DB_USERNAME", usuario,
                "NEON_DB_PASSWORD", clave);
    }
}
