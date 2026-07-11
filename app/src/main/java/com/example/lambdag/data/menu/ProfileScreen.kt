package com.example.lambdag.ui.menu

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

@Composable
fun ProfileScreen(
    auth: FirebaseAuth,
    dbRef: DatabaseReference,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit = {}
) {
    val colorFondo = Color(0xFFA8BC84)
    val colorTarjeta = Color.White
    val colorTextoPrincipal = Color(0xFF1F1F1F)
    val colorTextoSecundario = Color(0xFF555555)
    val colorVerde = Color(0xFF2E7D32)
    val colorBorde = Color(0xFFD0D0D0)

    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var userData by remember { mutableStateOf<Map<String, Any?>?>(null) }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            cargando = false
            mensajeError = "No se encontró una sesión activa."
            return@LaunchedEffect
        }

        dbRef.child("Usuarios").child(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                userData = snapshot.value as? Map<String, Any?>
                cargando = false

                if (userData == null) {
                    mensajeError = "No se encontraron datos del usuario."
                }
            }
            .addOnFailureListener {
                cargando = false
                mensajeError = "No fue posible cargar la información del usuario."
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
                .padding(horizontal = 16.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EncabezadoMisDatos()

            Spacer(modifier = Modifier.height(28.dp))

            when {
                cargando -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colorTarjeta),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = colorVerde
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Cargando tus datos...",
                                fontSize = 15.sp,
                                color = colorTextoSecundario
                            )
                        }
                    }
                }

                mensajeError != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colorTarjeta),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Sin datos",
                                tint = colorVerde,
                                modifier = Modifier.size(54.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = mensajeError.orEmpty(),
                                fontSize = 15.sp,
                                color = colorTextoPrincipal,
                                textAlign = TextAlign.Center,
                                lineHeight = 21.sp
                            )
                        }
                    }
                }

                else -> {
                    val nombre = obtenerCampo(userData, "nombre")
                    val apellidoPaterno = obtenerCampo(userData, "apellidoPaterno")
                    val apellidoMaterno = obtenerCampo(userData, "apellidoMaterno")
                    val edad = obtenerCampo(userData, "edad")
                    val sexo = obtenerCampo(userData, "sexo")
                    val correo = obtenerCampo(userData, "correo")

                    /*
                     * IMPORTANTE:
                     * Firebase Auth no permite recuperar la contraseña del usuario.
                     * Este campo solo se mostrará si tú guardas la contraseña en Realtime Database
                     * con la clave "contraseña" o "password".
                     */
                    val contrasena = obtenerCampo(userData, "contraseña")
                        .ifBlank { obtenerCampo(userData, "password") }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = colorTarjeta),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(86.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEAF4E4))
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFFBFD5AF),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Perfil",
                                    tint = colorVerde,
                                    modifier = Modifier.size(68.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = construirNombreCompleto(
                                    nombre = nombre,
                                    apellidoPaterno = apellidoPaterno,
                                    apellidoMaterno = apellidoMaterno
                                ),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorTextoPrincipal,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = correo.ifBlank { "Correo no registrado" },
                                fontSize = 13.sp,
                                color = colorTextoSecundario,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(22.dp))

                            DataRow(
                                label = "Nombre(s)",
                                value = nombre,
                                colorBorde = colorBorde
                            )

                            DataRow(
                                label = "Apellido paterno",
                                value = apellidoPaterno,
                                colorBorde = colorBorde
                            )

                            DataRow(
                                label = "Apellido materno",
                                value = apellidoMaterno,
                                colorBorde = colorBorde
                            )

                            DataRow(
                                label = "Edad",
                                value = edad,
                                colorBorde = colorBorde
                            )

                            DataRow(
                                label = "Sexo",
                                value = sexo,
                                colorBorde = colorBorde
                            )

                            DataRow(
                                label = "Correo",
                                value = correo,
                                colorBorde = colorBorde
                            )

                            DataRowPassword(
                                label = "Contraseña",
                                value = contrasena,
                                colorBorde = colorBorde
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    auth.signOut()
                    onCerrarSesion()
                },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Cerrar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onVolver,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.outlinedButtonColors(
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

                Spacer(modifier = Modifier.size(6.dp))

                Text(
                    text = "Volver",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun EncabezadoMisDatos() {
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
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Mis datos",
                tint = Color.Black,
                modifier = Modifier.size(58.dp)
            )

            Spacer(modifier = Modifier.size(18.dp))

            Text(
                text = "Mis datos",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F)
            )
        }
    }
}

@Composable
private fun DataRow(
    label: String,
    value: String,
    colorBorde: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2E2E2E),
                modifier = Modifier.weight(0.45f)
            )

            Text(
                text = value.ifBlank { "No registrado" },
                fontSize = 14.sp,
                color = Color(0xFF555555),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.55f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colorBorde)
        )
    }
}

@Composable
private fun DataRowPassword(
    label: String,
    value: String,
    colorBorde: Color
) {
    var mostrarPassword by remember { mutableStateOf(false) }

    val textoPassword = when {
        value.isBlank() -> "No disponible"
        mostrarPassword -> value
        else -> "•".repeat(value.length)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2E2E2E),
                modifier = Modifier.weight(0.40f)
            )

            Text(
                text = textoPassword,
                fontSize = 14.sp,
                color = Color(0xFF555555),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.45f)
            )

            IconButton(
                onClick = {
                    if (value.isNotBlank()) {
                        mostrarPassword = !mostrarPassword
                    }
                },
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = if (mostrarPassword) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    },
                    contentDescription = if (mostrarPassword) {
                        "Ocultar contraseña"
                    } else {
                        "Mostrar contraseña"
                    },
                    tint = if (value.isBlank()) {
                        Color.LightGray
                    } else {
                        Color(0xFF2E7D32)
                    },
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colorBorde)
        )
    }
}

private fun obtenerCampo(
    userData: Map<String, Any?>?,
    clave: String
): String {
    return userData?.get(clave)?.toString().orEmpty()
}

private fun construirNombreCompleto(
    nombre: String,
    apellidoPaterno: String,
    apellidoMaterno: String
): String {
    val nombreCompleto = listOf(
        nombre,
        apellidoPaterno,
        apellidoMaterno
    )
        .filter { it.isNotBlank() }
        .joinToString(" ")

    return nombreCompleto.ifBlank {
        "Usuario"
    }
}