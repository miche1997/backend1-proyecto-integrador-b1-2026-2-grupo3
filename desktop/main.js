// App de escritorio: arranca el .jar de Spring Boot + Vaadin y lo muestra en una ventana propia.
const { app, BrowserWindow, Menu, dialog, shell } = require('electron');
const { spawn, spawnSync } = require('child_process');
const fs = require('fs');
const http = require('http');
const net = require('net');
const path = require('path');

const TIEMPO_MAXIMO_ARRANQUE_MS = 180_000;
const MAX_LINEAS_LOG = 40;

let ventana = null;
let java = null;
let saliendo = false;
const ultimasLineas = [];

// Solo una instancia: si se abre otra vez, se enfoca la ventana existente
if (!app.requestSingleInstanceLock()) {
  app.quit();
} else {
  app.on('second-instance', () => {
    if (ventana) {
      if (ventana.isMinimized()) ventana.restore();
      ventana.focus();
    }
  });
  app.whenReady().then(iniciar);
}

async function iniciar() {
  if (app.isPackaged) Menu.setApplicationMenu(null);

  ventana = new BrowserWindow({
    width: 1280,
    height: 820,
    minWidth: 900,
    minHeight: 600,
    title: 'Sistema de Inventario',
    show: false,
  });
  ventana.once('ready-to-show', () => ventana.show());
  // Los enlaces externos se abren en el navegador del sistema, no dentro de la app
  ventana.webContents.setWindowOpenHandler(({ url }) => {
    shell.openExternal(url);
    return { action: 'deny' };
  });
  ventana.on('closed', () => { ventana = null; });
  ventana.loadFile(path.join(__dirname, 'cargando.html'));

  const jar = buscarJar();
  if (!jar) {
    return fallar(
      'No se encontró el .jar de la aplicación.',
      'Genéralo primero desde la raíz del proyecto con:\n\n  mvnw clean package -DskipTests');
  }

  const puerto = await puertoLibre();
  const url = `http://127.0.0.1:${puerto}/`;

  java = spawn(rutaJava(), [
    '-jar', jar,
    `--server.port=${puerto}`,
    '--server.address=127.0.0.1', // solo accesible desde esta PC
    '--vaadin.launch-browser=false',
  ], { cwd: carpetaDeTrabajo(), windowsHide: true });

  java.stdout.on('data', guardarLog);
  java.stderr.on('data', guardarLog);
  java.on('error', (err) => fallar(
    'No se pudo ejecutar Java.',
    `Instala Java 25 o define JAVA_HOME.\n\n${err.message}`));
  java.on('exit', (codigo) => {
    java = null;
    if (!saliendo) {
      fallar(`El servidor Java se cerró (código ${codigo}).`, ultimasLineas.join('\n'));
    }
  });

  try {
    await esperarServidor(url, TIEMPO_MAXIMO_ARRANQUE_MS);
    if (ventana) ventana.loadURL(url);
  } catch {
    if (java) fallar('El servidor tardó demasiado en arrancar.', ultimasLineas.join('\n'));
  }
}

// INVENTARIO_JAR (opcional) -> empaquetado: resources/app.jar -> desarrollo: ../target/*.jar
function buscarJar() {
  if (process.env.INVENTARIO_JAR) {
    return fs.existsSync(process.env.INVENTARIO_JAR) ? process.env.INVENTARIO_JAR : null;
  }
  if (app.isPackaged) {
    const jar = path.join(process.resourcesPath, 'app.jar');
    return fs.existsSync(jar) ? jar : null;
  }
  const target = path.join(__dirname, '..', 'target');
  if (!fs.existsSync(target)) return null;
  const jar = fs.readdirSync(target).find((f) => f.endsWith('.jar') && !f.endsWith('-plain.jar'));
  return jar ? path.join(target, jar) : null;
}

// Prioridad: Java incluido en el instalador (resources/jre) -> JAVA_HOME -> "java" del PATH
function rutaJava() {
  const exe = process.platform === 'win32' ? 'java.exe' : 'java';
  const candidatos = [];
  if (app.isPackaged) candidatos.push(path.join(process.resourcesPath, 'jre', 'bin', exe));
  if (process.env.JAVA_HOME) candidatos.push(path.join(process.env.JAVA_HOME, 'bin', exe));
  return candidatos.find((c) => fs.existsSync(c)) || 'java';
}

// NeonDatabase.java busca .env.local en la carpeta de trabajo:
// en desarrollo es la raíz del proyecto; instalada, la carpeta del .exe.
function carpetaDeTrabajo() {
  return app.isPackaged ? path.dirname(process.execPath) : path.join(__dirname, '..');
}

function puertoLibre() {
  return new Promise((resolve, reject) => {
    const servidor = net.createServer();
    servidor.unref();
    servidor.on('error', reject);
    servidor.listen(0, '127.0.0.1', () => {
      const { port } = servidor.address();
      servidor.close(() => resolve(port));
    });
  });
}

function esperarServidor(url, tiempoMaximo) {
  const limite = Date.now() + tiempoMaximo;
  return new Promise((resolve, reject) => {
    const intentar = () => {
      if (!java) return reject(new Error('Java terminó'));
      http.get(url, (res) => {
        res.resume();
        resolve();
      }).on('error', () => {
        if (Date.now() > limite) return reject(new Error('Tiempo agotado'));
        setTimeout(intentar, 500);
      });
    };
    intentar();
  });
}

function guardarLog(datos) {
  const texto = datos.toString();
  process.stdout.write(texto);
  for (const linea of texto.split(/\r?\n/)) {
    if (!linea.trim()) continue;
    ultimasLineas.push(linea);
    if (ultimasLineas.length > MAX_LINEAS_LOG) ultimasLineas.shift();
  }
}

let fallo = false;
function fallar(titulo, detalle) {
  if (fallo || saliendo) return;
  fallo = true;
  dialog.showErrorBox(titulo, (detalle || '').slice(-4000));
  app.quit();
}

function detenerJava() {
  saliendo = true;
  if (!java) return;
  if (process.platform === 'win32') {
    // En Windows "java" puede ser un lanzador que abre otro proceso: se cierra el árbol completo
    spawnSync('taskkill', ['/pid', String(java.pid), '/T', '/F'], { windowsHide: true });
  } else {
    java.kill();
  }
  java = null;
}

app.on('window-all-closed', () => app.quit());
app.on('before-quit', detenerJava);
process.on('exit', detenerJava);
