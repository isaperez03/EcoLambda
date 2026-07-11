package com.example.lambdag.ui.test

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.lambdag.R
import com.example.lambdag.model.ClasificadorResiduos
import com.example.lambdag.model.EstadoClasificacion
import com.example.lambdag.model.GeminiClasificador
import com.example.lambdag.model.HistorialRepository
import com.example.lambdag.model.RedUtils
import com.example.lambdag.model.RemovedorFondo
import com.example.lambdag.model.ResultadoClasificacion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.Executors

@Composable
fun TestScreen(
    onTomarFoto: () -> Unit,
    onSubirImagen: () -> Unit,
    onContinuar: () -> Unit
) {
    val contexto = LocalContext.current
    val appContext = contexto.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    val colorFondo = Color(0xFFA8BC84)
    val colorVerde = Color(0xFF2E7D32)

    var permisoCamara by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcherPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permisoCamara = concedido

        if (!concedido) {
            Toast.makeText(
                contexto,
                "Se necesita permiso de cámara para usar el escáner.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!permisoCamara) {
            launcherPermiso.launch(Manifest.permission.CAMERA)
        }
    }

    val clasificador = remember {
        ClasificadorResiduos(appContext)
    }

    val removedorFondo = remember {
        RemovedorFondo(appContext)
    }

    val geminiClasificador = remember {
        GeminiClasificador()
    }

    val historialRepository = remember {
        HistorialRepository()
    }

    val executorCamara = remember {
        Executors.newSingleThreadExecutor()
    }

    val handlerPrincipal = remember {
        Handler(Looper.getMainLooper())
    }

    var ultimoBitmapOriginal by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var overlayObjeto by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var procesandoFrame by remember {
        mutableStateOf(false)
    }

    var clasificando by remember {
        mutableStateOf(false)
    }

    var ultimoAnalisisMs by remember {
        mutableLongStateOf(0L)
    }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }

        val bitmapGaleria = convertirUriABitmap(
            context = contexto,
            uri = uri
        )

        if (bitmapGaleria == null) {
            Toast.makeText(
                contexto,
                "No se pudo cargar la imagen.",
                Toast.LENGTH_SHORT
            ).show()
            return@rememberLauncherForActivityResult
        }

        clasificando = true

        scope.launch {
            try {
                val bitmapSinFondo = withContext(Dispatchers.Default) {
                    removedorFondo.eliminarFondo(
                        bitmapOriginal = bitmapGaleria,
                        fondoNegro = true
                    )
                }

                val resultadoFinal = clasificarAutomaticamente(
                    context = appContext,
                    bitmapOriginal = bitmapGaleria,
                    bitmapSinFondo = bitmapSinFondo,
                    clasificador = clasificador,
                    geminiClasificador = geminiClasificador
                )

                EstadoClasificacion.imagenBitmap = bitmapSinFondo
                EstadoClasificacion.resultado = resultadoFinal

                guardarAnalisisEnSegundoPlano(
                    historialRepository = historialRepository,
                    bitmap = bitmapSinFondo,
                    resultado = resultadoFinal
                )

                clasificando = false
                onContinuar()

            } catch (e: Exception) {
                clasificando = false
                e.printStackTrace()

                Toast.makeText(
                    contexto,
                    "Error al clasificar imagen: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            executorCamara.shutdown()
            clasificador.cerrar()
            removedorFondo.cerrar()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorFondo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorFondo)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EncabezadoEcoLambda()

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Escáner de residuos",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Centra el objeto dentro del marcador",
                fontSize = 15.sp,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (permisoCamara) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(315.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = Color(0xFF4D4D4D),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(Color.Black, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            val previewView = PreviewView(context).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                            }

                            val cameraProviderFuture =
                                ProcessCameraProvider.getInstance(context)

                            cameraProviderFuture.addListener(
                                {
                                    val cameraProvider = cameraProviderFuture.get()

                                    val preview = Preview.Builder()
                                        .build()
                                        .also { previewUseCase ->
                                            previewUseCase.setSurfaceProvider(
                                                previewView.surfaceProvider
                                            )
                                        }

                                    val imageAnalysis = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(
                                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                                        )
                                        .setOutputImageFormat(
                                            ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
                                        )
                                        .build()

                                    imageAnalysis.setAnalyzer(
                                        executorCamara
                                    ) { imageProxy ->
                                        val tiempoActual = System.currentTimeMillis()

                                        if (
                                            procesandoFrame ||
                                            tiempoActual - ultimoAnalisisMs < 1000L
                                        ) {
                                            imageProxy.close()
                                            return@setAnalyzer
                                        }

                                        ultimoAnalisisMs = tiempoActual
                                        procesandoFrame = true

                                        try {
                                            val bitmapFrame =
                                                convertirImageProxyABitmap(imageProxy)

                                            imageProxy.close()

                                            if (bitmapFrame != null) {
                                                val bitmapOverlay =
                                                    removedorFondo.eliminarFondo(
                                                        bitmapOriginal = bitmapFrame,
                                                        fondoNegro = false
                                                    )

                                                handlerPrincipal.post {
                                                    ultimoBitmapOriginal = bitmapFrame
                                                    overlayObjeto = bitmapOverlay
                                                    procesandoFrame = false
                                                }
                                            } else {
                                                handlerPrincipal.post {
                                                    procesandoFrame = false
                                                }
                                            }

                                        } catch (e: Exception) {
                                            imageProxy.close()
                                            e.printStackTrace()

                                            handlerPrincipal.post {
                                                procesandoFrame = false
                                            }
                                        }
                                    }

                                    try {
                                        cameraProvider.unbindAll()

                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            CameraSelector.DEFAULT_BACK_CAMERA,
                                            preview,
                                            imageAnalysis
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                },
                                ContextCompat.getMainExecutor(context)
                            )

                            previewView
                        }
                    )

                    overlayObjeto?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Objeto detectado",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            alpha = 0.92f
                        )
                    }

                    MarcadorCentral()

                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(50),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.88f)
                        )
                    ) {
                        Text(
                            text = if (procesandoFrame) {
                                "Detectando objeto..."
                            } else {
                                "Objeto enfocado en tiempo real"
                            },
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(230.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Cámara",
                            tint = colorVerde,
                            modifier = Modifier.size(54.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Permite el acceso a la cámara para usar el escáner en tiempo real.",
                            color = Color.Black,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                launcherPermiso.launch(Manifest.permission.CAMERA)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorVerde,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Permitir cámara")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    val bitmapParaClasificar = ultimoBitmapOriginal

                    if (bitmapParaClasificar == null) {
                        Toast.makeText(
                            contexto,
                            "Aún no se ha detectado una imagen.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    clasificando = true

                    scope.launch {
                        try {
                            val bitmapSinFondo = withContext(Dispatchers.Default) {
                                removedorFondo.eliminarFondo(
                                    bitmapOriginal = bitmapParaClasificar,
                                    fondoNegro = true
                                )
                            }

                            val resultadoFinal = clasificarAutomaticamente(
                                context = appContext,
                                bitmapOriginal = bitmapParaClasificar,
                                bitmapSinFondo = bitmapSinFondo,
                                clasificador = clasificador,
                                geminiClasificador = geminiClasificador
                            )

                            EstadoClasificacion.imagenBitmap = bitmapSinFondo
                            EstadoClasificacion.resultado = resultadoFinal

                            guardarAnalisisEnSegundoPlano(
                                historialRepository = historialRepository,
                                bitmap = bitmapSinFondo,
                                resultado = resultadoFinal
                            )

                            clasificando = false
                            onContinuar()

                        } catch (e: Exception) {
                            clasificando = false
                            e.printStackTrace()

                            Toast.makeText(
                                contexto,
                                "Error al clasificar: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                enabled = permisoCamara && !clasificando,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorVerde,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF8CA58A),
                    disabledContentColor = Color.White
                ),
                modifier = Modifier
                    .widthIn(min = 220.dp)
                    .height(52.dp)
            ) {
                if (clasificando) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Clasificando...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "Clasificar objeto",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OpcionSubirImagen(
                onClick = {
                    launcherGaleria.launch("image/*")
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private suspend fun clasificarAutomaticamente(
    context: Context,
    bitmapOriginal: Bitmap,
    bitmapSinFondo: Bitmap,
    clasificador: ClasificadorResiduos,
    geminiClasificador: GeminiClasificador
): ResultadoClasificacion = coroutineScope {

    val resultadoLocalAsync = async(Dispatchers.Default) {
        clasificador.clasificar(bitmapSinFondo)
    }

    if (!RedUtils.hayConexionInternet(context)) {
        return@coroutineScope resultadoLocalAsync.await()
    }

    val resultadoGemini = withTimeoutOrNull(5000L) {
        geminiClasificador.clasificarResiduo(
            bitmap = bitmapOriginal
        )
    }

    if (resultadoGemini != null) {
        return@coroutineScope resultadoGemini
    }

    return@coroutineScope resultadoLocalAsync.await()
}

private fun guardarAnalisisEnSegundoPlano(
    historialRepository: HistorialRepository,
    bitmap: Bitmap,
    resultado: ResultadoClasificacion
) {
    historialRepository.guardarAnalisis(
        bitmap = bitmap,
        resultado = resultado,
        onSuccess = {
            Log.d(
                "HistorialFirebase",
                "Análisis guardado correctamente."
            )
        },
        onError = { error ->
            Log.e(
                "HistorialFirebase",
                "No se pudo guardar el análisis.",
                error
            )
        }
    )
}

@Composable
private fun EncabezadoEcoLambda() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo EcoLambda",
                modifier = Modifier.size(58.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.Black)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "EcoLambda",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F)
            )
        }
    }
}

@Composable
private fun MarcadorCentral() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val centro = Offset(
            x = size.width / 2f,
            y = size.height / 2f
        )

        drawCircle(
            color = Color.White,
            radius = 64f,
            center = centro,
            style = Stroke(
                width = 4f,
                cap = StrokeCap.Round
            )
        )

        drawLine(
            color = Color.White,
            start = Offset(centro.x - 20f, centro.y),
            end = Offset(centro.x + 20f, centro.y),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = Color.White,
            start = Offset(centro.x, centro.y - 20f),
            end = Offset(centro.x, centro.y + 20f),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun OpcionSubirImagen(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.72f)
            .height(54.dp)
            .border(
                width = 1.dp,
                color = Color(0xFF9E9E9E),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Subir imagen",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(18.dp))

            Text(
                text = "Subir Imagen",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1F1F1F),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun convertirImageProxyABitmap(
    imageProxy: ImageProxy
): Bitmap? {
    return try {
        val bitmap = Bitmap.createBitmap(
            imageProxy.width,
            imageProxy.height,
            Bitmap.Config.ARGB_8888
        )

        val buffer = imageProxy.planes[0].buffer
        buffer.rewind()
        bitmap.copyPixelsFromBuffer(buffer)

        val gradosRotacion = imageProxy.imageInfo.rotationDegrees

        if (gradosRotacion != 0) {
            val matrix = Matrix().apply {
                postRotate(gradosRotacion.toFloat())
            }

            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        } else {
            bitmap
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun convertirUriABitmap(
    context: Context,
    uri: Uri
): Bitmap? {
    return try {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(
                context.contentResolver,
                uri
            )

            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                uri
            )
        }

        bitmap.copy(
            Bitmap.Config.ARGB_8888,
            true
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}