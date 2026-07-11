// app/src/main/java/com/example/lambdag/ui/registro/CrearCuentaViewModel.kt
package com.example.lambdag.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CrearCuentaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CrearCuentaUiState())
    val uiState: StateFlow<CrearCuentaUiState> = _uiState.asStateFlow()

    // Helpers para actualizar campos y limpiar errores
    fun onNombreChange(valor: String) = update { it.copy(nombre = valor, errorNombre = null) }
    fun onApellidoPaternoChange(valor: String) = update { it.copy(apellidoPaterno = valor, errorApellidoPaterno = null) }
    fun onApellidoMaternoChange(valor: String) = update { it.copy(apellidoMaterno = valor, errorApellidoMaterno = null) }
    fun onEdadChange(valor: String) = update { it.copy(edad = valor, errorEdad = null) }
    fun onSexoChange(valor: String) = update { it.copy(sexo = valor, errorSexo = null) }
    fun onCorreoChange(valor: String) = update { it.copy(correo = valor, errorCorreo = null) }
    fun onConfirmarCorreoChange(valor: String) = update { it.copy(confirmarCorreo = valor, errorConfirmarCorreo = null) }
    fun onContrasenaChange(valor: String) = update { it.copy(contrasena = valor, errorContrasena = null) }
    fun onConfirmarContrasenaChange(valor: String) = update { it.copy(confirmarContrasena = valor, errorConfirmarContrasena = null) }

    // Al presionar "Terminar registro"
    fun onRegistrar() {
        viewModelScope.launch {
            val s = uiState.value

            // 1) Validaciones simples
            var valid = true
            if (s.nombre.isBlank()) {
                valid = false; update { it.copy(errorNombre = "Requerido") }
            }
            if (s.apellidoPaterno.isBlank()) {
                valid = false; update { it.copy(errorApellidoPaterno = "Requerido") }
            }
            if (s.edad.isBlank()) {
                valid = false; update { it.copy(errorEdad = "Requerido") }
            }
            if (s.sexo.isBlank()) {
                valid = false; update { it.copy(errorSexo = "Selecciona sexo") }
            }
            if (s.correo.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(s.correo).matches()) {
                valid = false; update { it.copy(errorCorreo = "Correo inválido") }
            }
            if (s.correo != s.confirmarCorreo) {
                valid = false; update { it.copy(errorConfirmarCorreo = "No coincide") }
            }
            if (s.contrasena.length < 6) {
                valid = false; update { it.copy(errorContrasena = "Mínimo 6 caracteres") }
            }
            if (s.contrasena != s.confirmarContrasena) {
                valid = false; update { it.copy(errorConfirmarContrasena = "No coincide") }
            }

            if (!valid) return@launch

            // 2) Mostrar indicador de carga
            update { it.copy(isLoading = true) }

            // TODO: aquí invocar tu repositorio para Auth/RealtimeDB
            // Por ejemplo:
            // repository.crearUsuario(s).onSuccess { ... }.onFailure { ... }

            // 3) Al finalizar, ocultar carga
            update { it.copy(isLoading = false) }
        }
    }

    // Función helper para actualizar estado
    private fun update(block: (CrearCuentaUiState) -> CrearCuentaUiState) {
        _uiState.update(block)
    }
}
