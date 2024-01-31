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
import korlibs.io.file.std.*
import korlibs.io.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.*
import kotlin.coroutines.*

actual object FirebaseManagerObj {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRTDB: FirebaseDatabase
    private lateinit var firebaseInit: FirebaseApp
    private var applicationContext: Application = Application()
    private var actualUid: String = ""

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
            texts["gaTrakingId"],
            projectId = "ruletaadriangabriel"
        )
        firebaseInit = Firebase.initialize(applicationContext,options)

    }

    actual suspend fun startFirebaseRealtimeDB() {
        databaseRTDB = Firebase.database(firebaseInit)
    }

    actual suspend fun startFirebaseAuth():FirebaseAuth? {
        auth = Firebase.auth
        actualUid = auth.currentUser?.uid.toString()
        //println(auth)
//        auth.signInWithEmailAndPassword("testako@test.com", "test123")
//        auth.signInWithEmailAndPassword("newtest@test.com", "test123")
//        println(auth.currentUser?.uid)

        return auth
    }

    actual suspend fun getActualUserDb(): String {
        actualUid = auth.currentUser?.uid.toString()

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

    actual suspend fun createUser(email: String, displayName: String) {
        try {
            actualUid = auth.currentUser?.uid.toString()
            val userReference = databaseRTDB.android.reference.child("users").child(actualUid)
            val userData = mapOf("email" to email, "displayname" to displayName)

            userReference.setValue(userData).await()

            Log.i("firebase", "User created successfully")
        } catch (e: Exception) {
            Log.e("firebase", "Error creating user", e)
        }
    }
    actual suspend fun isJVMorJS():Boolean{
        return true
    }

    actual suspend fun getDisplayName() {
    }
}

