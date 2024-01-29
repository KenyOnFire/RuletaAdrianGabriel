package mvc

import dev.gitlive.firebase.auth.*

class Controller {
    private lateinit var auth: FirebaseAuth
    private lateinit var firebasemanager: FirebaseManager
    private lateinit var actualUser: Usuario

     suspend fun launchFirebase() {
         firebaseManager.startFB()
         firebaseManager.startFirebaseRealtimeDB()
         auth = firebaseManager.startFirebaseAuth()!!
         firebasemanager = FirebaseManager(auth)
     }

    fun getUser(): Usuario? {
        if (!firebasemanager.readUser()) {
            return null
        }
        return firebasemanager.user
    }

    suspend fun setUser(email:String, password:String): Usuario? {
        if (!firebasemanager.loginUser(email, password)){
            return null
        }
        return firebasemanager.user
    }

    suspend fun setNewUser(email:String, password:String, displayName:String): Usuario? {
        if (!firebasemanager.registerUser(email, password, displayName)){
            return null
        }
        return firebasemanager.user
    }

    suspend fun signOutUser() {
        auth.signOut()
    }
//    suspend fun leerUsuario(): List<Usuario> {
//        val conjuntoUsuarios = jsonmanager.readUsers()
//        val listaUsuarios = conjuntoUsuarios.jsonArray.map {
//            Usuario(it.jsonObject["nombreUsuario"].toString().removeSurrounding("\""),it.jsonObject["dineroActual"].toString().toInt())
//        }
//        println(listaUsuarios)
//        return listaUsuarios
//    }
}
