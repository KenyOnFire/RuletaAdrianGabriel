import dev.gitlive.firebase.auth.*
import dev.gitlive.firebase.database.*
import korlibs.image.color.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.math.geom.*
import mvc.*

expect object FirebaseManagerObj {
    suspend fun startFirebaseAuth(): FirebaseAuth?
    suspend fun getDisplayName()
    suspend fun startFirebaseRealtimeDB()
    suspend fun startFB()
    suspend fun isJVMorJS():Boolean
    suspend fun createUser(email: String, displayName: String)
    suspend fun getActualUserDb(): String
}

suspend fun main() = Korge(
    windowSize = Size(2220, 1080),
    virtualSize = Size(2220, 1080),
    backgroundColor = Colors["#6e00b1"],
    displayMode = KorgeDisplayMode.DEFAULT.copy(scaleAnchor = Anchor.TOP_LEFT, clipBorders = false),
) {
    val controller = Controller()
    controller.launchFirebase()
    val sceneContainer = sceneContainer()
//    controller.signOutUser()
    println(FirebaseManagerObj.isJVMorJS())
    if (!FirebaseManagerObj.isJVMorJS()){
        sceneContainer.changeTo { Menu(controller) }
        return@Korge
    }

    sceneContainer.changeTo { Login(controller) }

//    val gameModel = GameModel()
//    gameModel.userName = "AdrianQR"
//    gameModel.money = 10000
//    sceneContainer.changeTo { GameView(gameModel) }

//    TestFirebase().test()
//    sceneContainer.changeTo { UserSelector() }
//    val user = Usuario("AdrianQR", 200)
//    val gameModel = GameModel()
//    gameModel.userName = "AdrianQR"
//    gameModel.money = 10000
//    sceneContainer.changeTo { GameView(gameModel) }
//    val userGetted = controller.getUser()
//    if (userGetted != null) {
//        sceneContainer.changeTo { GameView(GameModel(userGetted.nombreUsuario, userGetted.dineroActual)) }
    //} //else {
//        sceneContainer.changeTo{LoginTestWindow(controller)}
//    }

}
