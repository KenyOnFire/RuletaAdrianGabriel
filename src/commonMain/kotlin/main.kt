import dev.gitlive.firebase.auth.*
import dev.gitlive.firebase.database.*
import korlibs.image.color.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.math.geom.*
import mvc.*

expect object firebaseManager {
    suspend fun startFirebaseAuth(): FirebaseAuth?
    suspend fun testConnection()
    suspend fun startFirebaseRealtimeDB(): FirebaseDatabase?
    suspend fun startFB()
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
    sceneContainer.changeTo { Menu(controller) }
//    TestFirebase().test()
//    sceneContainer.changeTo { UserSelector() }
//    val user = Usuario("AdrianQR", 200)
//    val gameModel = GameModel()
//    gameModel.userName = "AdrianQR"
//    gameModel.money = 10000
//    sceneContainer.changeTo { GameView(gameModel) }
    val userGetted = controller.getUser()
    if (userGetted != null) {
        sceneContainer.changeTo { GameView(GameModel(userGetted.nombreUsuario, userGetted.dineroActual)) }
    } //else {
//        sceneContainer.changeTo{LoginTestWindow(controller)}
//    }

}
