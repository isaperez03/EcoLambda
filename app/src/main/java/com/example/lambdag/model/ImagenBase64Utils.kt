package com.example.lambdag.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImagenBase64Utils {

    fun bitmapABase64(
        bitmap: Bitmap,
        maxWidth: Int = 600,
        calidad: Int = 60
    ): String {
        val bitmapReducido = reducirBitmap(
            bitmap = bitmap,
            maxWidth = maxWidth
        )

        val outputStream = ByteArrayOutputStream()

        bitmapReducido.compress(
            Bitmap.CompressFormat.JPEG,
            calidad,
            outputStream
        )

        val bytes = outputStream.toByteArray()

        return Base64.encodeToString(
            bytes,
            Base64.NO_WRAP
        )
    }

    fun base64ABitmap(base64: String): Bitmap? {
        return try {
            val bytes = Base64.decode(
                base64,
                Base64.NO_WRAP
            )

            BitmapFactory.decodeByteArray(
                bytes,
                0,
                bytes.size
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun reducirBitmap(
        bitmap: Bitmap,
        maxWidth: Int
    ): Bitmap {
        if (bitmap.width <= maxWidth) {
            return bitmap
        }

        val relacion = maxWidth.toFloat() / bitmap.width.toFloat()
        val nuevoAlto = (bitmap.height * relacion).toInt()

        return Bitmap.createScaledBitmap(
            bitmap,
            maxWidth,
            nuevoAlto,
            true
        )
    }
}