package com.example;

import com.vaadin.flow.theme.aura.Aura;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;

@SpringBootApplication
@StyleSheet(Aura.STYLESHEET)
@StyleSheet("styles.css") // Your custom styles
@Push
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        // Conexión a Neon (ver NeonDatabase); DB_URL / DB_USERNAME / DB_PASSWORD tienen prioridad
        app.setDefaultProperties(NeonDatabase.propiedades());
        app.run(args);
    }

}
