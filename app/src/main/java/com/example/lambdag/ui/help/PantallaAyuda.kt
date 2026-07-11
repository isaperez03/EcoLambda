package com.example.lambdag.ui.help

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lambdag.R
import kotlinx.coroutines.delay

data class CategoriaReciclaje(
    val titulo: String,
    val descripcion: String,
    @DrawableRes val imagenRes: Int
)

data class SeccionResiduo(
    val titulo: String,
    val contenido: String
)

@Composable
fun PantallaAyuda(
    onVolver: () -> Unit
) {
    val colorFondo = Color(0xFFA8BC84)
    val colorTarjeta = Color.White
    val colorBorde = Color(0xFF9A9A9A)

    var categoriaSeleccionada by remember { mutableStateOf<CategoriaReciclaje?>(null) }

    val categorias = listOf(
        CategoriaReciclaje(
            titulo = "Orgánico",
            descripcion = "Restos de comida",
            imagenRes = R.drawable.img_organico
        ),
        CategoriaReciclaje(
            titulo = "Inorgánica",
            descripcion = "Botellas, envases",
            imagenRes = R.drawable.img_inorganico
        ),
        CategoriaReciclaje(
            titulo = "Papel/Cartón",
            descripcion = "Cajas, hojas",
            imagenRes = R.drawable.img_papel_carton
        )
    )

    val datosCuriosos = remember {
        listOf(
            "El reciclaje ayuda a reducir la cantidad de basura que llega a rellenos sanitarios.",
            "Separar residuos desde casa facilita mucho el trabajo de recolección y tratamiento.",
            "El vidrio puede reciclarse muchas veces sin perder demasiada calidad.",
            "Las latas de aluminio son de los materiales más fáciles de reciclar.",
            "Reciclar papel ayuda a disminuir la tala de árboles.",
            "No todo el plástico se recicla igual; algunos tipos son más difíciles de procesar.",
            "Los residuos orgánicos pueden convertirse en composta para plantas y cultivos.",
            "Una botella de plástico puede tardar cientos de años en degradarse.",
            "El cartón sucio con grasa, como cajas de pizza, no siempre puede reciclarse.",
            "Separar pilas y baterías es importante porque pueden liberar sustancias contaminantes.",
            "Los aparatos electrónicos contienen metales que pueden recuperarse y reutilizarse.",
            "El reciclaje también ayuda a ahorrar energía en comparación con fabricar materiales nuevos.",
            "La ropa vieja puede reutilizarse, donarse o transformarse en nuevos productos textiles.",
            "Los residuos peligrosos no deben mezclarse con la basura común.",
            "La separación de basura puede hacerse en orgánica, inorgánica, reciclable y peligrosa.",
            "Reutilizar es incluso mejor que reciclar, porque evita generar más residuos.",
            "Muchos envases reciclables deben estar limpios y secos para poder procesarse mejor.",
            "El aceite usado de cocina puede contaminar mucha agua si se tira al drenaje.",
            "Las tapas de plástico suelen reciclarse por separado de las botellas.",
            "El reciclaje promueve una economía circular, donde los materiales se aprovechan más tiempo.",
            "Algunos residuos pueden tener una segunda vida como muebles, macetas o decoración.",
            "El unicel es muy contaminante y difícil de reciclar en muchos lugares.",
            "Comprar productos con menos empaque ayuda a generar menos basura.",
            "Los residuos sanitarios, como cubrebocas o pañuelos usados, no deben ir con reciclables.",
            "La composta reduce malos olores si se hace correctamente con residuos orgánicos adecuados.",
            "Separar basura no solo cuida el ambiente, también mejora la salud pública.",
            "Las bolsas reutilizables ayudan a disminuir el consumo de bolsas plásticas desechables.",
            "Muchos materiales reciclados se usan para fabricar ropa, muebles, envases y papel.",
            "Las escuelas pueden fomentar el reciclaje con campañas, contenedores y actividades ambientales.",
            "El mejor residuo es el que no se genera, por eso reducir el consumo es clave."
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorFondo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EncabezadoAyuda()

            Spacer(modifier = Modifier.height(22.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = colorTarjeta),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Descubre cómo separar correctamente los residuos para ayudar a cuidar el planeta",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        categorias.forEach { categoria ->
                            TarjetaCategoria(
                                categoria = categoria,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    categoriaSeleccionada = categoria
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            GloboSabiasQueAnimado(
                datosCuriosos = datosCuriosos,
                colorTarjeta = colorTarjeta,
                colorBorde = colorBorde
            )

            Spacer(modifier = Modifier.height(34.dp))

            Button(
                onClick = onVolver,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .wrapContentHeight()
                    .border(1.dp, Color(0xFFB0B0B0), RoundedCornerShape(6.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Entiendo, Volver",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }

    categoriaSeleccionada?.let { categoria ->
        VentanaInformacionResiduo(
            categoria = categoria,
            onCerrar = {
                categoriaSeleccionada = null
            }
        )
    }
}

@Composable
private fun EncabezadoAyuda() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_reciclaje),
                contentDescription = "Reciclaje",
                modifier = Modifier.size(62.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(18.dp))

            Text(
                text = "Aprende a reciclar",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F)
            )
        }
    }
}

@Composable
private fun TarjetaCategoria(
    categoria: CategoriaReciclaje,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = categoria.titulo,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = categoria.imagenRes),
            contentDescription = categoria.titulo,
            modifier = Modifier
                .size(width = 100.dp, height = 70.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "-${categoria.descripcion}",
            fontSize = 12.sp,
            lineHeight = 15.sp,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Toca para saber más",
            fontSize = 10.sp,
            color = Color(0xFF2E7D32),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GloboSabiasQueAnimado(
    datosCuriosos: List<String>,
    colorTarjeta: Color,
    colorBorde: Color
) {
    var indiceDatoActual by remember { mutableStateOf(0) }

    fun mostrarSiguienteDato() {
        if (datosCuriosos.isNotEmpty()) {
            indiceDatoActual = (indiceDatoActual + 1) % datosCuriosos.size
        }
    }

    LaunchedEffect(indiceDatoActual, datosCuriosos) {
        if (datosCuriosos.isNotEmpty()) {
            delay(10000)
            mostrarSiguienteDato()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                mostrarSiguienteDato()
            },
        contentAlignment = Alignment.TopStart
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, start = 18.dp, end = 18.dp),
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 8.dp,
                bottomEnd = 8.dp,
                bottomStart = 8.dp
            ),
            colors = CardDefaults.cardColors(containerColor = colorTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Crossfade(
                targetState = indiceDatoActual,
                animationSpec = tween(durationMillis = 900),
                label = "TransicionDatosCuriosos"
            ) { indice ->
                Text(
                    text = datosCuriosos[indice],
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 28.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = Color.Black
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(start = 28.dp)
                .background(Color.White, RoundedCornerShape(50))
                .border(1.dp, colorBorde, RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = "¿Sabías que?",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun VentanaInformacionResiduo(
    categoria: CategoriaReciclaje,
    onCerrar: () -> Unit
) {
    val secciones = obtenerInformacionResiduo(categoria.titulo)

    AlertDialog(
        onDismissRequest = onCerrar,
        containerColor = Color.White,
        shape = RoundedCornerShape(18.dp),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = categoria.imagenRes),
                    contentDescription = categoria.titulo,
                    modifier = Modifier
                        .size(width = 120.dp, height = 85.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = categoria.titulo,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 430.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                secciones.forEach { seccion ->
                    SeccionInformacion(
                        titulo = seccion.titulo,
                        contenido = seccion.contenido
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onCerrar
            ) {
                Text(
                    text = "Entendido",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

private fun obtenerInformacionResiduo(
    tituloCategoria: String
): List<SeccionResiduo> {
    return when (tituloCategoria) {
        "Orgánico" -> listOf(
            SeccionResiduo(
                titulo = "¿Qué son?",
                contenido = "Son residuos de origen natural que provienen principalmente de restos de comida, frutas, verduras, plantas, hojas, ramas y otros materiales biodegradables."
            ),
            SeccionResiduo(
                titulo = "Ejemplos",
                contenido = "Cáscaras de fruta, restos de verduras, semillas, hojas secas, residuos de café, bolsitas de té, ramas pequeñas y restos de alimentos naturales."
            ),
            SeccionResiduo(
                titulo = "¿Por qué separarlos?",
                contenido = "Separarlos evita que se mezclen con plásticos, vidrios o metales. También reduce malos olores y facilita el tratamiento correcto de la basura."
            ),
            SeccionResiduo(
                titulo = "¿Qué se puede hacer con ellos?",
                contenido = "Pueden transformarse en composta, un abono natural que ayuda a mejorar la tierra y beneficia plantas, jardines, cultivos y áreas verdes."
            ),
            SeccionResiduo(
                titulo = "Recomendación",
                contenido = "Colócalos en un bote separado. Evita mezclarlos con plástico, vidrio, pilas, productos químicos o residuos sanitarios."
            ),
            SeccionResiduo(
                titulo = "Importante",
                contenido = "Para composta casera es mejor evitar exceso de carne, grasa, lácteos o comida muy condimentada, porque puede generar malos olores o atraer insectos."
            )
        )

        "Inorgánica" -> listOf(
            SeccionResiduo(
                titulo = "¿Qué son?",
                contenido = "Son residuos que no provienen directamente de seres vivos o que tardan mucho tiempo en degradarse. Muchos están hechos de plástico, metal, vidrio o materiales sintéticos."
            ),
            SeccionResiduo(
                titulo = "Ejemplos",
                contenido = "Botellas de plástico, envases, bolsas, latas, vidrio, empaques, envolturas, tapas, objetos metálicos, productos de unicel y algunos recipientes."
            ),
            SeccionResiduo(
                titulo = "¿Por qué separarlos?",
                contenido = "Separarlos ayuda a identificar qué materiales pueden reciclarse y evita que contaminen los residuos orgánicos o el ambiente."
            ),
            SeccionResiduo(
                titulo = "¿Qué se puede reciclar?",
                contenido = "Algunos materiales inorgánicos como aluminio, vidrio, ciertos plásticos y metales pueden procesarse para fabricar nuevos productos."
            ),
            SeccionResiduo(
                titulo = "Recomendación",
                contenido = "Antes de depositarlos, procura que los envases reciclables estén vacíos, limpios y secos. Esto facilita su recolección y mejora el proceso de reciclaje."
            ),
            SeccionResiduo(
                titulo = "Importante",
                contenido = "No todos los residuos inorgánicos son reciclables. Por ejemplo, algunos plásticos, unicel, envolturas metalizadas o materiales mezclados pueden ser difíciles de procesar."
            )
        )

        "Papel/Cartón" -> listOf(
            SeccionResiduo(
                titulo = "¿Qué son?",
                contenido = "Son residuos fabricados principalmente con fibras vegetales. Pueden aprovecharse nuevamente si se mantienen limpios, secos y separados de otros residuos."
            ),
            SeccionResiduo(
                titulo = "Ejemplos",
                contenido = "Hojas de papel, libretas usadas, periódicos, revistas, cajas, cartón corrugado, sobres, bolsas de papel y empaques de cartón limpios."
            ),
            SeccionResiduo(
                titulo = "¿Por qué separarlos?",
                contenido = "Separar papel y cartón ayuda a reducir la tala de árboles, ahorrar agua y disminuir la energía necesaria para fabricar papel nuevo."
            ),
            SeccionResiduo(
                titulo = "¿Cómo separarlos correctamente?",
                contenido = "Deben estar limpios y secos. Lo ideal es doblar o compactar las cajas para ocupar menos espacio y facilitar su almacenamiento."
            ),
            SeccionResiduo(
                titulo = "Recomendación",
                contenido = "Retira cinta adhesiva, grapas, restos de comida o plástico cuando sea posible. Guarda el papel y cartón en un lugar seco."
            ),
            SeccionResiduo(
                titulo = "Importante",
                contenido = "El papel o cartón con grasa, comida, humedad o químicos no siempre puede reciclarse. Un ejemplo común son las cajas de pizza muy grasosas."
            )
        )

        else -> emptyList()
    }
}

@Composable
private fun SeccionInformacion(
    titulo: String,
    contenido: String
) {
    Text(
        text = titulo,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = contenido,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        color = Color.DarkGray,
        textAlign = TextAlign.Justify
    )

    Spacer(modifier = Modifier.height(14.dp))
}