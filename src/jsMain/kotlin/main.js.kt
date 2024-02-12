import dev.gitlive.firebase.auth.*

actual object FirebaseManagerObj {
    actual suspend fun startFirebaseAuth(): FirebaseAuth? {
        TODO("Not yet implemented")
    }

    actual suspend fun startFirebaseRealtimeDB() {
    }

    actual suspend fun startFB() {
    }

    actual suspend fun isJVMorJS(): Boolean {
        TODO("Not yet implemented")
    }

    actual suspend fun createUser(nombreUsuario: String, dineroActual: Int, email: String) {
    }

    actual suspend fun getActualUserDb(pathString: String): String {
        TODO("Not yet implemented")
    }

    actual suspend fun getAllUsersDb(pathString: String): List<String> {
        TODO("Not yet implemented")
    }

    actual suspend fun modifyActualMoney(money: String) {
    }
}
