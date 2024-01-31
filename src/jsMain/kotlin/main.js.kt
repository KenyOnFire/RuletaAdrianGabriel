import dev.gitlive.firebase.auth.*
import dev.gitlive.firebase.database.*

actual object FirebaseManagerObj {

    actual suspend fun startFirebaseAuth(): FirebaseAuth? {
        TODO("Not yet implemented")
    }

    actual suspend fun getDisplayName() {
    }

    actual suspend fun startFirebaseRealtimeDB(){
        TODO("Not yet implemented")
    }

    actual suspend fun startFB() {
    }

    actual suspend fun isJVMorJS(): Boolean {
        return true
    }

    actual suspend fun createUser(email: String, displayName: String) {
    }

    actual suspend fun getActualUserDb(): String {
        TODO("Not yet implemented")
    }
}
