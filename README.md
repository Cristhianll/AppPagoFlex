# PagoFlex

Aplicación Android para gestionar cuentas por pagar, registrar pagos y consultar su historial desde una interfaz desarrollada con Jetpack Compose.

El proyecto incluye autenticación, perfiles de usuario, administración de deudas, registro de tarjetas y almacenamiento local mediante SQLite.

> Proyecto académico. El registro de pagos es una simulación interna: no realiza cobros bancarios ni procesa transacciones reales.

## Funcionalidades

### Autenticación y cuenta
- Registro de usuarios con validación de datos.
- Inicio y cierre de sesión.
- Validación de RUT mediante dígito verificador.
- Validación de correo electrónico y contraseña.
- Recuperación de cuenta mediante dos preguntas secretas.
- Consulta y edición de datos personales.

### Perfil de usuario
- Selección de una foto desde la galería.
- Captura de una foto utilizando la cámara.
- Solicitud de permisos según la versión de Android.
- Conversión y reducción de la imagen antes de almacenarla.
- Guardado de la foto en la base de datos como un campo BLOB.
- Recuperación de la foto asociada al usuario.
- Migración de fotos antiguas guardadas como archivos locales.

### Gestión de deudas
- Creación, actualización y eliminación de deudas pendientes.
- Asociación de cada deuda con un usuario.
- Validación de concepto, empresa, monto y vencimiento.
- Visualización del total pendiente.
- Ordenamiento de deudas por vencimiento.
- Indicadores de cuentas vencidas, próximas a vencer y al día.
- Restricciones para modificar o eliminar deudas que ya tienen pagos.

### Tarjetas
- Registro de hasta cuatro tarjetas por usuario.
- Almacenamiento de los últimos cuatro dígitos, titular, vencimiento y marca.
- Selección de una tarjeta principal.
- Eliminación de tarjetas.
- Validación de vigencia antes de registrar un pago.

### Pagos e historial
- Registro de pagos de deudas pendientes.
- Actualización de la deuda al estado `PAGADA`.
- Prevención de pagos duplicados para una misma deuda.
- Registro de la tarjeta utilizada.
- Historial de pagos por usuario.
- Visualización del total de los pagos incluidos en el historial.
- Generación de un identificador de comprobante con formato `PF-00000001`.

## Tecnologías

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje principal |
| Jetpack Compose | Construcción de la interfaz |
| Material 3 | Componentes y estilos visuales |
| ViewModel | Gestión del estado y acciones de las pantallas |
| Kotlin Coroutines | Trabajo asíncrono |
| SQLite | Persistencia local |
| SQLiteOpenHelper | Creación y actualización de la base de datos |
| Git y GitHub | Control de versiones y colaboración |
| Gradle | Compilación y gestión de dependencias |

## Requisitos

- Android Studio compatible con la configuración Gradle del proyecto.
- Android SDK correspondiente al nivel de compilación configurado.
- Emulador o dispositivo con Android API 24 o superior.
- Git, si se descargará el proyecto mediante clonación.

### Configuración Android

El módulo de la aplicación utiliza:

```kotlin
namespace = "com.example.pagoflex"

compileSdk {
    version = release(37)
}

defaultConfig {
    applicationId = "com.example.pagoflex"
    minSdk = 24
    targetSdk = 37
}
```

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone [https://github.com/Cristhianll/AppPagoFlex.git](https://github.com/Cristhianll/AppPagoFlex.git)
cd AppPagoFlex
```

Para trabajar con la rama de rediseño y autenticación:

```bash
git switch feature/redisenio-y-autenticacion
```

### 2. Abrir el proyecto

1. Abrir Android Studio.
2. Seleccionar `Open`.
3. Elegir la carpeta raíz del proyecto.
4. Esperar a que finalice la sincronización de Gradle.

### 3. Configurar el SDK

Revisar la ubicación del SDK en:

```text
Settings > Languages & Frameworks > Android SDK
```

Si aparece el error `SDK location not found`, crear un archivo
`local.properties` en la raíz del proyecto con la ruta del SDK del equipo.

Ejemplo para Windows:

```properties
sdk.dir=C:/Users/TU_USUARIO/AppData/Local/Android/Sdk
```

La ruta debe corresponder a una instalación existente del SDK.

> `local.properties` contiene configuración específica de cada equipo.
> No debe subirse al repositorio y está excluido mediante `.gitignore`.

### 4. Ejecutar la aplicación

1. Crear o seleccionar un emulador, o conectar un dispositivo.
2. Seleccionar la configuración `app`.
3. Presionar `Run`.

La aplicación utiliza una base de datos local. No requiere configurar un servidor de base de datos externo.

## Estructura del proyecto

```text
com.example.pagoflex
├── data
│   ├── PagoFlexDbHelper.kt
│   └── PagoFlexRepository.kt
├── model
│   ├── FormularioUiState.kt
│   └── PagoUiState.kt
├── validation
│   ├── PreguntasSecretas.kt
│   ├── ValidacionesCuenta.kt
│   ├── ValidacionesDeuda.kt
│   ├── ValidacionesRut.kt
│   └── ValidacionesTarjeta.kt
├── viewmodel
│   ├── FormularioViewModel.kt
│   └── PagoViewModel.kt
└── ui
    ├── components
    ├── navigation
    ├── screens
    ├── styles
    └── theme
```

### Responsabilidades

- `data`: acceso a SQLite y repositorio.
- `validation`: reglas de validación (cuenta, RUT, deuda, tarjeta y preguntas secretas).
- `model`: estructuras utilizadas por la aplicación.
- `components`: elementos reutilizables de interfaz.
- `navigation`: navegación entre pantallas.
- `screens`: pantallas de la aplicación.
- `styles` y `theme`: estilos, colores y dimensiones.
- `viewmodel`: estado y acciones relacionadas con las pantallas.

Las validaciones se mantienen en objetos independientes para centralizar reglas y evitar duplicación.

## Base de datos

La aplicación utiliza el archivo SQLite:

```text
pagoflex.db
```

La versión declarada en `PagoFlexDbHelper` es `3`.

### Tablas principales

| Tabla | Contenido |
|---|---|
| `usuarios` | Datos de cuenta, rol, preguntas secretas, hashes y foto |
| `deudas` | Concepto, empresa, monto, vencimiento y estado |
| `tarjetas` | Datos de referencia de las tarjetas de cada usuario |
| `pagos` | Deuda pagada, monto, fecha y tarjeta utilizada |

### Relaciones

```text
usuarios
   ├── deudas
   │      └── pagos
   └── tarjetas
```

Las claves foráneas se habilitan al configurar la base de datos.

### Fotos de perfil

La foto se almacena en la columna:

```sql
usuarios.foto BLOB
```

Flujo de almacenamiento:

```text
Cámara o galería
        ↓
Decodificación y reducción de imagen
        ↓
Conversión a JPG
        ↓
ByteArray
        ↓
Repositorio
        ↓
SQLite: usuarios.foto
```

La base de datos es local: la imagen se guarda dentro de SQLite, pero no se sincroniza automáticamente entre distintos dispositivos.

## Roles y cuentas de demostración

El proyecto define los roles `ADMIN` y `USUARIO`.

En una base de datos nueva se crean estas cuentas:

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@pagoflex.cl | Admin123$ |
| Usuario de demostración | emilia@pagoflex.cl | Emilia123$ |

La cuenta de demostración de Emilia incluye una deuda inicial.

> Estas credenciales son exclusivamente para pruebas académicas.
> En una instalación existente pueden haber cambiado.

## Permisos

| Permiso | Uso |
|---|---|
| `CAMERA` | Capturar una foto de perfil |
| `READ_MEDIA_IMAGES` | Solicitar acceso a imágenes en Android 13 o superior |
| `READ_MEDIA_VISUAL_USER_SELECTED` | Gestionar acceso parcial a fotos en Android 14 o superior |
| `READ_EXTERNAL_STORAGE` | Solicitar lectura de almacenamiento hasta Android 12L, limitado con `maxSdkVersion="32"` |

Los permisos se solicitan al utilizar las opciones de cámara o galería.

La selección de imágenes utiliza `ActivityResultContracts.GetContent()`.
Este selector puede proporcionar acceso al archivo elegido sin permiso general
de biblioteca; la solicitud adicional de permisos forma parte del requisito
académico del proyecto.

La cámara se declara como una característica no obligatoria para la instalación.

## Compilación y pruebas

Ejecutar los comandos desde la raíz del proyecto.

### Windows — PowerShell

Compilar el APK de depuración:

```powershell
.\gradlew.bat :app:assembleDebug
```

Ejecutar las pruebas unitarias existentes:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

### Verificaciones manuales sugeridas

- Registrar una cuenta con datos válidos.
- Comprobar los mensajes de validación.
- Iniciar y cerrar sesión.
- Editar el perfil.
- Autorizar y rechazar permisos de cámara y fotos.
- Guardar una foto y verificarla al volver a abrir la aplicación.
- Registrar una tarjeta y comprobar su vencimiento.
- Crear una deuda desde el flujo administrativo.
- Registrar su pago y verificar el cambio de estado.
- Comprobar el historial y su total.
- Confirmar que cada usuario ve sus propios registros.

## Seguridad y alcance académico

- Las contraseñas y respuestas secretas se almacenan mediante hashes SHA-256, no como texto plano.
- La implementación actual no utiliza un algoritmo especializado de almacenamiento de contraseñas con salt.
- La base de datos es local; no existe sincronización remota implementada en el código descrito.
- No se realizan cobros bancarios reales.
- Los datos registrados de las tarjetas son referencias para la simulación, no una integración con una pasarela de pagos.

Antes de un uso productivo se requiere revisar la autenticación, la recuperación de cuenta, la protección de datos y el procesamiento de pagos.


## Trabajo colaborativo

El proyecto se desarrolló utilizando Git y GitHub, organizando el trabajo en seis ramas: una principal y cinco destinadas a funcionalidades, correcciones y reorganización.

### Ramas del proyecto

| Rama | Propósito |
|---|---|
| `main` | Rama principal para integrar los cambios revisados del proyecto |
| `bugfix/corregir-error-validacion` | Corrección de errores de validación |
| `feature/cuenta-perfil` | Desarrollo de las funcionalidades de cuenta y perfil |
| `feature/dashboard-pago` | Desarrollo del dashboard y funcionalidades de pagos |
| `feature/rediseño-autentificacion` | Rediseño de la interfaz y funcionalidades de autenticación |
| `refactor/organizacion-pagoflex` | Reorganización y refactorización del código |

### Flujo de trabajo

1. Desarrollar las funcionalidades o correcciones en la rama correspondiente.
2. Revisar los archivos modificados.
3. Registrar los cambios mediante commits.
4. Subir los cambios al repositorio de GitHub.
5. Crear un Pull Request hacia `main`.
6. Revisar los cambios y comprobar el funcionamiento de la aplicación.
7. Integrar los cambios en la rama principal.

## Equipo de desarrollo

- Cristhian Llontop
- Emilia Zamora

Proyecto desarrollado de forma colaborativa como parte de una actividad académica
