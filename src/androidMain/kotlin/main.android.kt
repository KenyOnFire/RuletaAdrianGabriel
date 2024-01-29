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

actual object firebaseManager {
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

    actual suspend fun startFirebaseRealtimeDB():FirebaseDatabase? {
        databaseRTDB = Firebase.database(firebaseInit)
        return databaseRTDB
    }

    actual suspend fun startFirebaseAuth():FirebaseAuth? {
        auth = Firebase.auth
        return auth
    }

    actual suspend fun testConnection() {
        val test = databaseRTDB.android.reference.child("usuarios").child("email").get()
        test.addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

}
