package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.model.Categoria;
import com.example.service.CrudService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Prueba de punta a punta contra la base de datos de Neon.
 * Solo se ejecuta con NEON_IT=true y deshace sus cambios (rollback) al terminar.
 */
@SpringBootTest
@Transactional
@EnabledIfEnvironmentVariable(named = "NEON_IT", matches = "true")
class NeonConnectionIT {

    @DynamicPropertySource
    static void neon(DynamicPropertyRegistry registro) {
        NeonDatabase.propiedades().forEach((clave, valor) -> registro.add(clave, () -> valor));
    }

    @Autowired
    private CrudService crudService;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void conectaANeonYHaceCrud() {
        String host = jdbc.queryForObject("SELECT inet_server_addr()::text || ' ' || version()", String.class);
        System.out.println("Conectado a: " + host);
        assertTrue(jdbc.queryForObject("SELECT current_database()", String.class).equals("neondb"));

        Categoria guardada = crudService.guardar(new Categoria("Prueba Neon", "creada por NeonConnectionIT"));
        assertNotNull(guardada.getId());

        Categoria leida = crudService.buscarPorId(Categoria.class, guardada.getId());
        assertEquals("Prueba Neon", leida.getNombre());
        System.out.println("CRUD OK: " + leida + " (se revierte al terminar)");
    }
}
