package com.example.lambdag.ui.menu

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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lambdag.R

@Composable
fun MainMenuScreen(
    onTomarFoto: () -> Unit,
    onSubirImagen: () -> Unit,
    onAprenderInfo: () -> Unit,
    onHistorial: () -> Unit,
    onVerMisDatos: () -> Unit
) {
    val colorFondo = Color(0xFFA8BC84)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorFondo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorFondo)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BarraSuperiorMenu(
                onVerMisDatos = onVerMisDatos
            )

            EncabezadoEcoLambda()

            Spacer(modifier = Modifier.height(70.dp))

            OpcionMenuPrincipal(
                texto = "Tomar Foto",
                icono = Icons.Default.CameraAlt,
                onClick = onTomarFoto
            )

            Spacer(modifier = Modifier.height(18.dp))

            OpcionMenuPrincipal(
                texto = "Resultado de análisis",
                icono = Icons.Default.Image,
                onClick = onSubirImagen
            )

            Spacer(modifier = Modifier.height(18.dp))

            OpcionMenuPrincipal(
                texto = "Aprender(Info)",
                icono = Icons.Default.MenuBook,
                onClick = onAprenderInfo
            )

            Spacer(modifier = Modifier.height(18.dp))

            OpcionMenuPrincipal(
                texto = "Historial",
                icono = Icons.Default.BarChart,
                onClick = onHistorial
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun BarraSuperiorMenu(
    onVerMisDatos: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        menuExpanded = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menú",
                        tint = Color.Black
                    )
                }

                Text(
                    text = "Menú Principal",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                },
                modifier = Modifier
                    .background(Color.White)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFD0D0D0),
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Ver mis datos",
                            color = Color.Black
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onVerMisDatos()
                    }
                )
            }
        }
    }
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
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo EcoLambda",
                tint = Color.Black,
                modifier = Modifier.size(58.dp)
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
private fun OpcionMenuPrincipal(
    texto: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.78f)
            .widthIn(max = 300.dp)
            .height(64.dp)
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
                imageVector = icono,
                contentDescription = texto,
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.width(28.dp))

            Text(
                text = texto,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1F1F1F)
            )
        }
    }
}