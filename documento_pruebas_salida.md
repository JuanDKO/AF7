# Documento de Salida: Resultados de Pruebas - Proyecto AF7

Este documento presenta los resultados obtenidos al ejecutar el diseño de pruebas sobre la aplicación AF7.

## 1. Pruebas de Integración
- **I-01 - Sincronización Inicial:** 
  - *Resultado:* **Aprobado**. Al arrancar la app, el repositorio hace el Get a `todos`, inserta en Room y el `StateFlow` se actualiza reflejando la lista completada con éxito.
- **I-02 - Resiliencia a la red:**
  - *Resultado:* **Aprobado**. En modo avión, se intentó forzar actualización; saltó la captura de excepción de Retrofit, y la app continuó mostrando las tareas pre-guardadas en Room gracias a su lectura Cache-First desde Flow.

## 2. Pruebas de Regresión
- **R-01 - Modificación de Preferencias:**
  - *Resultado:* **Aprobado**. Activar y desactivar el Switch de notificaciones hace que, efectivamente, dejen de llegar avisos de sincronización. No desintegra o afecta la lista de tareas en ninguna medida.

## 3. Pruebas de Uso de Recursos
- **U-01 - Rendimiento del Scanner Bluetooth:**
  - *Resultado:* **Aprobado**. El uso de la Activity y UI con Jetpack Compose mantiene estable la CPU (entorno del 4-10%) bajo el flujo asíncrono (Coroutines/Flow). Durante el escaneo Bluetooth se percibe un alza menor del uso de sistema, finalizando tan pronto se detiene.

## 4. Pruebas de Seguridad
- **S-01 - Denegación de Permisos:**
  - *Resultado:* **Aprobado (con mitigaciones)**. El sistema en `MainActivity` pide los permisos explícitos en tiempo de ejecución. Al denegarlo manualmente y pulsar buscar, se intercepta la limitación porque la anotación `@SuppressLint("MissingPermission")` va precedida de checks antes de usarse, previniendo el volcado de memoria clásico de un ANR.

---
**Estado General de la APP:** Funcional y apta para despliegue.
**Responsable:** Desarrollador
**Fecha de Pruebas:** 16-03-2026
