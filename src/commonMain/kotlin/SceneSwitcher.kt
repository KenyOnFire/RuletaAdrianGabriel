import korlibs.korge.scene.*
import mvc.*

class SceneSwitcher(private var sceneContainer: SceneContainer, var isJVMorJS: Boolean, controller: Controller) {
    var userIsRegisterOrLogged: Boolean = false
    private val sceneMap: MutableMap<String, Scene> = mutableMapOf()
    var controller: Controller = controller
    private var menu: Scene = Menu(this, isJVMorJS)

    fun removeScene(name: String) {
        sceneMap.remove(name)
    }

    fun addScene(name: String, scene: Scene) {
        sceneMap[name] = scene
    }

    suspend fun switchScene(name: String) {
        sceneContainer.changeTo{ sceneMap[name]!! }
    }

    fun loadNewGame() {
        sceneMap.remove("game")
        sceneMap["game"] = Game(this)
    }

    suspend fun loadUser(): Usuario {
        return controller.getUser()
    }

    suspend fun goMenu(){
        menu = Menu(this, isJVMorJS)
        sceneContainer.changeTo{ menu }
    }

}
