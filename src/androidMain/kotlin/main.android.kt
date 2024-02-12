import android.content.ContentValues.TAG
import android.util.*
import com.google.android.gms.common.api.*
import com.google.firebase.database.*
import dev.gitlive.firebase.*
import dev.gitlive.firebase.auth.*
import dev.gitlive.firebase.database.*
import dev.gitlive.firebase.database.DataSnapshot
import dev.gitlive.firebase.database.FirebaseDatabase
import korlibs.datastructure.*
import korlibs.io.android.*
import korlibs.io.file.std.*
import korlibs.io.util.*
import korlibs.io.util.i18n.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.*

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

//    actual suspend fun getDisplayName() {
//        val test = databaseRTDB.android.reference.child("usuarios").child("email").get()
//        test.addOnSuccessListener {
//            Log.i("firebase", "Got value ${it.value}")
//        }.addOnFailureListener{
//            Log.e("firebase", "Error getting data", it)
//        }
//    }

    actual suspend fun isJVMorJS(): Boolean {
        return false
    }

    actual suspend fun createUser(nombreUsuario: String, dineroActual: Int, email: String) {
        val actualUid = auth.currentUser?.uid.toString()

        try {
            val userReference = databaseRTDB.android.reference.child("users").child(actualUid)

            // Save user data at corresponding paths
            userReference.child("displayname").setValue(nombreUsuario).await()
            userReference.child("actualMoney").setValue(dineroActual.toString()).await()
            userReference.child("email").setValue(email).await()

            Log.i("firebase", "User data saved successfully")
        } catch (e: Exception) {
            Log.e("firebase", "Error saving user data", e)
            // Handle the error appropriately, e.g., retry, notify user, etc.
        }
    }

    actual suspend fun getActualUserDb(pathString: String): String {
        val actualUid = auth.currentUser?.uid.toString()

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
