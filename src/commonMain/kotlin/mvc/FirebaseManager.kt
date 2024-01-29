package mvc

import dev.gitlive.firebase.auth.*

class FirebaseManager(val auth: FirebaseAuth) {
    lateinit var user: Usuario

    fun readUser(): Boolean {
        return try {
            val displayName = auth.currentUser?.displayName
            if (displayName != null) {
                this.user = Usuario(displayName, 10000)
                true
            } else {
                false
            }
        } catch (e: NullPointerException) {
            false
        }
    }

    suspend fun loginUser(email:String, password:String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password)
            this.user = Usuario(auth.currentUser?.displayName.toString(), 10000)
            true
        } catch (e: FirebaseAuthInvalidCredentialsException){
            false
        }
    }

    suspend fun registerUser(email:String, password:String, displayName:String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, password).user?.updateProfile(displayName)
            this.user = Usuario(displayName, 10000)
            true
        } catch (e: FirebaseAuthException) {
            false
        }
    }
}

