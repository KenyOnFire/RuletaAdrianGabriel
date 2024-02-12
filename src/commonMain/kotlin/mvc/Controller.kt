package mvc

import dev.gitlive.firebase.auth.*
import dev.gitlive.firebase.database.*

class Controller {
    private lateinit var auth: FirebaseAuth

    private lateinit var firebasemanager: FirebaseManager

    suspend fun launchFirebase() {
        FirebaseManagerObj.startFB()
        FirebaseManagerObj.startFirebaseRealtimeDB()
        auth = FirebaseManagerObj.startFirebaseAuth()!!
        firebasemanager = FirebaseManager(auth)
    }

    suspend fun setNewUserRealtimeDatabase(email: String, displayName: String){
        FirebaseManagerObj.createUser(displayName, 100000, email)
        firebasemanager.user.nombreUsuario = displayName
        firebasemanager.user.dineroActual = 100000
    }

    suspend fun getAllUsers(): List<Pair<String, String>> {
        val names = FirebaseManagerObj.getAllUsersDb("displayname")
        val money = FirebaseManagerObj.getAllUsersDb("actualMoney")
        return names.zip(money)
    }

    suspend fun modifyMoneyUser(money:Int) {
        FirebaseManagerObj.modifyActualMoney(money.toString())

    }

    suspend fun getUserRealtimeDatabase(){
        firebasemanager.user.nombreUsuario = FirebaseManagerObj.getActualUserDb("displayname")
        firebasemanager.user.dineroActual = FirebaseManagerObj.getActualUserDb("actualMoney").toInt()
    }

    suspend fun getUser(): Usuario {
        if (firebasemanager.readUser().nombreUsuario == "...") {
            return Usuario("Invitado", 100000)
        }

        return firebasemanager.user
    }

    suspend fun setUser(email:String, password:String): Usuario? {
        if (!firebasemanager.loginUser(email, password)){
            return null
        }
        return firebasemanager.user
    }

    suspend fun setNewUser(email:String, password:String): Usuario? {
        if (!firebasemanager.registerUser(email, password)){
            return null
        }

        return firebasemanager.user
    }

    suspend fun signOutUser() {
        auth.signOut()
        firebasemanager.user = Usuario("Invitado", 100000)
    }

    suspend fun getIsJVMorJS():Boolean {
        return FirebaseManagerObj.isJVMorJS()
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
