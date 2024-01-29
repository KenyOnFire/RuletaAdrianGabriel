import korlibs.image.bitmap.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.input.*
import korlibs.korge.scene.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.korge.view.onClick
import korlibs.math.geom.*
import mvc.*

class Menu(var controller: Controller) : Scene() {
    private var NBOTONES:Int = 3
    private var NBOTONESARRIBA:Int= 2
    private var userIsRegisterOrLogged:Boolean = false

    override suspend fun SContainer.sceneMain() {
        val userGetted = controller.getUser()
        if (userGetted != null){
            userIsRegisterOrLogged = true

        }
        val resourceWallpaper = resourcesVfs["fondoLogin.jpg"].readBitmap()

        // We load a container that will contain the background
        container {
            val bg = uiImage(
                views.virtualSizeDouble,
                resourceWallpaper.slice(),
                scaleMode = ScaleMode.COVER,
                contentAnchor = Anchor.CENTER
            )
            onStageResized { _, _ ->
                bg.width = views.actualVirtualWidth.toDouble()
                bg.height = views.actualVirtualHeight.toDouble()
            }
        }.name("Background")


        val image = image(resourcesVfs["logoApp.png"].readBitmap())

        //Contenedor 1 para botones de abajo
        container {
            val textosBotones = arrayListOf<String>("")
            for (n in 0 until NBOTONES) {
                val xCoordinate = 450 * n // Establecemos la cordenada x basandonos en la columna
                val yCoordinate = (120 * (n / 4)) // Establecemos la cordenada y basandonos en la fila


                // Cargamos la imagen una a una en la vista del contenedor
                val image = image(resourcesVfs["botonMenu.png"].readBitmap()).scale(0.60).position(xCoordinate, yCoordinate)
                when(n) {
                    0 -> text("JUGAR",50).xy(98, 60)
                    1 -> {
                        text(if userIsRegisterOrLogged "LOGOUT" else "LOGIN",50).xy(558, 60).onClick{
                            if (userIsRegisterOrLogged) {
                                controller.signOutUser()
                                sceneContainer.changeTo{Menu(controller)}
                            } else {
                                sceneContainer.changeTo { Login(controller) }
                            }
                        }
                    }
                    2 -> text("SALIR",50).xy(1008, 60)
                }
            }
        } .centerOnStage().y = this.scaledHeight - 200.0

        //Creamos un segundo contenedor para la parte de arriba
        container {
            for (n in 0 until NBOTONESARRIBA) {
                val xCoordinate = 1300 * n // Establecemos la cordenada x basandonos en la columna
                val yCoordinate = (1376 * (n / 4)) // Establecemos la cordenada y basandonos en la fila


                // Cargamos la imagen una a una en la vista del contenedor
                val image = image(resourcesVfs["botonMenu.png"].readBitmap()).scale(0.35).position(xCoordinate, yCoordinate)
                when(n) {
                    0 -> text("CREDITOS",30).xy(34, 34)
                    1 -> text("RANKING",30).xy(1344, 34)
                }
            }
        } .centerOnStage().y = this.scaledHeight - 950.0
    }
}
