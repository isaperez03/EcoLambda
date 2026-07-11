package com.example.lambdag.ui.results

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lambdag.model.AnalisisResiduo
import com.example.lambdag.model.EstadoClasificacion
import com.example.lambdag.model.HistorialRepository
import com.example.lambdag.model.ImagenBase64Utils
import com.example.lambdag.model.ResultadoClasificacion

@Composable
fun ResultsScreen(
    onVolver: () -> Unit,
    onIntentarOtraVez: () -> Unit
) {
    val colorFondo = Color(0xFFA8BC84)
    val colorVerde = Color(0xFF2E7D32)

    val historialRepository = remember {
        HistorialRepository()
    }

    var ultimoAnalisisFirebase by remember {
        mutableStateOf<AnalisisResiduo?>(null)
    }

    DisposableEffect(Unit) {
        val listener = historialRepository.escucharUltimoAnalisis(
            onData = { analisis: AnalisisResiduo? ->
                ultimoAnalisisFirebase = analisis
            }
        )

        onDispose {
            if (listener != null) {
                historialRepository.detenerEscucha(listener)
            }
        }
    }

    val bitmapFirebase = ultimoAnalisisFirebase?.imagenBase64?.let { imagenBase64 ->
        ImagenBase64Utils.base64ABitmap(imagenBase64)
    }

    val resultadoFirebase = ultimoAnalisisFirebase?.let { analisis ->
        ResultadoClasificacion(
            etiqueta = analisis.etiqueta,
            confianza = analisis.confianza.toFloat(),
            indiceClase = -1,
            categoriaGeneral = analisis.categoriaGeneral,
            recomendacion = analisis.recomendacion
        )
    }

    val bitmap = EstadoClasificacion.imagenBitmap ?: bitmapFirebase
    val resultado = EstadoClasificacion.resultado ?: resultadoFirebase

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorFondo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorFondo)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BarraSuperiorResultados(
                onVolver = onVolver
            )

            Spacer(modifier = Modifier.height(34.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Imagen clasificada",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Sin imagen",
                                tint = Color.Black,
                                modifier = Modifier.size(56.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Sin imagen",
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (resultado != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
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
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Recycling,
                                contentDescription = "Resultado",
                                tint = colorVerde,
                                modifier = Modifier.size(42.dp)
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "Residuo detectado",
                                    color = Color(0xFF555555),
                                    fontSize = 14.sp
                                )

                                Text(
                                    text = resultado.etiqueta,
                                    color = Color(0xFF1F1F1F),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Categoría general:",
                            color = Color(0xFF1F1F1F),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = resultado.categoriaGeneral,
                            color = Color(0xFF333333),
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        val porcentaje = resultado.confianza * 100f

                        Text(
                            text = "Confianza: ${"%.2f".format(porcentaje)}%",
                            color = Color(0xFF1F1F1F),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = {
                                resultado.confianza.coerceIn(0f, 1f)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = colorVerde,
                            trackColor = Color(0xFFE0E0E0)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Recomendación:",
                            color = Color(0xFF1F1F1F),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = resultado.recomendacion,
                            color = Color(0xFF333333),
                            fontSize = 15.sp,
                            lineHeight = 21.sp
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Sin resultado",
                            tint = Color(0xFFB00020),
                            modifier = Modifier.size(38.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Todavía no hay un resultado de clasificación.",
                            color = Color(0xFF1F1F1F),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(34.dp))

            Button(
                onClick = {
                    EstadoClasificacion.limpiar()
                    onIntentarOtraVez()
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .widthIn(min = 210.dp)
                    .height(50.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF8F8F8F),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Text(
                    text = "Intentar otra vez",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun BarraSuperiorResultados(
    onVolver: () -> Unit
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
            text = "Resultado de análisis",
            modifier = Modifier.align(Alignment.Center),
            color = Color(0xFF1F1F1F),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}