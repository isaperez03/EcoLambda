package com.example.lambdag.model

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HistorialRepository {

    private val auth = Firebase.auth
    private val database = Firebase.database.reference
    private val listenersActivos = mutableMapOf<ValueEventListener, Query>()

    private fun obtenerUid(): String? {
        return auth.currentUser?.uid
    }

    fun guardarAnalisis(
        bitmap: Bitmap,
        resultado: ResultadoClasificacion,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        Log.e("HistorialFirebase", "Intentando guardar análisis...")

        val uid = obtenerUid()

        if (uid == null) {
            Log.e("HistorialFirebase", "No hay usuario autenticado.")
            onError(Exception("No hay usuario autenticado."))
            return
        }

        val referencia = database
            .child("analisis_residuos")
            .child(uid)
            .push()

        val id = referencia.key ?: System.currentTimeMillis().toString()

        val imagenBase64 = ImagenBase64Utils.bitmapABase64(
            bitmap = bitmap,
            maxWidth = 500,
            calidad = 50
        )

        val analisis = AnalisisResiduo(
            id = id,
            uid = uid,
            etiqueta = resultado.etiqueta,
            categoriaGeneral = resultado.categoriaGeneral,
            confianza = resultado.confianza.toDouble(),
            recomendacion = resultado.recomendacion,
            imagenBase64 = imagenBase64,
            fechaHora = System.currentTimeMillis()
        )

        referencia
            .setValue(analisis)
            .addOnSuccessListener {
                Log.e("HistorialFirebase", "Análisis guardado correctamente en Firebase.")
                onSuccess()
            }
            .addOnFailureListener { error ->
                Log.e("HistorialFirebase", "Error al guardar análisis: ${error.message}", error)
                onError(error)
            }
    }

    fun escucharUltimoAnalisis(
        onData: (AnalisisResiduo?) -> Unit,
        onError: (DatabaseError) -> Unit = {}
    ): ValueEventListener? {
        val uid = obtenerUid()

        if (uid == null) {
            Log.e("HistorialFirebase", "No se puede leer último análisis. UID null.")
            return null
        }

        val query = database
            .child("analisis_residuos")
            .child(uid)
            .orderByChild("fechaHora")
            .limitToLast(1)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ultimo = snapshot.children
                    .mapNotNull { item ->
                        item.getValue(AnalisisResiduo::class.java)
                    }
                    .maxByOrNull { analisis ->
                        analisis.fechaHora
                    }

                Log.e("HistorialFirebase", "Último análisis leído: ${ultimo?.etiqueta}")
                onData(ultimo)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HistorialFirebase", "Error leyendo último análisis: ${error.message}")
                onError(error)
            }
        }

        query.addValueEventListener(listener)
        listenersActivos[listener] = query

        return listener
    }

    fun escucharHistorial(
        onData: (List<AnalisisResiduo>) -> Unit,
        onError: (DatabaseError) -> Unit = {}
    ): ValueEventListener? {
        val uid = obtenerUid()

        if (uid == null) {
            Log.e("HistorialFirebase", "No se puede leer historial. UID null.")
            return null
        }

        val query = database
            .child("analisis_residuos")
            .child(uid)
            .orderByChild("fechaHora")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = snapshot.children
                    .mapNotNull { item ->
                        item.getValue(AnalisisResiduo::class.java)
                    }
                    .sortedByDescending { analisis ->
                        analisis.fechaHora
                    }

                Log.e("HistorialFirebase", "Historial leído. Total: ${lista.size}")
                onData(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HistorialFirebase", "Error leyendo historial: ${error.message}")
                onError(error)
            }
        }

        query.addValueEventListener(listener)
        listenersActivos[listener] = query

        return listener
    }

    fun borrarAnalisis(
        idAnalisis: String,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val uid = obtenerUid()

        if (uid == null) {
            onError(Exception("No hay usuario autenticado."))
            return
        }

        if (idAnalisis.isBlank()) {
            onError(Exception("ID de análisis vacío."))
            return
        }

        database
            .child("analisis_residuos")
            .child(uid)
            .child(idAnalisis)
            .removeValue()
            .addOnSuccessListener {
                Log.e("HistorialFirebase", "Registro eliminado correctamente.")
                onSuccess()
            }
            .addOnFailureListener { error ->
                Log.e("HistorialFirebase", "Error al eliminar registro: ${error.message}", error)
                onError(error)
            }
    }

    fun borrarTodoHistorial(
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        val uid = obtenerUid()

        if (uid == null) {
            onError(Exception("No hay usuario autenticado."))
            return
        }

        database
            .child("analisis_residuos")
            .child(uid)
            .removeValue()
            .addOnSuccessListener {
                Log.e("HistorialFirebase", "Historial eliminado correctamente.")
                onSuccess()
            }
            .addOnFailureListener { error ->
                Log.e("HistorialFirebase", "Error al eliminar historial: ${error.message}", error)
                onError(error)
            }
    }

    fun detenerEscucha(listener: ValueEventListener) {
        val query = listenersActivos[listener]

        if (query != null) {
            query.removeEventListener(listener)
            listenersActivos.remove(listener)
        }
    }
}