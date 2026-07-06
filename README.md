# sharedPreferences_OchoaIvan

Práctica Android desarrollada con Kotlin y Jetpack Compose. En la rama `datastore`, la sesión y el carrito se almacenan de forma asíncrona y transaccional con Preferences DataStore.

## Credenciales

- Correo: `admin@mail.com`
- Contraseña: `1234`

## Funcionalidades

- Login con validación y acceso automático al reabrir la app.
- Catálogo local de videojuegos.
- Búsqueda por coincidencia parcial del nombre, sin distinguir mayúsculas.
- Consulta de productos por ID.
- Pantalla de detalle.
- Carrito con cantidades, total y eliminación de productos.
- Persistencia del carrito y de la sesión con Preferences DataStore.
- Cierre de sesión sin perder el carrito.

## Verificación

```text
./gradlew assembleDebug
./gradlew testDebugUnitTest
```
