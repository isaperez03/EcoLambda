package com.example.lambdag.model

import android.graphics.Bitmap

object EstadoClasificacion {
    var imagenBitmap: Bitmap? = null
    var resultado: ResultadoClasificacion? = null

    fun limpiar() {
        imagenBitmap = null
        resultado = null
    }
}