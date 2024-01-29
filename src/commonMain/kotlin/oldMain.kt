//import korlibs.time.*
//import korlibs.korge.*
//import korlibs.korge.scene.*
//import korlibs.korge.tween.*
//import korlibs.korge.view.*
//import korlibs.image.color.*
//import korlibs.image.format.*
//import korlibs.io.file.std.*
//import korlibs.io.util.*
//import korlibs.io.util.i18n.*
//import korlibs.korge.annotations.*
//import korlibs.korge.input.*
//import korlibs.korge.ui.*
//import korlibs.korge.view.container
//import korlibs.math.*
//import korlibs.math.geom.*
//import korlibs.math.interpolation.*
//
//suspend fun main() = Korge(
//    windowSize = Size(2220, 1080),
//    virtualSize = Size(2220, 1080),
//    backgroundColor = Colors["#6e00b1"],
//    displayMode = KorgeDisplayMode.DEFAULT.copy(scaleAnchor = Anchor.TOP_LEFT, clipBorders = false),
//) {
//    sceneContainer().changeTo { TestScene() }
//}
//
//class MyScene : Scene() {
//    private lateinit var ruleta: Image
//    private var ruletaMoving: Boolean = false
//    private var apuestaTablero: Boolean = false
//    private lateinit var tablero: Image
//    private lateinit var wallpaper: Container
//    private lateinit var mainContainer: Container
//    private lateinit var backgroundContainer: Container
//    private lateinit var fichasDraggableContainer: Container
//    private lateinit var notifyContainer: Container
//    private lateinit var notifySolid: SolidRect
//    private lateinit var apuestaTableroImg:Image
//    private lateinit var apuestasContainer: Container
//    private lateinit var apuestasTextContainer:Container
//    private lateinit var fichasStaticContainer:Container
//    private lateinit var textsProps: Props
//    private var listUiImages: MutableList<Ficha> = mutableListOf()
//    private var listFichasApostada: MutableList<Ficha> = mutableListOf()
//
//    @OptIn(KorgeExperimental::class)
//    override suspend fun SContainer.sceneMain() {
//        textsProps = resourcesVfs["${Language.CURRENT.iso6391}.properties"].loadProperties()
//
//        backgroundContainer = this.container {
//            wallpaper = uiImage(
//                views.virtualSizeDouble,
//                KR.fondo.readSlice(),
//                scaleMode = ScaleMode.COVER,
//                contentAnchor = Anchor.CENTER
//            )
//            onStageResized { _, _ ->
//                wallpaper.width = views.actualVirtualWidth.toDouble()
//                wallpaper.height = views.actualVirtualHeight.toDouble()
//            }
//        }
//
//        mainContainer = this.container {
//            ruleta = image(resourcesVfs["ruleta.png"].readBitmap()) {
//                rotation = ((+0).degrees)
//                anchor(.5, .5)
//                position(views.virtualLeft + 500, 550)
//            }
//            tablero = image(resourcesVfs["tablero.png"].readBitmap()) {
//                anchor(.0, .0)
//                scale(0.5)
//                position(views.virtualRight - 1350, 300)
//            }
//            ruleta.onClick {
//                if (!ruletaMoving){
//                    simulateInfinityRotating(ruleta, (10..359).random())
//                    spawnNotification()
//                }
//            }
//        }
//
//        fichasDraggableContainer = this.container {
//            val listFichas = List(8) { it + 1 }
//
//            var posFichaX = 1920.0
//            var posFichaY = 920.0
//
//            val createFicha: (Int) -> Ficha = { Ficha(it) }
//            val listUiImages = listFichas.map(createFicha).toMutableList()
//
//            listUiImages.forEachIndexed { index, ficha ->
//                with(ficha) {
//                    setImage("ficha${index + 1}.png", views.virtualRight, posFichaY)
//                    addChild(getImage())
//                    posFichaX -= 125
//                    posFichaY -= (index.toDouble() == listUiImages.size / 2.0).takeIf { it }?.run {
//                        posFichaX += 500.0
//                        120.0
//                    } ?: 0.0
//
//                    val imageData = getImage()
//                    imageData.size = Size(100, 100)
//                    ficha.posFichaX = posFichaX
//                    ficha.posFichaY = posFichaY
//                    imageData.position(views.virtualLeft + posFichaX, posFichaY)
//
//                    var actualText: Text? = null
//                    imageData.draggableCloseable(selector = imageData, autoMove = false) { info: DraggableInfo ->
//                        info.view.pos = info.viewNextXY
//                        if (info.end && info.view.x > tablero.x && info.view.y > tablero.y && info.view.y < 565) {
//                            val convertedNumber = convertCoordinatesToNumber(info.view.x, info.view.y)
//                            if (convertedNumber >= 0) {
//                                ficha.numTablero = convertedNumber
//                                listFichasApostada.add(ficha)
//                                apuestaTablero = true
//                                info.view.pos = calculatePoint(convertedNumber, info)
//                                actualText = text("Test: ${ficha.numTablero}") {
//                                    textSize = 44.0
//                                }
//                                apuestasTextContainer.addChild(actualText!!.position(950, 1100))
//                            }
//                        } else {
//                            actualText?.removeFromParent()
//                            listFichasApostada.remove(ficha)
//                            if (info.end) {
//                                info.view.pos = Point(ficha.posFichaX, ficha.posFichaY)
//                            }
//                        }
//                        imageData.onMove {
//                            if (apuestaTablero) {
//                                actualText?.let { spawnApuestasTablero(it) }
//                            }
//                            if (listFichasApostada.isEmpty()) {
//                                apuestaTablero = false
//                                actualText?.let { deSpawnApuestasTablero(it) }
//                            }
//                        }
//                    }
//                }
//            }
//        }
////        fichasStaticContainer = this.container {
//////            listUiImages.forEach {
//////                addChild(it.getImage().clone())
//////            }
////        }
//
//        notifyContainer = this.container {
//            notifySolid = solidRect(400, 100, Colors.WHITE)
//            notifySolid.position(20,-100)
//        }
//
//        apuestasContainer = this.container {
//            apuestaTableroImg = image(resourcesVfs["apuestaTablero.png"].readBitmap()) {
//                position(910,1300)//810
//                size(Size(600,210))
//                alpha = 0.0
//            }
//        }
//
//        apuestasTextContainer = this.container {}
//
//    }
//
//    private suspend fun spawnApuestasTablero(actualText:Text){
//        apuestasContainer.tween(actualText::y[900], time = 0.1.seconds, easing = Easing.EASE_IN,)
//        apuestasContainer.tween(apuestaTableroImg::y[810], time = 0.01.seconds, easing = Easing.EASE_IN,)
//        apuestaTableroImg.alpha = 0.8
//    }
//
//    private suspend fun deSpawnApuestasTablero(actualText:Text){
//        apuestasContainer.tween(apuestaTableroImg::y[1300], time = 0.2.seconds, easing = Easing.EASE_OUT,)
//        apuestaTableroImg.alpha = 0.0
//    }
//
//    private fun calculatePoint(convertedNumber: Int, info: DraggableInfo): Point {
//        val alignmentX = when (convertedNumber) {
//            0 -> 900.0
//            1, 2, 3 -> 980.0
//            4, 5, 6 -> 1069.0
//            7, 8, 9 -> 1154.0
//            10, 11, 12 -> 1239.0
//            13, 14, 15 -> 1327.0
//            16, 17, 18 -> 1415.0
//            19, 20, 21 -> 1500.0
//            22, 23, 24 -> 1586.0
//
//            25, 26, 27 -> 1671.0
//            28, 29, 30 -> 1759.0
//            31, 32, 33 -> 1843.0
//            34, 35, 36 -> 1930.0
//            else -> return info.view.pos // Mantain the original position if the number doesn't match any condition
//        }
//
//        val alignmentY = when (convertedNumber) {
//            0 -> 420.0
//            1, 4, 7, 10, 13, 16, 19, 22, 25 ,28, 31, 34 -> 530.0
//            2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35 -> 425.0
//            3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36 -> 320.0
//            else -> return info.view.pos // Mantain the original position if the number doesn't match any condition
//        }
//
//        return Point(info.viewNextXY.x.nearestAlignedTo(alignmentX), info.viewNextXY.y.nearestAlignedTo(alignmentY))
//    }
//
//    private fun convertCoordinatesToNumber(positionX: Double, positionY: Double): Int {
//        return when {
//            positionX in 850.0..940.0 && positionY in 300.0..570.0 -> 0
//            positionX in 950.0..1015.0 -> mapPositionYInRange(positionY, 1, 2, 3)
//            positionX in 1040.0..1095.0 -> mapPositionYInRange(positionY, 4, 5, 6)
//            positionX in 1130.0..1175.0 -> mapPositionYInRange(positionY, 7, 8, 9)
//            positionX in 1219.0..1258.0 -> mapPositionYInRange(positionY, 10, 11, 12)
//            positionX in 1299.0..1355.0 -> mapPositionYInRange(positionY, 13, 14, 15)
//            positionX in 1385.0..1440.0 -> mapPositionYInRange(positionY, 16, 17, 18)
//            positionX in 1472.0..1530.0 -> mapPositionYInRange(positionY, 19, 20, 21)
//            positionX in 1557.5..1618.0 -> mapPositionYInRange(positionY, 22, 23, 24)
//            positionX in 1646.0..1697.0 -> mapPositionYInRange(positionY, 25, 26, 27)
//            positionX in 1733.0..1790.0 -> mapPositionYInRange(positionY, 28, 29, 30)
//            positionX in 1815.0..1871.0 -> mapPositionYInRange(positionY, 31, 32, 33)
//
//            positionX in 1900.0..1955.0 -> mapPositionYInRange(positionY, 34, 35, 36)
//
//            else -> -1
//        }
//    }
//
//    private fun mapPositionYInRange(positionY: Double, upperRange: Int, middleRange: Int, lowerRange: Int): Int {
//        return when (positionY) {
//            in 480.0..990.0 -> upperRange
//            in 380.0..470.0 -> middleRange
//            in 280.0..350.0 -> lowerRange
//            else -> -1
//        }
//    }
//
//    private suspend fun spawnNotification() {
//        notifySolid.visible = true
//        notifySolid.tween(
//            notifySolid::y[20], time = 0.4.seconds, easing = Easing.EASE_IN_OUT_ELASTIC,
//        )
//        delay(1500.milliseconds)
//        notifySolid.tween(
//            notifySolid::y[-100], time = 0.4.seconds, easing = Easing.EASE_IN_OUT_ELASTIC,
//        )
//        ruletaMoving = false
//    }
//
//    private suspend fun simulateInfinityRotating(ruleta:Image, randomFin:Int){
//        ruletaMoving = true
//        for (i in 1..10){
//            ruleta.tween(ruleta::rotation[(+180).degrees], time = 0.1.seconds, easing = Easing.EASE_IN)
//            ruleta.tween(ruleta::rotation[(+359).degrees], time = 0.1.seconds, easing = Easing.EASE_IN)
//            ruleta.rotation((0).degrees)
//        }
//        ruleta.tween(ruleta::rotation[(randomFin).degrees], time = 0.3.seconds, easing = Easing.EASE_OUT_BACK)
//    }
//
//}
//
//
//class TestScene : Scene() {
//    private var fichasContainer: Container = Container()
//    override suspend fun SContainer.sceneMain(){
//        generateFichas()
//    }
//    private suspend fun generateFichas(){
//        this.sceneContainer.removeChildren()
//        val listFichas = List(8) { it + 1 }
//
//        var posFichaX = 1920.0
//        var posFichaY = 920.0
//
//        val createFicha: (Int) -> Ficha = { Ficha(it) }
//        val listUiImages = listFichas.map(createFicha).toMutableList()
//        listUiImages.forEachIndexed { index, ficha ->
//            // Adjust X and Y positions
//            posFichaX -= 125
//            posFichaY -= if (index.toDouble() == listUiImages.size / 2.0) {
//                posFichaX += 500.0
//                120.0
//            } else 0.0
//
//            // Set image for the current ficha
//            val imageName = "ficha${index + 1}.png"
//            val imageX = views.virtualLeft + posFichaX
//            val imageY = posFichaY
//
//            ficha.setImage(imageName, imageX, imageY)
//
//            // Get image data for the class Ficha
//            val imageData:Image = ficha.getImage()
//            ficha.setDraggable(fichasContainer)
//            // Add the image to the container
//            fichasContainer.addChild(ficha.getImage())
//        }
//        this.sceneContainer.addChild(fichasContainer)
//    }
//}
