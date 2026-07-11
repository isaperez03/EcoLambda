package com.example.lambdag.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.ByteBufferExtractor
import com.google.mediapipe.tasks.components.containers.NormalizedKeypoint
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.interactivesegmenter.InteractiveSegmenter
import com.google.mediapipe.tasks.vision.interactivesegmenter.InteractiveSegmenter.RegionOfInterest
import java.nio.ByteBuffer

class RemovedorFondo(
    private val context: Context
) {

    private val nombreModelo = "magic_touch.tflite"
    private var segmentador: InteractiveSegmenter? = null

    init {
        inicializarSegmentador()
    }

    private fun inicializarSegmentador() {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(nombreModelo)
            .build()

        val opciones = InteractiveSegmenter.InteractiveSegmenterOptions.builder()
            .setBaseOptions(baseOptions)
            .setOutputCategoryMask(true)
            .setOutputConfidenceMasks(false)
            .build()

        segmentador = InteractiveSegmenter.createFromOptions(
            context,
            opciones
        )
    }

    fun eliminarFondo(
        bitmapOriginal: Bitmap,
        fondoNegro: Boolean = true
    ): Bitmap {
        val bitmapEntrada = bitmapOriginal.copy(
            Bitmap.Config.ARGB_8888,
            true
        )

        val imagenMediaPipe = BitmapImageBuilder(bitmapEntrada).build()

        val puntoCentralX = bitmapEntrada.width * 0.5f
        val puntoCentralY = bitmapEntrada.height * 0.5f

        val regionInteres = RegionOfInterest.create(
            NormalizedKeypoint.create(
                puntoCentralX,
                puntoCentralY
            )
        )

        val resultadoSegmentacion = segmentador?.segment(
            imagenMediaPipe,
            regionInteres
        ) ?: return bitmapEntrada

        val mascara = resultadoSegmentacion.categoryMask().orElse(null)
            ?: return bitmapEntrada

        val bufferMascara = ByteBufferExtractor.extract(mascara)

        return aplicarMascaraConObjetoCentral(
            bitmapOriginal = bitmapEntrada,
            bufferMascara = bufferMascara,
            anchoMascara = mascara.width,
            altoMascara = mascara.height,
            fondoNegro = fondoNegro
        )
    }

    private fun aplicarMascaraConObjetoCentral(
        bitmapOriginal: Bitmap,
        bufferMascara: ByteBuffer,
        anchoMascara: Int,
        altoMascara: Int,
        fondoNegro: Boolean
    ): Bitmap {
        val bitmapEscalado = Bitmap.createScaledBitmap(
            bitmapOriginal,
            anchoMascara,
            altoMascara,
            true
        )

        val pixelesOriginales = IntArray(anchoMascara * altoMascara)

        bitmapEscalado.getPixels(
            pixelesOriginales,
            0,
            anchoMascara,
            0,
            0,
            anchoMascara,
            altoMascara
        )

        val valoresMascara = ByteArray(anchoMascara * altoMascara)

        bufferMascara.rewind()
        bufferMascara.get(valoresMascara)

        val centroX = anchoMascara / 2
        val centroY = altoMascara / 2
        val indiceCentro = centroY * anchoMascara + centroX
        val valorObjeto = valoresMascara[indiceCentro].toInt() and 0xFF

        val pixelesFinales = IntArray(anchoMascara * altoMascara)

        var pixelesObjeto = 0

        for (i in pixelesFinales.indices) {
            val valorActual = valoresMascara[i].toInt() and 0xFF
            val perteneceAlObjeto = valorActual == valorObjeto

            if (perteneceAlObjeto) {
                pixelesObjeto++
                pixelesFinales[i] = pixelesOriginales[i]
            } else {
                pixelesFinales[i] = if (fondoNegro) {
                    Color.BLACK
                } else {
                    Color.TRANSPARENT
                }
            }
        }

        val totalPixeles = anchoMascara * altoMascara
        val porcentajeObjeto = pixelesObjeto.toFloat() / totalPixeles.toFloat()

        if (porcentajeObjeto > 0.90f) {
            for (i in pixelesFinales.indices) {
                val valorActual = valoresMascara[i].toInt() and 0xFF
                val perteneceAlObjetoInvertido = valorActual != valorObjeto

                pixelesFinales[i] = if (perteneceAlObjetoInvertido) {
                    pixelesOriginales[i]
                } else {
                    if (fondoNegro) Color.BLACK else Color.TRANSPARENT
                }
            }
        }

        val bitmapSinFondo = Bitmap.createBitmap(
            anchoMascara,
            altoMascara,
            Bitmap.Config.ARGB_8888
        )

        bitmapSinFondo.setPixels(
            pixelesFinales,
            0,
            anchoMascara,
            0,
            0,
            anchoMascara,
            altoMascara
        )

        return bitmapSinFondo
    }

    fun cerrar() {
        segmentador?.close()
        segmentador = null
    }
}