# PagoFlex

Aplicación Android para gestionar cuentas por pagar, registrar pagos y consultar su historial, con una interfaz desarrollada en Jetpack Compose. La app consume una API REST construida con microservicios (Spring Boot + Docker).

> Proyecto académico. El registro de pagos es una simulación: no realiza cobros bancarios ni procesa transacciones reales.

## Arquitectura

```text
App Android (Compose + MVVM)
        │  Retrofit + JWT
        ▼
API Gateway :8080 ──► usuarios · deudas · pagos · tarjetas
                      (cada uno con su propia base de datos MySQL)
```

La app ya no usa SQLite: todos los datos viven en los microservicios. El backend está en su propio repositorio: **[agrega aquí el enlace al repositorio del backend]**.

## Funcionalidades

### Autenticación y cuenta
- Registro de usuarios con validación de datos.
- Inicio y cierre de sesión con JWT; la sesión se conserva entre aperturas de la app.
- Validación de RUT mediante dígito verificador.
- Validación de correo electrónico y contraseña.
- Recuperación de cuenta mediante dos preguntas secretas.
- Consulta y edición de datos personales.
- Cambio de contraseña.

### Perfil de usuario
- Selección de una foto desde la galería.
- Captura de una foto con la cámara.
- Solicitud de permisos según la versión de Android.
- Reducción de la imagen antes de enviarla al servidor.
- La foto se sube al backend y se recupera desde él.

### Gestión de deudas
- Creación, actualización y eliminación de deudas (rol GESTOR).
- Pago en cuotas: la primera cuota vence en la fecha elegida y las demás cada mes.
- Las cuotas se pagan en orden.
- Validación de concepto, empresa, monto y vencimiento.
- Ordenamiento por vencimiento e indicadores de cuentas vencidas, próximas a vencer y al día.
- Restricciones para modificar o eliminar deudas que ya tienen pagos.

### Tarjetas
- Registro de hasta cuatro tarjetas por usuario.
- Se guardan solo los últimos cuatro dígitos, titular, vencimiento y marca.
- Selección de una tarjeta principal y eliminación de tarjetas.
- Validación de vigencia antes de registrar un pago.

### Pagos e historial
- Pago de una cuota o de la deuda completa con una tarjeta.
- Actualización del estado de la deuda a `PAGADA`.
- Historial de pagos por usuario con su total.

## Roles

| Rol | Qué puede hacer en la app |
|---|---|
| `USUARIO` | Ver y pagar sus propias deudas, gestionar sus tarjetas y ver su historial |
| `GESTOR` | Crear, editar y eliminar deudas de los usuarios |
| `ADMIN` | Consultar las deudas y los historiales de los usuarios |

### Cuentas de demostración

Se crean automáticamente en el backend con la base de usuarios vacía:

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@pagoflex.cl | Admin123$ |
| Gestor | gestor@pagoflex.cl | Gestor123$ |
| Usuario | emilia@pagoflex.cl | Emilia123$ |

> Credenciales solo para pruebas académicas. En una instalación existente pueden haber cambiado.

## Tecnologías

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje principal |
| Jetpack Compose | Construcción de la interfaz |
| Material 3 | Componentes y estilos visuales |
| ViewModel + StateFlow | Estado y acciones de las pantallas (MVVM) |
| Kotlin Coroutines | Trabajo asíncrono |
| Retrofit + Gson | Consumo de la API REST |
| OkHttp (interceptores) | Envío del token JWT y registro de peticiones |
| SharedPreferences | Persistencia de la sesión |
| JUnit | Pruebas unitarias |
| Git y GitHub | Control de versiones y colaboración |
| Gradle | Compilación y gestión de dependencias |

## Requisitos

- Android Studio compatible con la configuración Gradle del proyecto.
- Android SDK correspondiente al nivel de compilación configurado.
- Emulador o dispositivo con Android API 24 o superior.
- El backend de PagoFlex en ejecución (Docker).
- Git, si se descargará el proyecto mediante clonación.

### Configuración Android

```kotlin
namespace = "com.example.pagoflex"

compileSdk {
    version = release(37)
}

defaultConfig {
    applicationId = "com.example.pagoflex"
    minSdk = 24
    targetSdk = 36
}
```

## Instalación y ejecución

### 1. Levantar el backend

Sigue las instrucciones del repositorio del backend:

```bash
docker compose up -d --build
```

Comprueba que el gateway responde en `http://localhost:8080/swagger-ui.html`.

### 2. Clonar la app

```bash
git clone https://github.com/Cristhianll/AppPagoFlex.git
cd AppPagoFlex
```

### 3. Abrir el proyecto

1. Abrir Android Studio.
2. Seleccionar `Open`.
3. Elegir la carpeta raíz del proyecto.
4. Esperar a que finalice la sincronización de Gradle.

### 4. Configurar el SDK

Revisar la ubicación del SDK en:

```text
Settings > Languages & Frameworks > Android SDK
```

Si aparece el error `SDK location not found`, crear un archivo `local.properties` en la raíz del proyecto con la ruta del SDK del equipo. Ejemplo para Windows:

```properties
sdk.dir=C:/Users/TU_USUARIO/AppData/Local/Android/Sdk
```

> `local.properties` es específico de cada equipo y no debe subirse al repositorio.

### 5. Configurar la URL del backend

La dirección está en `data/remote/RetrofitClient.kt`:

```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/"
```

| Dónde ejecutas la app | Valor de `BASE_URL` |
|---|---|
| Emulador de Android Studio | `http://10.0.2.2:8080/` |
| Celular físico en la misma red WiFi | `http://IP-DE-TU-PC:8080/` (por ejemplo `http://192.168.1.50:8080/`) |

- `10.0.2.2` es la dirección con la que el emulador llega al `localhost` del PC.
- `8080` es el puerto del API Gateway; la app nunca habla directamente con cada microservicio.
- El manifest habilita `usesCleartextTraffic` para permitir `http` en desarrollo.
- En Android 17 puede ser necesario conceder el permiso de red local en Ajustes si la app no logra conectarse al backend.

### 6. Ejecutar la aplicación

1. Crear o seleccionar un emulador, o conectar un dispositivo.
2. Seleccionar la configuración `app`.
3. Presionar `Run`.

## Estructura del proyecto

```text
com.example.pagoflex
├── data
│   ├── PagoFlexRepository.kt
│   ├── SesionPreferencias.kt
│   └── remote
│       ├── RetrofitClient.kt
│       ├── ApiModelos.kt
│       ├── UsuarioApi.kt
│       ├── DeudaApi.kt
│       ├── CuotaApi.kt
│       ├── PagoApi.kt
│       └── TarjetaApi.kt
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
│   ├── LoginViewModel.kt
│   ├── FormularioViewModel.kt
│   ├── DeudaViewModel.kt
│   ├── CuotaViewModel.kt
│   └── PagoViewModel.kt
└── ui
    ├── components
    ├── navigation
    ├── screens
    ├── styles
    └── theme
```

### Responsabilidades

- `data`: repositorio, sesión del usuario y cliente de red.
- `data/remote`: interfaces Retrofit (una por microservicio), modelos de la API y cliente HTTP con el interceptor del token.
- `validation`: reglas de validación (cuenta, RUT, deuda, tarjeta y preguntas secretas).
- `model`: estructuras de estado utilizadas por las pantallas.
- `viewmodel`: estado y acciones de las pantallas.
- `components`: elementos reutilizables de interfaz.
- `navigation`: navegación entre pantallas.
- `screens`: pantallas de la aplicación.
- `styles` y `theme`: estilos, colores y dimensiones.

Las validaciones se mantienen en objetos independientes para centralizar reglas y evitar duplicación.

## Comunicación con el backend

| Servicio | Prefijo de ruta | Uso en la app |
|---|---|---|
| Usuarios | `/api/usuarios` | Login, registro, recuperación, perfil y foto |
| Deudas | `/api/deudas` | Deudas y cuotas |
| Pagos | `/api/pagos` | Registro e historial de pagos |
| Tarjetas | `/api/tarjetas` | Tarjetas del usuario |

Tras el login, el token JWT se guarda en la sesión y un interceptor de OkHttp lo agrega como `Authorization: Bearer <token>` en cada petición.

## Permisos

| Permiso | Uso |
|---|---|
| `INTERNET` | Comunicarse con el backend |
| `ACCESS_LOCAL_NETWORK` | Conectar con servidores en la red local (Android 17 o superior) |
| `CAMERA` | Capturar una foto de perfil |
| `READ_MEDIA_IMAGES` | Acceso a imágenes en Android 13 o superior |
| `READ_MEDIA_VISUAL_USER_SELECTED` | Acceso parcial a fotos en Android 14 o superior |
| `READ_EXTERNAL_STORAGE` | Lectura de almacenamiento hasta Android 12L (`maxSdkVersion="32"`) |

Los permisos de cámara y galería se solicitan al usar esas opciones. La selección de imágenes utiliza `ActivityResultContracts.GetContent()`. La cámara se declara como característica no obligatoria para la instalación.

## Compilación y pruebas

Ejecutar los comandos desde la raíz del proyecto.

### Windows (PowerShell)

Compilar el APK de depuración:

```powershell
.\gradlew.bat :app:assembleDebug
```

Ejecutar las pruebas unitarias:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Las pruebas cubren los ViewModels de login, formulario, deudas y pagos.

### Verificaciones manuales sugeridas

- Registrar una cuenta con datos válidos y comprobar los mensajes de validación.
- Iniciar sesión, cerrar la app y comprobar que la sesión se conserva; cerrar sesión.
- Editar el perfil y subir una foto desde la cámara y desde la galería.
- Autorizar y rechazar permisos.
- Registrar una tarjeta y comprobar su vencimiento.
- Iniciar sesión como gestor y crear una deuda en cuotas.
- Pagar las cuotas en orden y verificar el cambio de estado.
- Comprobar el historial y su total.
- Confirmar que cada usuario ve solo sus propios registros.

## Seguridad y alcance académico

- La autenticación usa JWT; el backend guarda las contraseñas con BCrypt y las respuestas secretas con SHA-256.
- Los permisos por rol se validan en el backend, no solo en la app.
- Los datos de las tarjetas son referencias (últimos cuatro dígitos) para la simulación, no una integración con una pasarela de pagos.
- No se realizan cobros bancarios reales.
- La conexión usa `http` sin cifrar, solo para desarrollo local; en producción debe usarse `https`.

Antes de un uso productivo se requiere revisar la autenticación, la recuperación de cuenta, la protección de datos y el procesamiento de pagos.

## Trabajo colaborativo

El proyecto se desarrolló con Git y GitHub, organizando el trabajo en seis ramas: una principal y cinco destinadas a funcionalidades, correcciones y reorganización.

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

Proyecto desarrollado de forma colaborativa como parte de una actividad académica.
