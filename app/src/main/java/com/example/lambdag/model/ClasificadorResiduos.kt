package com.example.lambdag.model

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

data class ResultadoClasificacion(
    val etiqueta: String,
    val confianza: Float,
    val indiceClase: Int,
    val categoriaGeneral: String,
    val recomendacion: String
)

class ClasificadorResiduos(
    private val context: Context
) {

    private val nombreModelo = "modelo_residuos_float16.tflite"
    private val nombreEtiquetas = "labels_es.txt"

    private val tamanoImagen = 224
    private val canales = 3

    private val interpreter: Interpreter
    private val etiquetas: List<String>

    init {
        val opciones = Interpreter.Options().apply {
            setNumThreads(4)
        }

        interpreter = Interpreter(cargarModelo(), opciones)
        etiquetas = cargarEtiquetas()
    }

    private fun cargarModelo(): ByteBuffer {
        val assetFileDescriptor = context.assets.openFd(nombreModelo)

        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel

        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.declaredLength
        )
    }

    private fun cargarEtiquetas(): List<String> {
        val etiquetasCargadas = mutableListOf<String>()

        context.assets.open(nombreEtiquetas).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var linea = reader.readLine()

                while (linea != null) {
                    if (linea.isNotBlank()) {
                        etiquetasCargadas.add(linea.trim())
                    }

                    linea = reader.readLine()
                }
            }
        }

        return etiquetasCargadas
    }

    fun clasificar(bitmapOriginal: Bitmap): ResultadoClasificacion {
        val bitmapRedimensionado = Bitmap.createScaledBitmap(
            bitmapOriginal,
            tamanoImagen,
            tamanoImagen,
            true
        )

        val entrada = convertirBitmapAByteBuffer(bitmapRedimensionado)

        val numeroClases = etiquetas.size

        val salida = Array(1) {
            FloatArray(numeroClases)
        }

        interpreter.run(entrada, salida)

        val probabilidades = salida[0]

        var indiceMayor = 0
        var confianzaMayor = probabilidades[0]

        for (i in probabilidades.indices) {
            if (probabilidades[i] > confianzaMayor) {
                confianzaMayor = probabilidades[i]
                indiceMayor = i
            }
        }

        val etiqueta = if (indiceMayor < etiquetas.size) {
            etiquetas[indiceMayor]
        } else {
            "Clase desconocida"
        }

        return ResultadoClasificacion(
            etiqueta = etiqueta,
            confianza = confianzaMayor,
            indiceClase = indiceMayor,
            categoriaGeneral = obtenerCategoriaGeneral(etiqueta),
            recomendacion = obtenerRecomendacion(etiqueta)
        )
    }

    private fun convertirBitmapAByteBuffer(bitmap: Bitmap): ByteBuffer {
        val bytesPorFloat = 4

        val byteBuffer = ByteBuffer.allocateDirect(
            1 * tamanoImagen * tamanoImagen * canales * bytesPorFloat
        )

        byteBuffer.order(ByteOrder.nativeOrder())

        val pixeles = IntArray(tamanoImagen * tamanoImagen)

        bitmap.getPixels(
            pixeles,
            0,
            bitmap.width,
            0,
            0,
            bitmap.width,
            bitmap.height
        )

        var indicePixel = 0

        for (y in 0 until tamanoImagen) {
            for (x in 0 until tamanoImagen) {
                val pixel = pixeles[indicePixel++]

                val rojo = (pixel shr 16 and 0xFF).toFloat()
                val verde = (pixel shr 8 and 0xFF).toFloat()
                val azul = (pixel and 0xFF).toFloat()

                /*
                 El modelo ya tiene la normalización dentro:
                 Rescaling(1.0 / 127.5, offset = -1.0)

                 Por eso aquí mandamos valores de 0 a 255.
                */
                byteBuffer.putFloat(rojo)
                byteBuffer.putFloat(verde)
                byteBuffer.putFloat(azul)
            }
        }

        return byteBuffer
    }

    private fun obtenerCategoriaGeneral(etiqueta: String): String {
        val etiquetaNormalizada = etiqueta.lowercase()

        return when {
            etiquetaNormalizada.contains("orgánico") ||
                    etiquetaNormalizada.contains("organico") ||
                    etiquetaNormalizada.contains("organic") -> {
                "Orgánico"
            }

            etiquetaNormalizada.contains("papel") ||
                    etiquetaNormalizada.contains("paper") ||
                    etiquetaNormalizada.contains("cartón") ||
                    etiquetaNormalizada.contains("carton") ||
                    etiquetaNormalizada.contains("cardboard") -> {
                "Papel/Cartón"
            }

            etiquetaNormalizada.contains("plástico") ||
                    etiquetaNormalizada.contains("plastico") ||
                    etiquetaNormalizada.contains("plastic") ||
                    etiquetaNormalizada.contains("vidrio") ||
                    etiquetaNormalizada.contains("glass") ||
                    etiquetaNormalizada.contains("metal") -> {
                "Inorgánico reciclable"
            }

            etiquetaNormalizada.contains("basura") ||
                    etiquetaNormalizada.contains("trash") -> {
                "Basura no reciclable"
            }

            else -> {
                "Residuo no identificado"
            }
        }
    }

    private fun obtenerRecomendacion(etiqueta: String): String {
        val etiquetaNormalizada = etiqueta.lowercase()

        return when {
            etiquetaNormalizada.contains("orgánico") ||
                    etiquetaNormalizada.contains("organico") ||
                    etiquetaNormalizada.contains("organic") -> {
                "Deposítalo en residuos orgánicos. Puede aprovecharse para composta si no contiene plástico, vidrio o metal."
            }

            etiquetaNormalizada.contains("papel") ||
                    etiquetaNormalizada.contains("paper") -> {
                "Deposítalo limpio y seco en papel/cartón. Evita mezclarlo con comida, grasa o líquidos."
            }

            etiquetaNormalizada.contains("cartón") ||
                    etiquetaNormalizada.contains("carton") ||
                    etiquetaNormalizada.contains("cardboard") -> {
                "Dóblalo o aplástalo antes de reciclarlo. Debe estar limpio y seco para poder aprovecharse."
            }

            etiquetaNormalizada.contains("plástico") ||
                    etiquetaNormalizada.contains("plastico") ||
                    etiquetaNormalizada.contains("plastic") -> {
                "Enjuágalo, aplástalo y deposítalo en residuos inorgánicos reciclables."
            }

            etiquetaNormalizada.contains("vidrio") ||
                    etiquetaNormalizada.contains("glass") -> {
                "Deposítalo en reciclaje de vidrio. Si está roto, envuélvelo para evitar accidentes."
            }

            etiquetaNormalizada.contains("metal") -> {
                "Enjuágalo y deposítalo en inorgánicos reciclables. Las latas pueden comprimirse para ahorrar espacio."
            }

            etiquetaNormalizada.contains("basura") ||
                    etiquetaNormalizada.contains("trash") -> {
                "Deposítalo en basura no reciclable. Evita mezclarlo con residuos orgánicos o reciclables."
            }

            else -> {
                "No se pudo identificar con seguridad. Intenta tomar otra foto con mejor iluminación y el residuo centrado."
            }
        }
    }

    fun cerrar() {
        interpreter.close()
    }
}