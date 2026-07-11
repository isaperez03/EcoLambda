package com.example.lambdag.ui.registro

data class CrearCuentaUiState(
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val edad: String = "",
    val sexo: String = "",
    val correo: String = "",
    val confirmarCorreo: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val isLoading: Boolean = false,

    // Campos de error (si la validación falla)
    val errorNombre: String? = null,
    val errorApellidoPaterno: String? = null,
    val errorApellidoMaterno: String? = null,
    val errorEdad: String? = null,
    val errorSexo: String? = null,
    val errorCorreo: String? = null,
    val errorConfirmarCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmarContrasena: String? = null,
)
