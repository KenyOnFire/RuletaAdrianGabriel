import dev.gitlive.firebase.auth.*

expect object FirebaseManagerObj {
    suspend fun startFirebaseAuth(): FirebaseAuth?
    suspend fun startFirebaseRealtimeDB()
    suspend fun startFB()
    suspend fun isJVMorJS():Boolean
    suspend fun createUser(nombreUsuario: String, dineroActual: Int, email: String)
    suspend fun getActualUserDb(pathString: String): String
    suspend fun getAllUsersDb(pathString: String): List<String>
    suspend fun modifyActualMoney(money: String)
}
