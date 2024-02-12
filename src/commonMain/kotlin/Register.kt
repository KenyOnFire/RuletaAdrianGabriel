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

class Register(
    private var sceneSwitcher: SceneSwitcher
) : Scene() {
    @OptIn(KorgeExperimental::class)
    override suspend fun SContainer.sceneMain() {
        container {

            val nicknameInput = uiTextInput("", Size(500, 75)).position(0,0)
            nicknameInput.scale = 1.5
            nicknameInput.textSize = 40.0
            val emailInput = uiTextInput("", Size(500, 75)).position(0,200)
            emailInput.scale = 1.5
            emailInput.textSize = 40.0
            val password = uiTextInput("", Size(500, 75)).position(0,400)
            password.scale = 1.5
            password.textSize = 40.0
            val register = uiButton("Register")
            register.size(Size(400,75))
            register.position((this.width-register.width)/2,this.height+register.height)
            register.textSize = 33.0
            register.onClick {
                sceneSwitcher.controller.setNewUser(emailInput.text, password.text)
                sceneSwitcher.controller.setNewUserRealtimeDatabase(emailInput.text, nicknameInput.text)
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
