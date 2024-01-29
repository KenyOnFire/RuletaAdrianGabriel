package mvc

import kotlinx.serialization.Serializable

// Modelo de Usuario
@Serializable
data class Usuario(val nombreUsuario: String, val dineroActual: Int)
