# Sistema de Inventario: versión de escritorio (Electron)

Electron abre una ventana propia, arranca por dentro el `.jar` de Spring Boot + Vaadin en un puerto libre
(solo accesible desde esta PC) y muestra la aplicación. Al cerrar la ventana se cierra también Java.

## Requisitos

- Node.js 22 o superior
- Java 25 (el que pide el `pom.xml`), en el `PATH` o en `JAVA_HOME`

## 1. Generar el .jar (desde la raíz del proyecto)

```bash
mvnw clean package -DskipTests
```

Debe quedar `target/app-1.0-SNAPSHOT.jar`.

## 2. Probar la app de escritorio

```bash
cd desktop
npm install
npm start
```

## 3. Crear el instalador para Windows

```bash
cd desktop
npm run dist
```

El instalador queda en `desktop/dist/`.

### Incluir Java en el instalador (opcional)

Para que la PC del usuario no necesite tener Java instalado, crea un Java reducido dentro de `desktop/jre`
antes de `npm run dist`:

```bash
jlink --add-modules java.se,jdk.unsupported,jdk.zipfs,jdk.management --strip-debug --no-man-pages --no-header-files --output desktop/jre
```

La app usa primero ese Java; si no existe, usa `JAVA_HOME` y por último el `java` del `PATH`.

## Base de datos

El `.jar` busca la conexión igual que siempre (ver `NeonDatabase.java`): variables de entorno
`DB_URL`/`DB_USERNAME`/`DB_PASSWORD` o `DATABASE_URL`, o un archivo `.env.local`.

- En desarrollo (`npm start`) el `.env.local` se lee de la raíz del proyecto.
- Instalada, se lee de la carpeta donde está el `.exe`.

No incluyas el `.env.local` dentro del instalador: cualquiera que lo tenga podría leer la contraseña de la base.

## Otras opciones

- `INVENTARIO_JAR=ruta/al/archivo.jar npm start` usa otro `.jar` (útil para pruebas).
