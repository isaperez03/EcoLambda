package com.example.lambdag.ui.registro

import android.widget.Toast
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

@Composable
fun PantallaCrearCuenta(
    onCrearCuenta: (
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String,
        edad: String,
        sexo: String,
        correo: String,
        password: String
    ) -> Unit
) {
    val contexto = LocalContext.current

    val colorFondo = Color(0xFFA8BC84)
    val colorTarjeta = Color.White
    val colorVerde = Color(0xFF2E7D32)
    val colorTextoPrincipal = Color(0xFF1F1F1F)
    val colorTextoSecundario = Color(0xFF555555)
    val colorBorde = Color(0xFFD0D0D0)

    var nombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var confirmarCorreo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    var mostrarPassword by remember { mutableStateOf(false) }
    var mostrarConfirmarPassword by remember { mutableStateOf(false) }
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
            EncabezadoRegistro()

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp),
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
                            contentDescription = "Crear cuenta",
                            tint = colorVerde,
                            modifier = Modifier.size(58.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Crear cuenta",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorTextoPrincipal,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Regístrate para usar EcoLambda y comenzar a clasificar residuos.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = colorTextoSecundario,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    CampoTextoRegistro(
                        valor = nombre,
                        onValorChange = {
                            nombre = filtrarSoloLetras(it)
                        },
                        etiqueta = "Nombre(s)",
                        colorVerde = colorVerde,
                        colorBorde = colorBorde
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoTextoRegistro(
                        valor = apellidoPaterno,
                        onValorChange = {
                            apellidoPaterno = filtrarSoloLetras(it)
                        },
                        etiqueta = "Apellido paterno",
                        colorVerde = colorVerde,
                        colorBorde = colorBorde
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoTextoRegistro(
                        valor = apellidoMaterno,
                        onValorChange = {
                            apellidoMaterno = filtrarSoloLetras(it)
                        },
                        etiqueta = "Apellido materno",
                        colorVerde = colorVerde,
                        colorBorde = colorBorde
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = edad,
                        onValueChange = { nuevoValor ->
                            edad = nuevoValor.filter { it.isDigit() }.take(3)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Edad")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Edad",
                                tint = colorVerde
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorVerde,
                            unfocusedBorderColor = colorBorde,
                            focusedLabelColor = colorVerde,
                            cursorColor = colorVerde
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Sexo",
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorTextoPrincipal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SelectorSexo(
                        sexoSeleccionado = sexo,
                        onSexoSeleccionado = {
                            sexo = it
                        },
                        colorVerde = colorVerde
                    )

                    Spacer(modifier = Modifier.height(18.dp))

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

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = confirmarCorreo,
                        onValueChange = {
                            confirmarCorreo = it.trim()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = "Confirmar correo")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Confirmar correo",
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

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoPasswordRegistro(
                        valor = password,
                        onValorChange = {
                            password = it
                        },
                        etiqueta = "Contraseña",
                        mostrarPassword = mostrarPassword,
                        onCambiarVisibilidad = {
                            mostrarPassword = !mostrarPassword
                        },
                        colorVerde = colorVerde,
                        colorBorde = colorBorde
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CampoPasswordRegistro(
                        valor = confirmarPassword,
                        onValorChange = {
                            confirmarPassword = it
                        },
                        etiqueta = "Confirmar contraseña",
                        mostrarPassword = mostrarConfirmarPassword,
                        onCambiarVisibilidad = {
                            mostrarConfirmarPassword = !mostrarConfirmarPassword
                        },
                        colorVerde = colorVerde,
                        colorBorde = colorBorde
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Button(
                        onClick = {
                            val validacion = validarRegistro(
                                nombre = nombre,
                                apellidoPaterno = apellidoPaterno,
                                apellidoMaterno = apellidoMaterno,
                                edad = edad,
                                sexo = sexo,
                                correo = correo,
                                confirmarCorreo = confirmarCorreo,
                                password = password,
                                confirmarPassword = confirmarPassword
                            )

                            if (validacion != null) {
                                Toast.makeText(
                                    contexto,
                                    validacion,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                cargando = true

                                onCrearCuenta(
                                    nombre.trim(),
                                    apellidoPaterno.trim(),
                                    apellidoMaterno.trim(),
                                    edad.trim(),
                                    sexo.trim(),
                                    correo.trim(),
                                    password
                                )

                                cargando = false
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
                                text = "Registrando...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = "Crear cuenta",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "EcoLambda te ayuda a separar mejor tus residuos y aprender sobre reciclaje.",
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
private fun EncabezadoRegistro() {
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
private fun CampoTextoRegistro(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    colorVerde: Color,
    colorBorde: Color
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = etiqueta)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = etiqueta,
                tint = colorVerde
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorVerde,
            unfocusedBorderColor = colorBorde,
            focusedLabelColor = colorVerde,
            cursorColor = colorVerde
        )
    )
}

@Composable
private fun CampoPasswordRegistro(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    mostrarPassword: Boolean,
    onCambiarVisibilidad: () -> Unit,
    colorVerde: Color,
    colorBorde: Color
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = etiqueta)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = etiqueta,
                tint = colorVerde
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onCambiarVisibilidad
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
                    tint = colorVerde
                )
            }
        },
        singleLine = true,
        visualTransformation = if (mostrarPassword) {
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
}

@Composable
private fun SelectorSexo(
    sexoSeleccionado: String,
    onSexoSeleccionado: (String) -> Unit,
    colorVerde: Color
) {
    val opciones = listOf("Masculino", "Femenino", "Otro")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0xFFD0D0D0),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        opciones.forEach { opcion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 42.dp)
                    .clickable {
                        onSexoSeleccionado(opcion)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = sexoSeleccionado == opcion,
                    onClick = {
                        onSexoSeleccionado(opcion)
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = colorVerde,
                        unselectedColor = Color(0xFF777777)
                    )
                )

                Text(
                    text = opcion,
                    fontSize = 14.sp,
                    color = Color(0xFF1F1F1F)
                )
            }
        }
    }
}

private fun filtrarSoloLetras(texto: String): String {
    return texto.filter { caracter ->
        caracter.isLetter() || caracter.isWhitespace()
    }
}

private fun validarRegistro(
    nombre: String,
    apellidoPaterno: String,
    apellidoMaterno: String,
    edad: String,
    sexo: String,
    correo: String,
    confirmarCorreo: String,
    password: String,
    confirmarPassword: String
): String? {
    val edadNumero = edad.toIntOrNull()

    return when {
        nombre.trim().length < 3 -> {
            "El nombre debe tener al menos 3 letras."
        }

        apellidoPaterno.trim().length < 3 -> {
            "El apellido paterno debe tener al menos 3 letras."
        }

        apellidoMaterno.trim().length < 3 -> {
            "El apellido materno debe tener al menos 3 letras."
        }

        edadNumero == null -> {
            "Ingresa una edad válida."
        }

        edadNumero !in 8..100 -> {
            "La edad debe estar entre 8 y 100 años."
        }

        sexo.isBlank() -> {
            "Selecciona tu sexo."
        }

        correo.isBlank() -> {
            "Ingresa tu correo electrónico."
        }

        !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
            "Ingresa un correo electrónico válido."
        }

        correo != confirmarCorreo -> {
            "Los correos no coinciden."
        }

        password.length < 6 -> {
            "La contraseña debe tener al menos 6 caracteres."
        }

        password.length > 15 -> {
            "La contraseña no debe superar los 15 caracteres."
        }

        password != confirmarPassword -> {
            "Las contraseñas no coinciden."
        }

        else -> null
    }
}