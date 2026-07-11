package com.example.lambdag.model

data class AnalisisResiduo(
    val id: String = "",
    val uid: String = "",
    val etiqueta: String = "",
    val categoriaGeneral: String = "",
    val confianza: Double = 0.0,
    val recomendacion: String = "",
    val imagenBase64: String = "",
    val fechaHora: Long = 0L
)