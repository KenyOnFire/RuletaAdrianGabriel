import android.app.*
import android.util.*
import com.google.firebase.*
import com.google.firebase.database.*
import dev.gitlive.firebase.*
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.auth.*
import dev.gitlive.firebase.database.*
import dev.gitlive.firebase.database.FirebaseDatabase
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

    actual suspend fun getActualUserDb(pathString: String): String {
        actualUid = auth.currentUser?.uid.toString()

        return try {
            val deferred = CompletableDeferred<String>()
            val userReference = databaseRTDB.android.reference.child("users").child(actualUid).child(pathString)
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

    actual suspend fun createUser(nombreUsuario: String, dineroActual: Int, email: String) {
        //TODO for add support in JVM (SDK TODO)
    }

    actual suspend fun isJVMorJS():Boolean{
        return true
    }

    actual suspend fun getAllUsersDb(pathString: String): List<String> {
        val usersList = mutableListOf<String>()
        try {
            val deferred = CompletableDeferred<List<String>>()
            val usersReference = databaseRTDB.android.reference.child("users")
            usersReference.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(p0: com.google.firebase.database.DataSnapshot) {
                    for (userSnapshot in p0.children) {
                        val displayName = userSnapshot.child(pathString).getValue(String::class.java)
                        Log.i("firebase", "Got value: $displayName")
                        displayName?.let { usersList.add(it) }
                    }
                    deferred.complete(usersList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase", "Error getting data", error.toException())
                    deferred.completeExceptionally(error.toException())
                }
            })
            return deferred.await()
        } catch (e: Exception) {
            Log.e("firebase", "Error in getAllUsersDb", e)
            return emptyList()
        }
    }

    actual suspend fun modifyActualMoney(money: String) {
        try {
            val actualUid = auth.currentUser?.uid.toString()
            val userReference = databaseRTDB.android.reference.child("users").child(actualUid)
            val updates = HashMap<String, Any>()
            updates["actualMoney"] = money
            userReference.updateChildren(updates).await()
            Log.i("firebase", "actualMoney updated successfully to $money")
        } catch (e: Exception) {
            Log.e("firebase", "Error modifying actualMoney", e)
        }
    }


}

