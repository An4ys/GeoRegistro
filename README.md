# GeoRegistro 

Aplicación móvil desarrollada en Android Studio (Java) para el registro georreferenciado de incidentes, utilizando Firebase Cloud Firestore, Google Maps SDK y sensores del dispositivo.

# Requisitos
- Android Studio (última versión recomendada).
- SDK de Android (API nivel 24 o superior).
- Dispositivo físico o emulador con servicios de Google Play.

# Configuración
1. Clonar el repositorio:
   git clone https://github.com/An4ys/GeoRegistro.git
2. Debido a políticas de seguridad, el archivo de credenciales `google-services.json` no se encuentra versionado en el repositorio público. Para compilar la app localmente:
   - Solicita el archivo `google-services.json`.
   - Colócalo dentro de la carpeta `app/` del proyecto.
3. Sincroniza el proyecto con Gradle.

# Pruebas de la aplicación
- **Registro con GPS:** Ingresa a la pantalla de registro, permite los permisos de ubicación para capturar automáticamente las coordenadas actuales.
- **Sensor:** Realiza una sacudida o movimiento con el dispositivo para activar la limpieza automática de los campos del formulario.
- **Historial y Mapa:** Visualiza los registros almacenados en la base de datos Cloud Firestore y su respectiva ubicación en el mapa.






#Declaración de Uso de IA

Para el desarrollo y entrega de este proyecto, se utilizó asistencia de Inteligencia Artificial (Google Gemini) como herramienta de apoyo técnico exclusivamente para las siguientes tareas:
- Guía paso a paso en la ejecución de comandos de Git y la estructuración del historial mediante Conventional Commits.
- Asesoría en buenas prácticas de ciberseguridad para el ocultamiento de credenciales (`google-services.json`) y la configuración restrictiva de las reglas de acceso en Firebase Cloud Firestore.
- Resolución de advertencias menores de compilación en Android Studio y estructuración de este documento (README).
