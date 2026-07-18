# EcoLambda

Aplicación móvil desarrollada con Kotlin, Jetpack Compose, Firebase y TensorFlow Lite para la clasificación inteligente de residuos mediante inteligencia artificial.

---

# Descripción

EcoLambda es una aplicación móvil desarrollada para facilitar la correcta separación de residuos mediante el uso de Inteligencia Artificial.

La aplicación permite al usuario capturar o seleccionar una imagen de un residuo para identificar automáticamente su categoría utilizando un modelo de aprendizaje automático integrado con TensorFlow Lite.

Además, incorpora un módulo educativo para fomentar el reciclaje, un historial de clasificaciones realizadas y almacenamiento de información mediante Firebase.

Este proyecto fue desarrollado como parte de un trabajo académico enfocado en el desarrollo móvil, aprendizaje automático y sostenibilidad ambiental.

---

# Características principales

- Inicio de sesión mediante Firebase Authentication.
- Registro de nuevos usuarios.
- Clasificación automática de residuos utilizando Inteligencia Artificial.
- Captura de fotografías desde la cámara.
- Selección de imágenes desde la galería.
- Historial de análisis realizados.
- Información educativa sobre reciclaje.
- Consulta de información detallada de cada tipo de residuo.
- Integración con Firebase Realtime Database.
- Modelo ejecutado localmente mediante TensorFlow Lite.

---

# Tecnologías utilizadas

- Kotlin
- Jetpack Compose
- Android Studio
- Firebase Authentication
- Firebase Realtime Database
- TensorFlow Lite
- Python
- TensorFlow
- Machine Learning
- Git
- GitHub

---

# Arquitectura del proyecto

```
EcoLambda
│
├── app
│   ├── screens
│   ├── navigation
│   ├── repository
│   ├── model
│   ├── ui
│   └── utils
│
├── scripts
│   ├── eliminar_fondo_dataset.py
│   └── modeloClasificadorBasura.py
│
├── assets
│
└── modelo TensorFlow Lite
```

---

# Inteligencia Artificial

El sistema utiliza un modelo de Machine Learning entrenado para identificar distintas categorías de residuos.

Durante el desarrollo se realizaron las siguientes actividades:

- Preparación del dataset.
- Organización de imágenes por categorías.
- Eliminación automática del fondo de las imágenes.
- Entrenamiento del modelo utilizando TensorFlow.
- Conversión del modelo a TensorFlow Lite.
- Integración del modelo dentro de la aplicación Android.
- Validación del modelo mediante pruebas de clasificación.

---

# Capturas de pantalla

## Autenticación

| Inicio de sesión | Crear cuenta |
|------------------|--------------|
| ![](Screenshots/login.png) | ![](Screenshots/register_account.png) |

---

## Aplicación

### Menú principal

![](Screenshots/main_menu.png)

### Clasificación de residuos

![](Screenshots/classify_waste.png)

### Resultado del análisis

![](Screenshots/analysis_result.png)

### Aprende a reciclar

![](Screenshots/learn_recycling.png)

### Información del residuo

![](Screenshots/waste_information.png)

### Historial

![](Screenshots/analysis_history.png)

### Perfil del usuario

![](Screenshots/user_profile.png)

---

# Desarrollo del proyecto

## Android Studio

![](Screenshots/android_studio_development.png)

---

## Modelo TensorFlow Lite

![](Screenshots/tensorflow_lite_model.png)

---

## Script para eliminación de fondo

![](Screenshots/background_removal_script.png)

---

## Organización del Dataset

![](Screenshots/dataset_structure.png)

---

## Imágenes del Dataset

![](Screenshots/dataset_images.png)

---

## Código de entrenamiento del modelo

![](Screenshots/model_training_code.png)

---

# Firebase

## Authentication

![](Screenshots/firebase_authentication.png)

---

## Realtime Database

![](Screenshots/firebase_realtime_database.png)

---

# Instalación

1. Clonar el repositorio.

```bash
git clone https://github.com/isaperez03/EcoLambda.git
```

2. Abrir el proyecto con Android Studio.

3. Configurar Firebase.

4. Sincronizar Gradle.

5. Ejecutar la aplicación.

---

# Requisitos

- Android Studio
- Android SDK
- Kotlin
- Firebase
- TensorFlow Lite
- Python 3
- JDK 17

---

# Participación en el proyecto

Proyecto desarrollado de manera colaborativa.

Mi participación incluyó:

- Desarrollo de funcionalidades de la aplicación móvil.
- Implementación de interfaces utilizando Jetpack Compose.
- Preparación y organización del dataset de imágenes.
- Preprocesamiento de imágenes para entrenamiento.
- Desarrollo e integración del modelo de Inteligencia Artificial.
- Integración del modelo TensorFlow Lite dentro de la aplicación.
- Implementación de funcionalidades utilizando Kotlin.
- Integración con Firebase Authentication y Realtime Database.
- Pruebas, validación y documentación del proyecto.

---

# Autor

**Juana Isabel Pérez López**

Ingeniería en Sistemas Computacionales

Instituto Tecnológico Superior de Misantla

2026
