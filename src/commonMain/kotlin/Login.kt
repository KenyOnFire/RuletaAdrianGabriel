import korlibs.image.color.*
import korlibs.korge.annotations.*
import korlibs.korge.input.*
import korlibs.korge.scene.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import dev.gitlive.firebase.auth.*
import mvc.*

class Login(
    private var sceneSwitcher: SceneSwitcher
) : Scene() {
    @OptIn(KorgeExperimental::class)
    override suspend fun SContainer.sceneMain() {
        container {
            val emailInput = uiTextInput("", Size(500, 75)).position(0,0)
            emailInput.scale = 1.5
            emailInput.textSize = 40.0
            val password = uiTextInput("", Size(500, 75)).position(0,200)
            password.scale = 1.5
            password.textSize = 40.0
            val login = uiButton("login")
            login.size(Size(400,75))
            login.position((this.width-login.width)/2,this.height+login.height)
            login.textSize = 33.0
            login.onClick {
                sceneSwitcher.controller.setUser(emailInput.text, password.text) // DEBUG
                sceneSwitcher.controller.getUserRealtimeDatabase()
                sceneSwitcher.userIsRegisterOrLogged = true
                sceneSwitcher.loadNewGame()
                sceneSwitcher.goMenu()
            }
        }.centerOnStage()
        uiButton {
            text="MENU"
            onClick {
                sceneSwitcher.goMenu()
            }
            scale = 2.0
        }.position(750, height/1.10)
    }
}
