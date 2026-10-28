# SM3 Dash — prototipo inicial

Launcher Android nativo para una radio de auto de 7 pulgadas. Objetivo de pantalla: 1024 × 600, orientación horizontal. Versión de referencia: Android 13.

## Estado de esta versión (0.1.0)

Incluye una primera pantalla de tablero construida con vistas Android nativas, reloj, selector de temas básico, acceso a aplicaciones comunes y lectura de velocidad GPS cuando se concede permiso y hay señal.

Todavía es un **prototipo inicial**. El clima real, la edición persistente de los datos del vehículo, la carátula definitiva del SM3, los controles multimedia reales, el diseño completo de los siete paneles, transparencia/desenfoque por panel y el modo vertical/horizontal de accesos aún deben desarrollarse. La radio FM del fabricante puede requerir una integración específica.

## Compilar desde GitHub usando el celular

1. Crea un repositorio nuevo en https://github.com/new llamado `SM3-Dash`.
2. Déjalo **Private** si prefieres que el código no sea público.
3. Descarga este ZIP y extráelo con el administrador de archivos del celular.
4. En GitHub, abre tu repositorio y pulsa **Add file → Upload files**.
5. Sube el contenido de la carpeta `SM3-Dash` (incluida la carpeta oculta `.github`). Confirma el commit en la rama `main`.
6. Abre la pestaña **Actions**. El flujo `Build SM3 Dash APK` debería comenzar automáticamente. Si no, selecciónalo y pulsa **Run workflow**.
7. Cuando termine en verde, abre la ejecución y descarga el artefacto `sm3-dash-debug-apk`.
8. Descomprime el artefacto y encontrarás `app-debug.apk`.

## Requisitos de compilación

- JDK 17
- Gradle 8.9
- Android SDK Platform 35 y Build Tools 35.0.0

GitHub Actions instala estas herramientas para ti. No necesitas instalar Android Studio en el celular.

## Instalación

Instala el APK en tu teléfono para una prueba inicial. Android puede pedir autorización para instalar aplicaciones desde el navegador o administrador de archivos. Después se puede probar en la radio. Para usarlo como launcher, selecciona **SM3 Dash** cuando Android pregunte qué aplicación usar para Inicio/Home, si el firmware de la radio permite launchers de terceros.

## Seguridad y límites

- La velocidad GPS es orientativa y puede perder precisión en túneles, estacionamientos o con mala señal. No sustituye al velocímetro del vehículo.
- No se conecta aún al OBD-II ni lee datos de la ECU.
- La información del auto todavía no se guarda de forma persistente.
- No se ha probado en el hardware real de la radio; primero debe compilarse y luego probarse.

## Identificador

`cl.sm3dash`
