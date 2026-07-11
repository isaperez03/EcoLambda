package com.example.lambdag.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lambdag.ui.help.PantallaAyuda
import com.example.lambdag.ui.history.HistorialScreen
import com.example.lambdag.ui.login.LoginScreen
import com.example.lambdag.ui.menu.MainMenuScreen
import com.example.lambdag.ui.menu.ProfileScreen
import com.example.lambdag.ui.registro.PantallaCrearCuenta
import com.example.lambdag.ui.results.ResultsScreen
import com.example.lambdag.ui.test.TestScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val auth = Firebase.auth
    val dbRef = Firebase.database.reference
    val contexto = LocalContext.current

    val startDestination = if (auth.currentUser != null) {
        "menu"
    } else {
        "login"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("login") {
            LoginScreen(
                onCrearCuenta = {
                    navController.navigate("registro")
                },
                onLoginSuccess = {
                    navController.navigate("menu") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("registro") {
            PantallaCrearCuenta { nombre, apellidoP, apellidoM, edad, sexo, correo, password ->

                auth.createUserWithEmailAndPassword(correo, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = task.result?.user?.uid

                            if (uid == null) {
                                Toast.makeText(
                                    contexto,
                                    "No se pudo obtener el UID del usuario.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@addOnCompleteListener
                            }

                            val userMap = mapOf(
                                "nombre" to nombre,
                                "apellidoPaterno" to apellidoP,
                                "apellidoMaterno" to apellidoM,
                                "edad" to edad,
                                "sexo" to sexo,
                                "correo" to correo,
                                "contraseña" to password
                            )

                            dbRef.child("Usuarios")
                                .child(uid)
                                .setValue(userMap)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Toast.makeText(
                                            contexto,
                                            "Registro exitoso",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        navController.navigate("menu") {
                                            popUpTo("registro") {
                                                inclusive = true
                                            }
                                        }
                                    } else {
                                        Toast.makeText(
                                            contexto,
                                            "Error BD: ${dbTask.exception?.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                        } else {
                            Toast.makeText(
                                contexto,
                                "Error Auth: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }

        composable("menu") {
            MainMenuScreen(
                onTomarFoto = {
                    navController.navigate("test")
                },
                onSubirImagen = {
                    navController.navigate("results")
                },
                onAprenderInfo = {
                    navController.navigate("help")
                },
                onHistorial = {
                    navController.navigate("history")
                },
                onVerMisDatos = {
                    navController.navigate("profile")
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                auth = auth,
                dbRef = dbRef,
                onVolver = {
                    navController.popBackStack()
                },
                onCerrarSesion = {
                    auth.signOut()

                    navController.navigate("login") {
                        popUpTo("menu") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("test") {
            TestScreen(
                onTomarFoto = {
                    // La lógica real de cámara está dentro de TestScreen
                },
                onSubirImagen = {
                    navController.navigate("results")
                },
                onContinuar = {
                    navController.navigate("results")
                }
            )
        }

        composable("results") {
            ResultsScreen(
                onVolver = {
                    navController.popBackStack()
                },
                onIntentarOtraVez = {
                    navController.navigate("test")
                }
            )
        }

        composable("history") {
            HistorialScreen(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable("help") {
            PantallaAyuda(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}