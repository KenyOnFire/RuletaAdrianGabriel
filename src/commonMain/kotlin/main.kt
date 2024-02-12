import korlibs.image.color.*
import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.math.geom.*
import mvc.*

suspend fun main() = Korge(
//    windowSize = Size(2220, 1080),
//    virtualSize = Size(2220, 1080),
    windowSize = Size(800, 600),
    virtualSize = Size(2220, 1080),
    backgroundColor = Colors["#000000"],
    displayMode = KorgeDisplayMode.DEFAULT.copy(scaleAnchor = Anchor.TOP_LEFT, clipBorders = false),
){


    val controller = Controller()
    controller.launchFirebase()

    val sceneSwitcher = SceneSwitcher(sceneContainer(), controller.getIsJVMorJS(), controller)
//    val userDEBUG =  //DEBUG
//    sceneContainer().changeTo{Game(sceneSwitcher)}//DEBUG

    sceneSwitcher.addScene("game", Game(sceneSwitcher))
    sceneSwitcher.addScene("rank", Rankings(sceneSwitcher))
    sceneSwitcher.addScene("login", Login(sceneSwitcher))
    sceneSwitcher.addScene("register", Register(sceneSwitcher))
    sceneSwitcher.goMenu()
}
