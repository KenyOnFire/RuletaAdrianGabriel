import korlibs.korge.annotations.*
import korlibs.korge.input.*
import korlibs.korge.scene.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import mvc.*

class Login(private var controller: Controller) : Scene() {
    @OptIn(KorgeExperimental::class)
    override suspend fun SContainer.sceneMain() {
        container {
            val emailInput = uiTextInput("", Size(180, 24)).position(0,0)
            emailInput.scale = 1.5
            val password = uiTextInput("", Size(180, 24)).position(0,100)
            password.scale = 1.5
            uiButton("login").position(0,300).onClick {
                controller.setUser(emailInput.text, password.text)//"apendicitis@aguda.com", "test123"
                controller.getUserRealtimeDatabase()
                val user = controller.getUser()!!
                sceneContainer.changeTo { GameView(GameModel(user.nombreUsuario, user.dineroActual)) }
//                controller.setNewUserRealtimeDatabase("apendicitis@aguda.com", "queansiedadporfavorrrr")

            }
        }.centerOnStage()
    }
}
