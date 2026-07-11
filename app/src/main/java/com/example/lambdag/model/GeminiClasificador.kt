package com.example.lambdag.model

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.lambdag.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiClasificador {

    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val modelo = "gemini-2.5-flash"

    private val cliente = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun clasificarResiduo(
        bitmap: Bitmap
    ): ResultadoClasificacion? = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                Log.e("GeminiClasificador", "GEMINI_API_KEY está vacía.")
                return@withContext null
            }

            val imagenBase64 = convertirBitmapABase64(bitmap)

            val prompt = """
                Analiza la imagen y clasifica el objeto visible según su material principal.

                Etiquetas permitidas:
                - Cartón
                - Vidrio
                - Metal
                - Orgánico
                - Papel
                - Plástico
                - Basura no reciclable
                - No es residuo

                Instrucciones importantes:
                - No clasifiques únicamente basura; también clasifica objetos cotidianos por su material principal.
                - Si aparece un teclado, mouse, control, cargador, botella, envase, juguete, carcasa, accesorio, herramienta pequeña u objeto común, clasifícalo por el material visible predominante.
                - Si el objeto parece de plástico, responde "Plástico".
                - Si el objeto parece metálico, responde "Metal".
                - Si parece papel, responde "Papel".
                - Si parece cartón, responde "Cartón".
                - Si parece vidrio, responde "Vidrio".
                - Si parece comida, cáscara, planta o residuo natural, responde "Orgánico".
                - Si es una mezcla difícil de reciclar o no se puede identificar un material claro, responde "Basura no reciclable".

                Usa "No es residuo" únicamente en estos casos:
                - Personas o partes del cuerpo, por ejemplo mano, brazo, rostro o piel.
                - Animales.
                - Pantallas, televisores, monitores o celulares mostrando pantalla.
                - Mesas, escritorios, sillas o muebles grandes.
                - Telas, ropa, cobijas, cortinas o materiales textiles.
                - Fondo vacío o imagen donde no se distingue ningún objeto útil para clasificar.

                Reglas:
                - Si aparece una mano sosteniendo un objeto, ignora la mano y clasifica el objeto.
                - Si solo aparece una mano o parte del cuerpo, responde "No es residuo".
                - No respondas explicaciones fuera del JSON.
                - Devuelve únicamente JSON válido.

                Formato obligatorio:
                {
                  "etiqueta": "Plástico",
                  "categoriaGeneral": "Inorgánico reciclable",
                  "confianza": 0.85,
                  "recomendacion": "Deposítalo en residuos inorgánicos reciclables si está limpio y seco. Si forma parte de un dispositivo electrónico, llévalo a un centro de acopio electrónico."
                }
            """.trimIndent()

            val bodyJson = JSONObject().apply {
                put(
                    "contents",
                    JSONArray().put(
                        JSONObject().put(
                            "parts",
                            JSONArray()
                                .put(
                                    JSONObject().put(
                                        "text",
                                        prompt
                                    )
                                )
                                .put(
                                    JSONObject().put(
                                        "inline_data",
                                        JSONObject()
                                            .put("mime_type", "image/jpeg")
                                            .put("data", imagenBase64)
                                    )
                                )
                        )
                    )
                )

                put(
                    "generationConfig",
                    JSONObject()
                        .put("temperature", 0.0)
                        .put("topP", 0.5)
                        .put("maxOutputTokens", 512)
                        .put("responseMimeType", "application/json")
                        .put(
                            "thinkingConfig",
                            JSONObject()
                                .put("thinkingBudget", 0)
                        )
                )
            }

            val url =
                "https://generativelanguage.googleapis.com/v1beta/models/$modelo:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(
                    bodyJson.toString()
                        .toRequestBody("application/json".toMediaType())
                )
                .build()

            val response = cliente.newCall(request).execute()
            val responseBody = response.body?.string()

            Log.d("GeminiClasificador", "Código HTTP: ${response.code}")
            Log.d("GeminiClasificador", "Respuesta: $responseBody")

            if (!response.isSuccessful) {
                response.close()
                return@withContext null
            }

            response.close()

            if (responseBody.isNullOrBlank()) {
                return@withContext null
            }

            val textoGemini = extraerTextoGemini(responseBody)
                ?: return@withContext null

            val jsonLimpio = limpiarJson(textoGemini)

            return@withContext convertirJsonAResultado(jsonLimpio)

        } catch (e: Exception) {
            Log.e("GeminiClasificador", "Error usando Gemini", e)
            return@withContext null
        }
    }

    private fun convertirBitmapABase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()

        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            85,
            stream
        )

        return Base64.encodeToString(
            stream.toByteArray(),
            Base64.NO_WRAP
        )
    }

    private fun extraerTextoGemini(responseBody: String): String? {
        val json = JSONObject(responseBody)

        val candidates = json.optJSONArray("candidates")
            ?: return null

        if (candidates.length() == 0) {
            return null
        }

        val content = candidates
            .getJSONObject(0)
            .optJSONObject("content")
            ?: return null

        val parts = content.optJSONArray("parts")
            ?: return null

        if (parts.length() == 0) {
            return null
        }

        return parts
            .getJSONObject(0)
            .optString("text")
    }

    private fun limpiarJson(texto: String): String {
        return texto
            .replace("```json", "")
            .replace("```", "")
            .trim()
    }

    private fun convertirJsonAResultado(jsonTexto: String): ResultadoClasificacion {
        val json = JSONObject(jsonTexto)

        val etiqueta = json.optString(
            "etiqueta",
            "Basura no reciclable"
        )

        val categoriaGeneral = json.optString(
            "categoriaGeneral",
            obtenerCategoriaGeneral(etiqueta)
        )

        val confianza = json.optDouble(
            "confianza",
            0.0
        ).toFloat().coerceIn(0f, 1f)

        val recomendacion = json.optString(
            "recomendacion",
            obtenerRecomendacion(etiqueta)
        )

        return ResultadoClasificacion(
            etiqueta = etiqueta,
            confianza = confianza,
            indiceClase = -1,
            categoriaGeneral = categoriaGeneral,
            recomendacion = recomendacion
        )
    }

    private fun obtenerCategoriaGeneral(etiqueta: String): String {
        val e = etiqueta.lowercase()

        return when {
            e.contains("no es residuo") ||
                    e.contains("no clasificable") ||
                    e.contains("no identificado") -> {
                "No clasificable"
            }

            e.contains("orgánico") ||
                    e.contains("organico") -> {
                "Orgánico"
            }

            e.contains("papel") ||
                    e.contains("cartón") ||
                    e.contains("carton") -> {
                "Papel/Cartón"
            }

            e.contains("plástico") ||
                    e.contains("plastico") ||
                    e.contains("vidrio") ||
                    e.contains("metal") -> {
                "Inorgánico reciclable"
            }

            e.contains("basura") -> {
                "Basura no reciclable"
            }

            else -> {
                "Residuo no identificado"
            }
        }
    }

    private fun obtenerRecomendacion(etiqueta: String): String {
        val e = etiqueta.lowercase()

        return when {
            e.contains("no es residuo") ||
                    e.contains("no clasificable") ||
                    e.contains("no identificado") -> {
                "La imagen no corresponde a un objeto clasificable para separación de residuos."
            }

            e.contains("orgánico") ||
                    e.contains("organico") -> {
                "Deposítalo en residuos orgánicos. Puede aprovecharse para composta si no contiene plástico, vidrio o metal."
            }

            e.contains("papel") -> {
                "Deposítalo limpio y seco en papel/cartón. Evita mezclarlo con comida, grasa o líquidos."
            }

            e.contains("cartón") ||
                    e.contains("carton") -> {
                "Dóblalo o aplástalo antes de reciclarlo. Debe estar limpio y seco para poder aprovecharse."
            }

            e.contains("plástico") ||
                    e.contains("plastico") -> {
                "Deposítalo en residuos inorgánicos reciclables si está limpio y seco. Si forma parte de un dispositivo electrónico, llévalo a un centro de acopio electrónico."
            }

            e.contains("vidrio") -> {
                "Deposítalo en reciclaje de vidrio. Si está roto, envuélvelo para evitar accidentes."
            }

            e.contains("metal") -> {
                "Deposítalo en inorgánicos reciclables. Si pertenece a un aparato electrónico, llévalo a un centro de acopio especializado."
            }

            e.contains("basura") -> {
                "Deposítalo en basura no reciclable. Evita mezclarlo con residuos orgánicos o materiales reciclables."
            }

            else -> {
                "No se pudo identificar con seguridad. Intenta tomar otra foto con mejor iluminación y el objeto centrado."
            }
        }
    }
}