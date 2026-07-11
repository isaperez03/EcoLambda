package com.example.lambdag.ui.login

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lambdag.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(
    onCrearCuenta: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val contexto = LocalContext.current
    val auth = Firebase.auth

    val colorFondo = Color(0xFFA8BC84)
    val colorTarjeta = Color.White
    val colorVerde = Color(0xFF2E7D32)
    val colorTextoPrincipal = Color(0xFF1F1F1F)
    val colorTextoSecundario = Color(0xFF555555)
    val colorBorde = Color(0xFFD0D0D0)

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }

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
            EncabezadoLogin()

            Spacer(modifier = Modifier.height(38.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorTarjeta
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
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
                            imageVector = Icons.Default.Person,
                            contentDescription = "Usuario",
                            tint = colorVerde,
                            modifier = Modifier.size(58.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Iniciar sesión",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorTextoPrincipal,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Accede a EcoLambda para clasificar residuos y aprender sobre reciclaje.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorTextoSecundario,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = {
                            correo = it.trim()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Correo electrónico")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Correo",
                                tint = colorVerde
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorVerde,
                            unfocusedBorderColor = colorBorde,
                            focusedLabelColor = colorVerde,
                            cursorColor = colorVerde
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = {
                            contrasena = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Contraseña")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Contraseña",
                                tint = colorVerde
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    mostrarContrasena = !mostrarContrasena
                                }
                            ) {
                                Icon(
                                    imageVector = if (mostrarContrasena) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = if (mostrarContrasena) {
                                        "Ocultar contraseña"
                                    } else {
                                        "Mostrar contraseña"
                                    },
                                    tint = colorVerde
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (mostrarContrasena) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorVerde,
                            unfocusedBorderColor = colorBorde,
                            focusedLabelColor = colorVerde,
                            cursorColor = colorVerde
                        )
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Button(
                        onClick = {
                            when {
                                correo.isBlank() -> {
                                    Toast.makeText(
                                        contexto,
                                        "Ingresa tu correo electrónico.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                contrasena.isBlank() -> {
                                    Toast.makeText(
                                        contexto,
                                        "Ingresa tu contraseña.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> {
                                    cargando = true

                                    auth.signInWithEmailAndPassword(correo, contrasena)
                                        .addOnCompleteListener { task ->
                                            cargando = false

                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    contexto,
                                                    "Inicio de sesión exitoso",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                onLoginSuccess()
                                            } else {
                                                Toast.makeText(
                                                    contexto,
                                                    "Error: ${task.exception?.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                }
                            }
                        },
                        enabled = !cargando,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorVerde,
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF8CA58A),
                            disabledContentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (cargando) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "Ingresando...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = "Ingresar",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = onCrearCuenta,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFFB0B0B0),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Text(
                            text = "Crear cuenta",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Cuida el planeta separando tus residuos correctamente.",
                fontSize = 13.sp,
                color = Color(0xFF2E2E2E),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun EncabezadoLogin() {
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