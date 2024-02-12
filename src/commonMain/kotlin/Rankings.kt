import korlibs.image.color.*
import korlibs.korge.annotations.*
import korlibs.korge.input.*
import korlibs.korge.scene.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import dev.gitlive.firebase.auth.*
import korlibs.image.bitmap.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import mvc.*

class Rankings(
    private var sceneSwitcher: SceneSwitcher
) : Scene() {
    @OptIn(KorgeExperimental::class)
    override suspend fun SContainer.sceneMain() {
        val image = uiImage(
            views.virtualSizeDouble,
            resourcesVfs["backgrounds/fondoTablero.png"].readBitmap().slice(),
            scaleMode = ScaleMode.COVER,
            contentAnchor = Anchor.CENTER
        )
        onStageResized { _, _ ->
            image.width = views.actualVirtualWidth.toDouble()
            image.height = views.actualVirtualHeight.toDouble()
        }

        val data: List<Pair<String, String>> = sceneSwitcher.controller.getAllUsers()
        val sortedData = data.sortedByDescending { it.second.toInt() }

        container {
            sortedData.forEachIndexed { index, (name, money) ->
                text("${index + 1}. $name: $money$") { scale = 4.0 }.position(0, index * 80) // Display each name and corresponding money
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
