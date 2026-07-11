package com.example.lambdag.ui.history

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lambdag.model.AnalisisResiduo
import com.example.lambdag.model.HistorialRepository
import com.example.lambdag.model.ImagenBase64Utils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistorialScreen(
    onVolver: () -> Unit
) {
    val contexto = LocalContext.current
    val colorFondo = Color(0xFFA8BC84)
    val colorVerde = Color(0xFF2E7D32)

    val historialRepository = remember {
        HistorialRepository()
    }

    var historial by remember {
        mutableStateOf<List<AnalisisResiduo>>(emptyList())
    }

    var analisisAEliminar by remember {
        mutableStateOf<AnalisisResiduo?>(null)
    }

    var mostrarDialogoBorrarTodo by remember {
        mutableStateOf(false)
    }

    DisposableEffect(Unit) {
        val listener = historialRepository.escucharHistorial(
            onData = { lista ->
                historial = lista
            }
        )

        onDispose {
            if (listener != null) {
                historialRepository.detenerEscucha(listener)
            }
        }
    }

    val historialAnterior = if (historial.size > 1) {
        historial.drop(1)
    } else {
        emptyList()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorFondo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorFondo)
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ) {
            BarraSuperiorHistorial(
                onVolver = onVolver,
                puedeBorrarTodo = historial.isNotEmpty(),
                onBorrarTodo = {
                    mostrarDialogoBorrarTodo = true
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Historial",
                        tint = colorVerde,
                        modifier = Modifier.size(42.dp)
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Historial de análisis",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1F1F)
                        )

                        Text(
                            text = "Resultados guardados anteriormente",
                            fontSize = 14.sp,
                            color = Color(0xFF555555)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (historialAnterior.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Recycling,
                                contentDescription = "Sin historial",
                                tint = colorVerde,
                                modifier = Modifier.size(54.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Aún no hay análisis anteriores.",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F1F1F),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Cuando realices más clasificaciones, aparecerán aquí.",
                                fontSize = 14.sp,
                                color = Color(0xFF555555),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = historialAnterior,
                        key = { analisis ->
                            analisis.id
                        }
                    ) { analisis ->
                        TarjetaAnalisis(
                            analisis = analisis,
                            onEliminar = {
                                analisisAEliminar = analisis
                            }
                        )
                    }
                }
            }
        }
    }

    analisisAEliminar?.let { analisis ->
        AlertDialog(
            onDismissRequest = {
                analisisAEliminar = null
            },
            title = {
                Text(
                    text = "Eliminar registro",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Seguro que deseas eliminar este análisis de ${analisis.etiqueta}?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        historialRepository.borrarAnalisis(
                            idAnalisis = analisis.id,
                            onSuccess = {
                                Toast.makeText(
                                    contexto,
                                    "Registro eliminado",
                                    Toast.LENGTH_SHORT
                                ).show()

                                analisisAEliminar = null
                            },
                            onError = { error ->
                                Toast.makeText(
                                    contexto,
                                    "Error al eliminar: ${error.message}",
                                    Toast.LENGTH_LONG
                                ).show()

                                analisisAEliminar = null
                            }
                        )
                    }
                ) {
                    Text(text = "Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        analisisAEliminar = null
                    }
                ) {
                    Text(text = "Cancelar")
                }
            }
        )
    }

    if (mostrarDialogoBorrarTodo) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoBorrarTodo = false
            },
            title = {
                Text(
                    text = "Borrar historial",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Esta acción eliminará todos los análisis guardados, incluyendo el último resultado. ¿Deseas continuar?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        historialRepository.borrarTodoHistorial(
                            onSuccess = {
                                Toast.makeText(
                                    contexto,
                                    "Historial eliminado",
                                    Toast.LENGTH_SHORT
                                ).show()

                                mostrarDialogoBorrarTodo = false
                            },
                            onError = { error ->
                                Toast.makeText(
                                    contexto,
                                    "Error al eliminar historial: ${error.message}",
                                    Toast.LENGTH_LONG
                                ).show()

                                mostrarDialogoBorrarTodo = false
                            }
                        )
                    }
                ) {
                    Text(text = "Eliminar todo")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoBorrarTodo = false
                    }
                ) {
                    Text(text = "Cancelar")
                }
            }
        )
    }
}

@Composable
private fun BarraSuperiorHistorial(
    onVolver: () -> Unit,
    puedeBorrarTodo: Boolean,
    onBorrarTodo: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onVolver,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "Historial",
            modifier = Modifier.align(Alignment.Center),
            color = Color(0xFF1F1F1F),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        if (puedeBorrarTodo) {
            IconButton(
                onClick = onBorrarTodo,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Borrar historial",
                    tint = Color(0xFFB00020),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun TarjetaAnalisis(
    analisis: AnalisisResiduo,
    onEliminar: () -> Unit
) {
    val bitmap = ImagenBase64Utils.base64ABitmap(
        analisis.imagenBase64
    )

    val porcentaje = analisis.confianza * 100.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .background(
                            color = Color(0xFFF4F4F4),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Imagen del análisis",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(5.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Sin imagen",
                            tint = Color.Gray,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = analisis.etiqueta,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = analisis.categoriaGeneral,
                        fontSize = 14.sp,
                        color = Color(0xFF555555)
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = formatearFecha(analisis.fechaHora),
                        fontSize = 12.sp,
                        color = Color(0xFF777777)
                    )
                }

                IconButton(
                    onClick = onEliminar
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar registro",
                        tint = Color(0xFFB00020),
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Confianza: ${"%.2f".format(porcentaje)}%",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F)
            )

            Spacer(modifier = Modifier.height(7.dp))

            LinearProgressIndicator(
                progress = {
                    analisis.confianza.toFloat().coerceIn(0f, 1f)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp),
                color = Color(0xFF2E7D32),
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Recomendación:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F)
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = analisis.recomendacion,
                fontSize = 13.sp,
                color = Color(0xFF333333),
                lineHeight = 18.sp
            )
        }
    }
}

private fun formatearFecha(fechaHora: Long): String {
    return try {
        val formato = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        )

        formato.format(Date(fechaHora))
    } catch (e: Exception) {
        "Fecha no disponible"
    }
}