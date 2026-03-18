# Documento de Entrada: Diseño de Pruebas - Proyecto AF7

Este documento describe el plan de pruebas para la aplicación móvil AF7, la cual gestiona tareas, sincroniza datos mediante una API, y ofrece características como escáner Bluetooth y reportes estadísticos.

## 1. Pruebas de Integración
**Objetivo:** Verificar que los módulos de interfaz de usuario, base de datos local (Room) y peticiones a red (Retrofit) funcionan correctamente como un sistema conjunto.
- **Caso de Prueba I-01:** Sincronización Inicial.
  - *Acción:* Abrir la aplicación con conexión a internet.
  - *Resultado Esperado:* La aplicación descargará las tareas de JSONPlaceholder y las almacenará en la base de datos local, reflejándose en el listado.
- **Caso de Prueba I-02:** Resiliencia a la red.
  - *Acción:* Desactivar la conexión a red y forzar sincronización.
  - *Resultado Esperado:* Aparece un mensaje de error y el listado de tareas mantiene el estado anterior cacheado.

## 2. Pruebas de Regresión
**Objetivo:** Asegurar que los cambios añadidos (como notificaciones, tema u orientación) no rompen características estables existentes.
- **Caso de Prueba R-01:** Modificación de Preferencias.
  - *Acción:* Cambiar nombre de usuario, o desactivar notificaciones, cerrar la app y reabrir.
  - *Resultado Esperado:* La configuración elegida persiste sin alterar el funcionamiento del MainViewModel o el RecyclerView/LazyColumn de la lista.

## 3. Pruebas de Uso de Recursos
**Objetivo:** Monitorear el consumo de batería, memoria y red por parte de la aplicación, especialmente en utilidades pesadas.
- **Caso de Prueba U-01:** Rendimiento del Scanner Bluetooth.
  - *Acción:* Mantener encendido el escáner Bluetooth (`BluetoothScanner.kt`) durante 2 minutos.
  - *Resultado Esperado:* El consumo de batería y la CPU no deberían desestabilizarse en extremo ni producir OutOfMemoryExceptions.

## 4. Pruebas de Seguridad
**Objetivo:** Comprobar la correcta gestión de permisos y peticiones HTTP.
- **Caso de Prueba S-01:** Denegación de Permisos.
  - *Acción:* Denegar el permiso de "Nearby Devices" (Bluetooth) desde Ajustes y pulsar iniciar escaneo.
  - *Resultado Esperado:* La aplicación no debe "crashear" (`SecurityException`). Debe capturar la excepción o la validación y no hacer nada, o mostrar aviso.

---
**Responsable:** Desarrollador
**Fecha de Elaboración:** 16-03-2026
