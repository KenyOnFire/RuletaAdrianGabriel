
import korlibs.audio.sound.*
import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.annotations.*
import korlibs.korge.input.*
import korlibs.korge.scene.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.korge.view.onClick
import korlibs.math.geom.*
import korlibs.time.*

class Menu(private var sceneSwitcher: SceneSwitcher, var isJVMorJS: Boolean): Scene() {

    @OptIn(KorgeExperimental::class)
    override suspend fun SContainer.sceneMain() {
        val music = resourcesVfs["songs/sonidoFondoMenu.mp3"].readMusic()
        val channel = music.play()
        channel.sound
        channel.volume = 0.2
        if (sceneSwitcher.loadUser().nombreUsuario != "Invitado") {
            sceneSwitcher.userIsRegisterOrLogged = true
        }
        val image = uiImage(
            views.virtualSizeDouble,
            resourcesVfs["backgrounds/menuBackground.jpg"].readBitmap().slice(),
            scaleMode = ScaleMode.COVER,
            contentAnchor = Anchor.CENTER
        )
        onStageResized { _, _ ->
            image.width = views.actualVirtualWidth.toDouble()
            image.height = views.actualVirtualHeight.toDouble()
        }


        if (get("root").isEmpty()){
            container {
                container {
                    val buttonLabels = mutableListOf(
                        "JUGAR",
                        "RANKINGS",
                        if (sceneSwitcher.userIsRegisterOrLogged) "LOGOUT" else "LOGIN",
                        if (!isJVMorJS) "REGISTER" else null,
                        "SALIR").filterNotNull()
                    for ((index, label) in buttonLabels.withIndex()) {

                        uiButton {
                            text = label
                            scale = 3.0
                            xy(x + index * 350, 0)
                            bgColorOut = Colors.DARKORANGE
                            when (label) {
                                "JUGAR" -> {
                                    onClick {
                                        sceneSwitcher.switchScene("game")
                                    }
                                }
                                "RANKINGS" -> {
                                    onClick {
                                        sceneSwitcher.switchScene("rank")
                                    }
                                }
                                "LOGIN" -> {
                                    onClick {
                                        sceneSwitcher.switchScene("login")

                                    }
                                }
                                "LOGOUT" -> {
                                    onClick {
                                        sceneSwitcher.controller.signOutUser()
                                        sceneSwitcher.userIsRegisterOrLogged = false
                                        sceneSwitcher.loadNewGame()
                                        sceneSwitcher.goMenu()
                                    }
                                }
                                "REGISTER" -> {
                                    onClick {
                                        sceneSwitcher.switchScene("register")
                                    }
                                }
                            }
                        }
                    }
                }.name("belowButtons")
            }.centerOnStage().name("root")
        }

    }
}
