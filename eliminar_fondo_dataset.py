import io
import random
from pathlib import Path

from PIL import Image
from rembg import remove, new_session
from tqdm import tqdm


DATASET_ORIGINAL = Path(r"C:\CDE\DATASET_FINAL")
DATASET_SIN_FONDO = Path(r"C:\CDE\DATASET_FINAL_NO_BG")

EXTENSIONES_VALIDAS = [".jpg", ".jpeg", ".png", ".webp", ".bmp"]

COLOR_FONDO = (0, 0, 0, 255)

SEED = 123
TRAIN_RATIO = 0.70
VALID_RATIO = 0.15
TEST_RATIO = 0.15


def es_imagen_valida(ruta_imagen: Path) -> bool:
    return ruta_imagen.suffix.lower() in EXTENSIONES_VALIDAS


def obtener_split(indice: int, total: int) -> str:
    proporcion = indice / total

    if proporcion < TRAIN_RATIO:
        return "train"

    if proporcion < TRAIN_RATIO + VALID_RATIO:
        return "valid"

    return "test"


def eliminar_fondo_imagen(
    ruta_origen: Path,
    ruta_destino: Path,
    session
) -> bool:
    try:
        with open(ruta_origen, "rb") as archivo:
            imagen_bytes = archivo.read()

        imagen_sin_fondo_bytes = remove(
            imagen_bytes,
            session=session
        )

        imagen_rgba = Image.open(
            io.BytesIO(imagen_sin_fondo_bytes)
        ).convert("RGBA")

        fondo = Image.new(
            "RGBA",
            imagen_rgba.size,
            COLOR_FONDO
        )

        fondo.paste(
            imagen_rgba,
            (0, 0),
            imagen_rgba
        )

        imagen_final = fondo.convert("RGB")

        ruta_destino.parent.mkdir(
            parents=True,
            exist_ok=True
        )

        imagen_final.save(
            ruta_destino,
            format="JPEG",
            quality=95
        )

        return True

    except Exception as error:
        print(f"\nError procesando: {ruta_origen}")
        print(f"Detalle: {error}")
        return False


def procesar_dataset():
    if not DATASET_ORIGINAL.exists():
        raise FileNotFoundError(
            f"No existe el dataset original: {DATASET_ORIGINAL}"
        )

    random.seed(SEED)

    print("Dataset original:")
    print(DATASET_ORIGINAL)

    print("\nDataset sin fondo:")
    print(DATASET_SIN_FONDO)

    clases = [
        carpeta for carpeta in DATASET_ORIGINAL.iterdir()
        if carpeta.is_dir()
    ]

    if not clases:
        raise FileNotFoundError(
            "No se encontraron carpetas de clases dentro del dataset original."
        )

    print("\nClases encontradas:")
    for clase in clases:
        print(f"- {clase.name}")

    print("\nCargando modelo de rembg...")
    session = new_session("u2net")

    total_procesadas = 0
    total_omitidas = 0
    total_errores = 0

    for carpeta_clase_origen in clases:
        nombre_clase = carpeta_clase_origen.name

        imagenes = [
            archivo for archivo in carpeta_clase_origen.iterdir()
            if archivo.is_file() and es_imagen_valida(archivo)
        ]

        if not imagenes:
            print(f"\nLa clase {nombre_clase} no tiene imágenes válidas.")
            continue

        random.shuffle(imagenes)

        total_imagenes = len(imagenes)

        print(f"\nProcesando clase: {nombre_clase}")
        print(f"Total de imágenes: {total_imagenes}")

        for indice, ruta_imagen in enumerate(
            tqdm(
                imagenes,
                desc=nombre_clase,
                unit="img"
            )
        ):
            split = obtener_split(
                indice=indice,
                total=total_imagenes
            )

            carpeta_destino = DATASET_SIN_FONDO / split / nombre_clase
            ruta_destino = carpeta_destino / f"{ruta_imagen.stem}.jpg"

            if ruta_destino.exists():
                total_omitidas += 1
                continue

            resultado = eliminar_fondo_imagen(
                ruta_origen=ruta_imagen,
                ruta_destino=ruta_destino,
                session=session
            )

            if resultado:
                total_procesadas += 1
            else:
                total_errores += 1

    print("\nProceso terminado.")
    print(f"Imágenes procesadas: {total_procesadas}")
    print(f"Imágenes omitidas porque ya existían: {total_omitidas}")
    print(f"Errores: {total_errores}")
    print(f"Dataset generado en: {DATASET_SIN_FONDO}")

    print("\nEstructura generada:")
    print(DATASET_SIN_FONDO / "train")
    print(DATASET_SIN_FONDO / "valid")
    print(DATASET_SIN_FONDO / "test")


if __name__ == "__main__":
    procesar_dataset()