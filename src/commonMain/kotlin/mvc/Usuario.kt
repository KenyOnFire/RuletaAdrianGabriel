package mvc

import kotlinx.serialization.Serializable

// Modelo de Usuario
@Serializable
data class Usuario(var nombreUsuario: String, var dineroActual: Int)
