import android.content.ContentValues.TAG
import android.util.*
import com.google.android.gms.common.api.*
import dev.gitlive.firebase.*
import dev.gitlive.firebase.auth.*
import dev.gitlive.firebase.database.*
import korlibs.datastructure.*
import korlibs.io.android.*
import korlibs.io.file.std.*
import korlibs.io.util.*
import korlibs.io.util.i18n.*
import kotlinx.coroutines.*

actual object FirebaseManagerObj {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRTDB: FirebaseDatabase
    private lateinit var firebaseInit: FirebaseApp
    actual suspend fun startFB(){
        val texts = resourcesVfs["apikeys.properties"].loadProperties()
        val options = FirebaseOptions(
            texts["applicationId"]!!,
            texts["apiKey"]!!,
            texts["databaseURL"],
            texts["gaTrakingId"]
        )
        firebaseInit = Firebase.initialize(androidContext(),options)
    }

    actual suspend fun startFirebaseRealtimeDB() {
        databaseRTDB = Firebase.database(firebaseInit)
    }

    actual suspend fun startFirebaseAuth():FirebaseAuth? {
        auth = Firebase.auth
        return auth
    }

    actual suspend fun getDisplayName() {
        val test = databaseRTDB.android.reference.child("usuarios").child("email").get()
        test.addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    actual suspend fun isJVMorJS(): Boolean {
        return false
    }

    actual suspend fun createUser(email: String, displayName: String) {
    }

    actual suspend fun getActualUserDb(): String {
        val actualUid = auth.currentUser?.uid.toString()

        return try {
            val deferred = CompletableDeferred<String>()
            val userReference = databaseRTDB.android.reference.child("users").child(actualUid).child("displayname")
            userReference.get().addOnSuccessListener {
                val displayName = it.value as? String
                Log.i("firebase", "Got value: $displayName")
                deferred.complete(displayName ?: "")
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
                deferred.completeExceptionally(it)
            }
            deferred.await()
        } catch (e: Exception) {
            Log.e("firebase", "Error in getActualUserDb", e)
            ""
        }
    }

}
