package mvc

import dev.gitlive.firebase.auth.*
import dev.gitlive.firebase.database.*

class FirebaseManager(val auth: FirebaseAuth) {
    var user: Usuario = Usuario("...", 0)

    suspend fun readUser(): Usuario {
        println(auth.currentUser?.uid)
        if (auth.currentUser?.uid == null){
            return user
        }
        val displayName = FirebaseManagerObj.getActualUserDb("displayname")
        val actualMoney = FirebaseManagerObj.getActualUserDb("actualMoney")
        user = Usuario(if (displayName == "") "Invitado" else displayName, if(actualMoney == "") 100000 else actualMoney.toInt())
        return user
    }

    suspend fun loginUser(email:String, password:String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password)
            this.user.nombreUsuario = "Logged"
//            val displayName = databaseRTDB.reference().child("users")
//            println(displayName.key)

//            databaseRTDB.android.reference.child("users").child(actualUid).get()
//            this.user = Usuario(auth.currentUser?.displayName.toString(), 10000)
//            this.user = Usuario("TESt", 10000)
            true
        } catch (e: FirebaseAuthInvalidCredentialsException){
            println("ERROR as $e")
            false
        }
    }

    suspend fun registerUser(email:String, password:String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, password)//.user?.updateProfile(displayName)

//            this.user = Usuario("...", 10000)
            true
        } catch (e: FirebaseAuthException) {
            println("ERROR as $e")
            false
        }
    }
}

