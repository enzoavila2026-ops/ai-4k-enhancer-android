# AI 4K Enhancer (Android)

Aplicación Android que utiliza inteligencia artificial para mejorar imágenes a resolución 4K mediante super-resolución con ESRGAN.

## 🚧 Estado del proyecto
- UI completa con Jetpack Compose
- Lógica de procesamiento con TensorFlow Lite integrada
- **Pendiente**: agregar el modelo TFLite (ver abajo)

## 📱 Funcionalidades
- Selección de imagen desde galería o cámara
- Mejora a 4K usando IA (modelo ESRGAN 4x)
- Comparación visual antes/después
- Controles de nitidez y formato de salida

## 🔧 Tecnologías
- Kotlin + Jetpack Compose
- TensorFlow Lite (con delegado GPU/NNAPI)
- Coil para carga de imágenes
- Arquitectura MVVM con ViewModel

## 📥 Instalación y compilación

1. Clonar el repositorio
2. Abrir con Android Studio (versión Hedgehog o superior)
3. **Descargar el modelo TFLite** desde una fuente confiable, por ejemplo:
   - [PINTO_model_zoo (Real-ESRGAN)](https://github.com/PINTO0309/PINTO_model_zoo/tree/main/154_ESRGAN)
   - Descargar la versión `ESRGAN_4x_float16.tflite` o `ESRGAN_4x_dynamic_range_quant.tflite`
4. Renombrar el archivo descargado a `realesrgan_x4plus.tflite`
5. Colocarlo en `app/src/main/assets/`
6. Ejecutar la app en un dispositivo/emulador

## 📝 Notas
- El modelo de prueba debe admitir entrada 512×512 y escala 4×. Si usas otro tamaño, ajusta `SuperResolutionInterpreter.kt`.
- Para imágenes de mayor resolución se necesita implementar *tiling* (procesamiento por parches).
- La primera ejecución cargará el modelo (~2-8 segundos).

## 🤝 Contribuciones
Proyecto colaborativo. Revisa los Issues para ver tareas abiertas.
