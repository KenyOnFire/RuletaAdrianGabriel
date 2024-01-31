package mvc

import dev.gitlive.firebase.auth.*
import dev.gitlive.firebase.database.*

class FirebaseManager(val auth: FirebaseAuth) {
    var user: Usuario = Usuario("...", 10000)

    fun readUser(): Boolean {
        return try {
            true
        } catch (e: NullPointerException) {
            false
        }
    }

    suspend fun loginUser(email:String, password:String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password)
//            val displayName = databaseRTDB.reference().child("users")
//            println(displayName.key)

            //.android.reference.child("users").child(actualUid).get()
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

