import android.app.*
import android.util.*
import com.google.firebase.*
import dev.gitlive.firebase.*
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.auth.*
import dev.gitlive.firebase.database.*
import dev.gitlive.firebase.initialize
import kotlin.coroutines.*

actual object firebaseManager {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRTDB: FirebaseDatabase
    private lateinit var firebaseInit: FirebaseApp
    private var applicationContext: Application = Application()

    actual suspend fun startFB(){
        FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
            val storage = mutableMapOf<String, String>()
            override fun store(key: String, value: String) = storage.set(key, value)
            override fun retrieve(key: String) = storage[key]
            override fun clear(key: String) { storage.remove(key) }
            override fun log(msg: String) = println(msg)
        })
        val texts = resourcesVfs["apikeys.properties"].loadProperties()
        val options = FirebaseOptions(
            texts["applicationId"]!!,
            texts["apiKey"]!!,
            texts["databaseURL"],
            texts["gaTrakingId"]
        )
        firebaseInit = Firebase.initialize(applicationContext,options)
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

