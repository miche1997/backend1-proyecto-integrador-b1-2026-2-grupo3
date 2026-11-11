# Eat And Bite - Gestión de Entidades (CRUD)

## Resumen ejecutivo 
Eat And Bite requería una solución centralizada para administrar sus operaciones cotidianas sin depender de manipulación manual propensa a errores. Este sistema implementa un módulo integral de Gestión de Entidades (CRUD) que permite a los administradores crear, consultar, modificar y dar de baja información clave del negocio: Clientes, Proveedores, Productos, Cupones y Administradores. 

![Vista previa](preview.png)
![Vista previa 2](Preview2.png)

## Arquitectura 
su arquitectura esta basado en un sisitema monolitico en capas en las cuales tenemos definidas :
Capa de Presentación (UI)Desarrollada con Vaadin 25, maneja componentes reactivos y vistas en el navegador sin requerir frameworks de frontend independientes.
Capa de Servicio y Negocio: Implementada con Spring Boot 4, orquesta las reglas de validación, lógica de cupones y operaciones CRUD.
Capa de Datos: En esta fase inicial, las entidades residen en memoria/colecciones locales, preparadas para acoplarse a repositorios JPA/Hibernate en fases posteriores usando postqres neo. tech 

## Tecnologías

- Java 25
- Spring Boot 4
- Vaadin 25
- postqres proximamente

## Requisitos
- Java 25 instalado.
JDK 25 instalado y configurado en la variable de entorno.
- Docker (opcional, para despliegue en contenedores).
- Java 25 instalado.

## Inicializar la Gestión de Entidades

Abre una terminal en la carpeta del proyecto y utiliza el siguiente comando.

En Windows (PowerShell):

```powershell
.\mvnw.cmd
```

En Linux, macOS o Git Bash:

```bash
./mvnw
```

Cuando la consola muestre `Started Application`, la página estará disponible en http://localhost:8080. El navegador se abre automáticamente; la primera vez tarda varios minutos porque descarga las dependencias.

Para detener el programa, presiona `Ctrl + C` en la terminal.

## Compilar para producción

Para generar el archivo `.jar` en modo producción, ejecuta:

```bash
./mvnw package
```

Y para arrancarlo:

```bash
java -jar target/app-1.0-SNAPSHOT.jar
```

## Docker

Para crear una imagen Docker, ejecuta:

```bash
docker build -t eat-and-bite:latest .
```

## Integrantes

- Samuel Pulgarin Chavarria
- Michell Paola Gonzalez Comas
- Andrey Hernandez Patiño
