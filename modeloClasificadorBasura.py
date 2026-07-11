import os
import json
import random
from pathlib import Path

import numpy as np
import tensorflow as tf
from sklearn.metrics import classification_report, confusion_matrix

# CONFIGURACIÓN PRINCIPAL

DATASET_DIR = Path(r"C:\CDE\DATASET_FINAL_NO_BG")

OUTPUT_DIR = Path("modelo_residuos")
OUTPUT_DIR.mkdir(exist_ok=True)

IMG_SIZE = (224, 224)
BATCH_SIZE = 32
SEED = 123

EPOCHS_CABECERA = 15
EPOCHS_AJUSTE_FINO = 15

LEARNING_RATE_CABECERA = 0.001
LEARNING_RATE_AJUSTE_FINO = 0.00001

# REPRODUCIBILIDAD

random.seed(SEED)
np.random.seed(SEED)
tf.random.set_seed(SEED)

# VALIDAR ESTRUCTURA DEL DATASET

def validar_directorios():
    if not DATASET_DIR.exists():
        raise FileNotFoundError(f"No existe la carpeta del dataset: {DATASET_DIR}")

    print("Directorio base del dataset encontrado correctamente:")
    print(f"Ruta: {DATASET_DIR}")

# CARGAR Y DIVIDIR DATASETS

def cargar_datasets():
    # 80% para entrenamiento
    train_ds = tf.keras.utils.image_dataset_from_directory(
        DATASET_DIR,
        validation_split=0.2,
        subset="training",
        seed=SEED,
        image_size=IMG_SIZE,
        batch_size=BATCH_SIZE,
        label_mode="categorical",
        shuffle=True
    )

    val_test_ds = tf.keras.utils.image_dataset_from_directory(
        DATASET_DIR,
        validation_split=0.2,
        subset="validation",
        seed=SEED,
        image_size=IMG_SIZE,
        batch_size=BATCH_SIZE,
        label_mode="categorical",
        shuffle=True
    )

    class_names = train_ds.class_names

    val_batches = tf.data.experimental.cardinality(val_test_ds)
    test_ds = val_test_ds.take(val_batches // 2)
    valid_ds = val_test_ds.skip(val_batches // 2)

    print("\nClases detectadas:")
    for indice, nombre in enumerate(class_names):
        print(f"{indice}: {nombre}")

    return train_ds, valid_ds, test_ds, class_names

# TRADUCCIÓN DE ETIQUETAS

def guardar_etiquetas(class_names):
    # Se actualizaron las traducciones para que coincidan con tus nuevas carpetas
    traducciones = {
        "carton_papel": "Cartón y Papel",
        "metal": "Metal",
        "organico": "Orgánico",
        "plastico": "Plástico",
        "vidrio": "Vidrio"
    }

    labels_en = OUTPUT_DIR / "labels_en.txt"
    labels_es = OUTPUT_DIR / "labels_es.txt"
    labels_json = OUTPUT_DIR / "labels.json"

    with open(labels_en, "w", encoding="utf-8") as f:
        for clase in class_names:
            f.write(clase + "\n")

    with open(labels_es, "w", encoding="utf-8") as f:
        for clase in class_names:
            f.write(traducciones.get(clase, clase) + "\n")

    with open(labels_json, "w", encoding="utf-8") as f:
        json.dump(
            {
                "labels_en": class_names, # Mantiene los nombres de carpeta originales
                "labels_es": [traducciones.get(clase, clase) for clase in class_names]
            },
            f,
            indent=4,
            ensure_ascii=False
        )

    print("\nEtiquetas guardadas:")
    print(labels_en)
    print(labels_es)
    print(labels_json)

# PREPARAR DATASETS

def preparar_datasets(train_ds, valid_ds, test_ds):
    AUTOTUNE = tf.data.AUTOTUNE

    data_augmentation = tf.keras.Sequential(
        [
            tf.keras.layers.RandomFlip("horizontal"),
            tf.keras.layers.RandomRotation(0.08),
            tf.keras.layers.RandomZoom(0.12),
            tf.keras.layers.RandomContrast(0.15),
        ],
        name="aumento_datos"
    )

    train_ds = train_ds.map(
        lambda imagenes, etiquetas: (
            data_augmentation(imagenes, training=True),
            etiquetas
        ),
        num_parallel_calls=AUTOTUNE
    )

    train_ds = train_ds.prefetch(AUTOTUNE)
    valid_ds = valid_ds.prefetch(AUTOTUNE)
    test_ds = test_ds.prefetch(AUTOTUNE)

    return train_ds, valid_ds, test_ds

# CREAR MODELO CNN

def crear_modelo(num_clases):
    entrada = tf.keras.Input(
        shape=(IMG_SIZE[0], IMG_SIZE[1], 3),
        name="imagen"
    )

    x = tf.keras.layers.Rescaling(
        scale=1.0 / 127.5,
        offset=-1.0,
        name="normalizacion_mobilenet"
    )(entrada)

    modelo_base = tf.keras.applications.MobileNetV2(
        input_shape=(IMG_SIZE[0], IMG_SIZE[1], 3),
        include_top=False,
        weights="imagenet"
    )

    modelo_base.trainable = False

    x = modelo_base(x, training=False)
    x = tf.keras.layers.GlobalAveragePooling2D(name="promedio_global")(x)
    x = tf.keras.layers.Dropout(0.35, name="dropout")(x)

    salida = tf.keras.layers.Dense(
        num_clases,
        activation="softmax",
        name="clasificacion_residuos"
    )(x)

    modelo = tf.keras.Model(
        inputs=entrada,
        outputs=salida,
        name="EcoLambda_MobileNetV2"
    )

    return modelo, modelo_base

# ENTRENAR MODELO

def entrenar_modelo(modelo, modelo_base, train_ds, valid_ds):
    checkpoint_path = OUTPUT_DIR / "mejor_modelo.keras"

    callbacks = [
        tf.keras.callbacks.ModelCheckpoint(
            filepath=checkpoint_path,
            monitor="val_accuracy",
            save_best_only=True,
            mode="max",
            verbose=1
        ),
        tf.keras.callbacks.EarlyStopping(
            monitor="val_loss",
            patience=5,
            restore_best_weights=True,
            verbose=1
        ),
        tf.keras.callbacks.ReduceLROnPlateau(
            monitor="val_loss",
            factor=0.3,
            patience=2,
            min_lr=1e-7,
            verbose=1
        )
    ]

    print("\n==============================")
    print("FASE 1: Entrenando la cabeza del modelo")
    print("==============================")

    modelo.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=LEARNING_RATE_CABECERA),
        loss="categorical_crossentropy",
        metrics=["accuracy"]
    )

    historial_1 = modelo.fit(
        train_ds,
        validation_data=valid_ds,
        epochs=EPOCHS_CABECERA,
        callbacks=callbacks
    )

    print("\n==============================")
    print("FASE 2: Ajuste fino de MobileNetV2")
    print("==============================")

    modelo_base.trainable = True

    for capa in modelo_base.layers[:-30]:
        capa.trainable = False

    for capa in modelo_base.layers:
        if isinstance(capa, tf.keras.layers.BatchNormalization):
            capa.trainable = False

    modelo.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=LEARNING_RATE_AJUSTE_FINO),
        loss="categorical_crossentropy",
        metrics=["accuracy"]
    )

    historial_2 = modelo.fit(
        train_ds,
        validation_data=valid_ds,
        epochs=EPOCHS_AJUSTE_FINO,
        callbacks=callbacks
    )

    return historial_1, historial_2

# EVALUAR MODELO

def evaluar_modelo(modelo, test_ds, class_names):
    print("\n==============================")
    print("EVALUACIÓN FINAL CON TEST")
    print("==============================")

    loss, accuracy = modelo.evaluate(test_ds)

    print(f"\nLoss final: {loss:.4f}")
    print(f"Accuracy final: {accuracy:.4f}")

    y_true = []
    y_pred = []

    for imagenes, etiquetas in test_ds:
        predicciones = modelo.predict(imagenes, verbose=0)

        y_true.extend(np.argmax(etiquetas.numpy(), axis=1))
        y_pred.extend(np.argmax(predicciones, axis=1))

    reporte = classification_report(
        y_true,
        y_pred,
        target_names=class_names
    )

    matriz = confusion_matrix(y_true, y_pred)

    print("\nReporte de clasificación:")
    print(reporte)

    print("\nMatriz de confusión:")
    print(matriz)

    with open(OUTPUT_DIR / "reporte_clasificacion.txt", "w", encoding="utf-8") as f:
        f.write("REPORTE DE CLASIFICACIÓN\n\n")
        f.write(reporte)
        f.write("\n\nMATRIZ DE CONFUSIÓN\n")
        f.write(str(matriz))
        f.write(f"\n\nAccuracy final: {accuracy:.4f}")
        f.write(f"\nLoss final: {loss:.4f}")

    np.savetxt(
        OUTPUT_DIR / "matriz_confusion.csv",
        matriz,
        delimiter=",",
        fmt="%d"
    )

    print("\nReporte guardado en:")
    print(OUTPUT_DIR / "reporte_clasificacion.txt")
    print(OUTPUT_DIR / "matriz_confusion.csv")

# EXPORTAR A TFLITE FLOAT16

def exportar_tflite_float16(modelo):
    print("\n==============================")
    print("EXPORTANDO MODELO TFLITE FLOAT16")
    print("==============================")

    converter = tf.lite.TFLiteConverter.from_keras_model(modelo)

    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.target_spec.supported_types = [tf.float16]

    modelo_tflite = converter.convert()

    ruta_tflite = OUTPUT_DIR / "modelo_residuos_float16.tflite"

    with open(ruta_tflite, "wb") as f:
        f.write(modelo_tflite)

    print(f"Modelo TFLite float16 guardado en: {ruta_tflite}")

# EXPORTAR A TFLITE INT8 OPCIONAL

def exportar_tflite_int8(modelo):
    print("\n==============================")
    print("EXPORTANDO MODELO TFLITE INT8")
    print("==============================")

    representative_ds = tf.keras.utils.image_dataset_from_directory(
        DATASET_DIR,
        validation_split=0.2, # Solo usamos el subconjunto de train para no filtrar datos de test
        subset="training",
        seed=SEED,
        image_size=IMG_SIZE,
        batch_size=1,
        label_mode=None,
        shuffle=True
    )

    def representative_data_gen():
        for imagen in representative_ds.take(300):
            yield [tf.cast(imagen, tf.float32)]

    converter = tf.lite.TFLiteConverter.from_keras_model(modelo)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.representative_dataset = representative_data_gen

    converter.target_spec.supported_ops = [
        tf.lite.OpsSet.TFLITE_BUILTINS_INT8
    ]

    converter.inference_input_type = tf.uint8
    converter.inference_output_type = tf.uint8

    modelo_tflite_int8 = converter.convert()

    ruta_tflite = OUTPUT_DIR / "modelo_residuos_int8.tflite"

    with open(ruta_tflite, "wb") as f:
        f.write(modelo_tflite_int8)

    print(f"Modelo TFLite int8 guardado en: {ruta_tflite}")

# PROBAR MODELO TFLITE FLOAT16

def probar_tflite_float16(test_ds, class_names):
    print("\n==============================")
    print("PROBANDO MODELO TFLITE FLOAT16")
    print("==============================")

    ruta_tflite = OUTPUT_DIR / "modelo_residuos_float16.tflite"

    interpreter = tf.lite.Interpreter(model_path=str(ruta_tflite))
    interpreter.allocate_tensors()

    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    print("Entrada TFLite:")
    print(input_details)

    print("Salida TFLite:")
    print(output_details)

    for imagenes, etiquetas in test_ds.take(1):
        imagen = imagenes[0:1].numpy().astype(np.float32)

        interpreter.set_tensor(input_details[0]["index"], imagen)
        interpreter.invoke()

        salida = interpreter.get_tensor(output_details[0]["index"])[0]

        clase_predicha = int(np.argmax(salida))
        confianza = float(np.max(salida))

        clase_real = int(np.argmax(etiquetas[0].numpy()))

        print("\nPrueba rápida TFLite:")
        print(f"Clase real     : {class_names[clase_real]}")
        print(f"Clase predicha : {class_names[clase_predicha]}")
        print(f"Confianza      : {confianza:.4f}")

        break

# PROGRAMA PRINCIPAL

def main():
    validar_directorios()

    train_ds, valid_ds, test_ds, class_names = cargar_datasets()

    guardar_etiquetas(class_names)

    train_ds, valid_ds, test_ds = preparar_datasets(
        train_ds,
        valid_ds,
        test_ds
    )

    modelo, modelo_base = crear_modelo(
        num_clases=len(class_names)
    )

    modelo.summary()

    entrenar_modelo(
        modelo=modelo,
        modelo_base=modelo_base,
        train_ds=train_ds,
        valid_ds=valid_ds
    )

    evaluar_modelo(
        modelo=modelo,
        test_ds=test_ds,
        class_names=class_names
    )

    ruta_modelo_keras = OUTPUT_DIR / "modelo_residuos.keras"
    modelo.save(ruta_modelo_keras)

    print(f"\nModelo Keras guardado en: {ruta_modelo_keras}")

    exportar_tflite_float16(modelo)

    exportar_tflite_int8(modelo)

    probar_tflite_float16(
        test_ds=test_ds,
        class_names=class_names
    )

    print("\nProceso terminado correctamente.")
    print(f"Archivos generados en la carpeta: {OUTPUT_DIR.resolve()}")

if __name__ == "__main__":
    main()