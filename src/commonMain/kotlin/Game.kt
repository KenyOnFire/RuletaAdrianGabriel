import korlibs.audio.sound.*
import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.io.async.*
import korlibs.io.async.async
import korlibs.io.file.std.*
import korlibs.korge.animate.*
import korlibs.korge.input.*
import korlibs.korge.internal.*
import korlibs.korge.render.SDFShaders.x
import korlibs.korge.scene.*
import korlibs.korge.tween.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.math.interpolation.*
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class Game(private var sceneSwitcher: SceneSwitcher) : Scene() {
    private var ruletaActiva = false

    private var actualUsername = ""
    private var actualMoney = 0
    private lateinit var bets: MutableBets
    private lateinit var game: GameModel

    private lateinit var fichasApostadas: Container
    private lateinit var numFichasApostadas: Container
    private val fichasApostadasContenedorTablero = arrayListOf<Container>()
    private val listaApuestasCantidad = arrayListOf<Int>()

    private lateinit var viewRuleta: Image
    private lateinit var viewBola: Image

    private val listaResultadosNumeros = arrayListOf<Int>()
    private var imageFichaApuestas = arrayListOf<View>()
    private val fichaViewLista = mutableListOf<FichaView>()

    private lateinit var elementsUser:List<View>
    private lateinit var coordenadas:MutableList<Casilla>

    private lateinit var userResources: UserResources
    private lateinit var gameResources: GameResources

    private lateinit var textResultado: Text

    @OptIn(KorgeInternal::class)
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
        val userData = sceneSwitcher.loadUser()
        game = GameModel(userData.nombreUsuario, userData.dineroActual)
        bets = MutableBets(game.money)

        actualUsername = game.userName
        actualMoney = game.money

        userResources = UserResources(
            resourcesVfs["user/imageUser.jpg"].readBitmap(),
            resourcesVfs["user/frameUser.png"].readBitmap()
        )
        gameResources = GameResources(
            resourcesVfs["gameSrc/tablero.png"].readBitmap(),
            resourcesVfs["gameSrc/ruleta.png"].readBitmap(),
            resourcesVfs["gameSrc/apuestaTablero.png"].readBitmap(),
            resourcesVfs["gameSrc/playButton.png"].readBitmap(),
            resourcesVfs["gameSrc/retryButton.png"].readBitmap(),
            resourcesVfs["gameSrc/bola.png"].readBitmap(),
            (0 until 8).map { resourcesVfs["chips/ficha${it+1}.png"].readBitmap() }
        )

        coordenadas = (0 until GameModel.NCASILLAS-1).map {
            val row = it % 3
            val col = it / 3
            val xOffset = 98 * col + 905
            val yOffset = 86 * row + when {
                (it - 2) % 3 == 0 -> 90
                (it - 1) % 3 == 0 -> 300
                else -> 500
            } - 100
            Casilla(it + 1, Point(xOffset, yOffset))
        }.toMutableList()

        coordenadas.add(0, Casilla(0, Point(coordenadas[1].pos.x - 80, coordenadas[1].pos.y))) // 0

        coordenadas.add(37, Casilla(37, Point(coordenadas[34].pos.x + 100, coordenadas[1].pos.y))) // 2to1bot

        coordenadas.add(38, Casilla(38, Point(coordenadas[35].pos.x + 100, coordenadas[2].pos.y))) // 2to1mid

        coordenadas.add(39, Casilla(39, Point(coordenadas[36].pos.x + 100, coordenadas[3].pos.y))) // 2to1top

        coordenadas.add(40, Casilla(40, Point(coordenadas[28].pos.x + 50, coordenadas[1].pos.y + 125))) // 3rd 12

        coordenadas.add(41, Casilla(41, Point(coordenadas[16].pos.x + 50, coordenadas[1].pos.y + 125))) // 2rd 12

        coordenadas.add(42, Casilla(42, Point(coordenadas[4].pos.x + 50, coordenadas[1].pos.y + 125)))// 1rd 12

        coordenadas.add(43, Casilla(43, Point(coordenadas[42].pos.x - 100, coordenadas[1].pos.y + 200))) // 1-18

        coordenadas.add(44, Casilla(44, Point(coordenadas[41].pos.x - 100, coordenadas[1].pos.y + 200))) // Rojos

        coordenadas.add(45, Casilla(45, Point(coordenadas[40].pos.x - 100, coordenadas[1].pos.y + 200))) // Impar

        coordenadas.add(46, Casilla(46, Point(coordenadas[43].pos.x + 190, coordenadas[43].pos.y))) // Pares

        coordenadas.add(47, Casilla(47, Point(coordenadas[44].pos.x + 190, coordenadas[44].pos.y))) // Negros

        coordenadas.add(48, Casilla(48, Point(coordenadas[45].pos.x + 190, coordenadas[45].pos.y))) // 19-36

        container {
            elementsUser = listOf(
                image(userResources.containerProfileImage){scale=1.1},
                image(userResources.profileImage){scale=1.2},
                text(actualUsername, 50),
                text("$actualMoney$", 50)
            )
            for ((index, label) in elementsUser.withIndex()) {
                this.addChild(
                    when (index) {
                        0 -> {label.xy(50, 60)}
                        1 -> {label}
                        2 -> {label.xy(210, 80)}
                        3 -> {label.xy(210, 135)}
                        else -> {label}
                    }
                )
            }

        }.name("Label User").position(20,20)

        val resultCont = container {}.position(width/1.57,50).scale(1.4).name("Resultados Obtenidos")

        container {
            textResultado = text("").xy(resultCont.x-400, resultCont.y+15).scale(3.0)
        }.name("Cantidad ganada")

        container {
            viewRuleta = image(gameResources.ruletaImage) {
                rotation = ((+0).degrees)
                anchor(.5, .5)
                position(views.virtualLeft + 425, 550)
            }
            viewBola = image(gameResources.bolaRuletaImage) {
                rotation = ((+0).degrees)
                anchor(.5, .5)
                position(425, 550)
            }
        }.name("Ruleta").position(60,height/15)

        val contenedorTablero = container {
            image(gameResources.tableroImage) {
                scale(0.57)
                position(780, 145)
            }
        }.name("Tablero")

        fichasApostadas = container {
            for (n in 0 until 8) {
                val xCoordinate = 130 * (n % 4)
                val yCoordinate = 875 - (130 * (n / 4))
                val image = image(gameResources.fichaImages[n]).scale(0.35).position(1590.0 + xCoordinate, yCoordinate)
                makeDraggable(n, image, contenedorTablero)
            }
        }.name("Fichas")

        container {
            val cont = image(gameResources.contenedorApuestas) {
                position(230,52)
                size(550,250)
            }
            text("Apuesta actual",40).position(cont.x * 1.65,cont.y)

            numFichasApostadas = container {
                for (n in 0 until 8) {
                    val text = text("0",34).name(n.toString()).xy((n*30), 100)
                    text.x += n * 23.2
                    listaApuestasCantidad.add(0)
                }
            }
            numFichasApostadas.x = cont.x * 1.34

        }.name("Numero apuestas Decoracion").xy(750,height/1.55)

        fun actualizarHistorial(numWinner: Int) {
            if (listaResultadosNumeros.size > 7) {
                val lastNumber = listaResultadosNumeros.removeAt(listaResultadosNumeros.size - 1)
                listaResultadosNumeros.add(lastNumber)
                listaResultadosNumeros.removeAt(0)
            }
            listaResultadosNumeros.add(numWinner)
            resultCont.removeChildren()
            listaResultadosNumeros.forEachIndexed { index, i ->
                val isRed: Boolean = when (i) {
                    1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36 -> true
                    else -> false
                }
                resultCont.addChild(
                    container {
                        val boxNum = solidRect(50,50, if (isRed) Colors.RED else Colors.WHITE)
                        val textNum = text(
                            i.toString(),
                            35,
                            if (isRed) Colors.WHITE else Colors.BLACK
                        )
                        textNum.centerOn(
                            boxNum
                        )
                        textNum.x = if (i >= 20.0) 7.0 else 11.5
                        textNum.y = 7.0
                    }.position((index * 120)/2, 0)
                )
            }

        }

        fun genCoordinateByWin(winNumber: Int, lastPositionRuleta: Int): Int {
            val ruletaArrayOrdenada = intArrayOf(0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26)
            val position = ruletaArrayOrdenada.indexOf(winNumber)

            var rotationDegrees = (position * 10) - 2

            if (position >= ruletaArrayOrdenada.size / 2) {
                rotationDegrees -= 5
            }

            rotationDegrees = ((rotationDegrees + lastPositionRuleta + 360) % 360 - 360)
            return rotationDegrees
        }

        fun calculateTotalLoopTime(maxLoop: Int, speedFactor: Double): Double {
            val dynamicTime = 0.5 * (maxLoop + 1) + 3.5
            val totalSpeed = dynamicTime * maxLoop * speedFactor
            return when {
                totalSpeed in 0.0..0.825 -> totalSpeed + 2.00001000
                else -> totalSpeed
            }
        }

        //Calculamos la velocidad
        fun calculateSpeedFactor(lastPosition: Int): Double {
            return 1.0 / (lastPosition + 1)
        }

        //Creamos la animacion
        suspend fun animationSpinRoulette(viewImg: Image, viewBola: Image, maxLoop: Int, lastPosition: Int, numWinner:Int) {
            val speedFactor = calculateSpeedFactor(lastPosition)
            val totalLoopTime = calculateTotalLoopTime(maxLoop, speedFactor)
            coroutineScope {
                val imgJob = async {
                    repeat(maxLoop) {
                        val dynamicTime = 0.5 * (it / 2) + 0.5
                        viewImg.rotation = 0.degrees
                        tween(viewImg::rotation[360.degrees].denormalized(), time = dynamicTime.seconds, easing = Easing.LINEAR)
                    }
                    viewImg.rotation = 1.degrees
                    viewImg.tween(time = totalLoopTime.seconds, easing = Easing.EASE_OUT) {
                        viewImg.rotation = (it * lastPosition).degrees
                    }
                }
                val bolaJob = async {
                    repeat(maxLoop + 1) {
                        val dynamicTime = 0.5 * (it / 2) + 1.0
                        viewBola.rotation = (-0).degrees
                        tween(viewBola::rotation[(-360).degrees].denormalized(), time = dynamicTime.seconds, easing = Easing.LINEAR)
                    }
                    viewBola.tween(time = (totalLoopTime+1).seconds, easing = Easing.EASE_OUT) {
                        viewBola.rotation = (it * genCoordinateByWin(numWinner, lastPosition)).degrees
                    }
                }
                imgJob.await()
                bolaJob.await()
            }
        }

        //Creamos el spinrulette
        suspend fun spinRoulette(winNumber: Int){
            val range1 = 80..130
            val range2 = 200..300
            val randomPositionRoulette = when ((0..1).random()) {
                0 -> range1.random()
                else -> range2.random()
            }
            animationSpinRoulette(
                viewImg = viewRuleta,
                viewBola = viewBola,
                maxLoop = 3,
                lastPosition = randomPositionRoulette,
                numWinner = winNumber
            )
        }

        val botonPlay = container {
                val imgPlay = image(gameResources.botonPlay).xy(10,120)
                if (!ruletaActiva){
                    imgPlay.onClick {
                        val sound = resourcesVfs["songs/roulletteSpin.mp3"].readSound()
                        val channel = sound.play()
                        channel.sound
                        channel.volume = 0.1
                        ruletaActiva = true
                        for (i in fichaViewLista) {
                            fichasApostadas.removeChild(i.viewFicha)
                            i.viewFicha = i.viewFicha.clone()
                            fichasApostadas.addChild(i.viewFicha )
                        }
                        val result = game.girarRuleta(bets.getBets())
                        spinRoulette(result.first)
                        delay(500.milliseconds)
                        updateScores(result.second)
                        sceneSwitcher.controller.modifyMoneyUser(actualMoney)
                        actualizarFichasApostadas(index, true, isOnGame = true)
                        for (n in 0 until GameModel.NCASILLAS) {
                            bets.retirarApuestas(n)
                        }
                        for (i in fichaViewLista) {
                            i.eliminarConjuntoFichas(this)
                            fichasApostadas.removeChild(i.viewFicha)
                        }
                        actualizarHistorial(result.first) // Num Winner
                        fichasApostadas.forEachChild {
                            if (it.index > 7) {
                                println(it.index)
                                println(it.name)
                                fichasApostadas.removeChild(it)

                            }
                        }
                        ruletaActiva = false
                    }
                }

        }.name("Boton Juego").position(0, height/1.35)

        uiButton {
            text="MENU"
            onClick {
                sceneSwitcher.goMenu()
            }
            scale = 2.0
        }.position(750, height/1.10)



    }

    private suspend fun updateScores(wonAmount: Int) {
        actualMoney = game.money
        elementsUser[3].setText("$actualMoney$")
        textResultado.text = "Cantidad ganada: $wonAmount"
        var sound = resourcesVfs["songs/winMoney.mp3"].readSound()
        if (wonAmount == 0) {
            sound = resourcesVfs["songs/noWinMoney.mp3"].readSound()
        }
        val channel = sound.play()
        channel.sound
        channel.volume = 0.1
        delay(500.milliseconds)
        for (fichaImage in imageFichaApuestas){
            fichasApostadas.removeChild(fichaImage)
        }
    }

    private fun makeDraggable(fichaN: Int, image: View, tablero: Container) {
        image.mouse.down {
            if (!ruletaActiva) {
                val fichaClon = image.clone().name("clon")
                val fichaView = FichaView(fichaClon)
                fichaView.coordenadas = coordenadas
                image.parent?.addChild(fichaView.viewFicha)
                fichaViewLista.add(fichaView)
                var casillaApostada: Int? = null
                fichaView.viewFicha.draggableCloseable { info ->
                    val (distanciaCercana, casillaCercana) = coordenadas
                        .map { it.pos.distanceTo(info.viewNextXY) to it }.minBy { it.first }

                    if (info.end){
                        val differenceX = (info.viewNextXY.x - tablero[0].x).toInt()
                        val differenceY = (info.viewNextXY.y - tablero[0].y).toInt()
                        if (differenceX in 0..1350 && differenceY in 0..500 && distanciaCercana < 50) {
                            if (casillaApostada == null) {
                                casillaApostada = casillaCercana.num
                                apostarFichas(fichaView, bets, casillaCercana, fichaN, image)
                                actualizarFichasApostadas(fichaN, false, isOnGame = false)
                            } else {
                                if (casillaApostada != casillaCercana.num) {
                                    desapostarFichas(fichaView, bets, casillaApostada!!, fichaN, image)
                                    casillaApostada = casillaCercana.num
                                    apostarFichas(fichaView, bets, casillaCercana, fichaN, image)
                                }
                            }

                        } else {
                            if (casillaApostada != null){
                                val casillaNueva = if (casillaApostada == null) casillaCercana.num else casillaApostada
                                desapostarFichas(fichaView, bets, casillaNueva!!, fichaN, image)
                                actualizarFichasApostadas(fichaN, true, isOnGame = false)
                            }
                            fichaClon.simpleAnimator.sequence {
                                tween(fichaClon::pos[image.pos])
                                hide(fichaClon, time = 0.1.seconds)
                                removeFromParent(fichaClon)
                                block { fichaView.viewFicha }
                            }
                            fichaViewLista.remove(fichaView)
                        }
                    }
                    elementsUser[3].setText("${actualMoney}$")
                }
            }

        }
    }

    private fun desapostarFichas(
        fichaView: FichaView,
        bets: MutableBets,
        casillaCercana: Int,
        fichaN: Int,
        image: View
    ) {
        if (casillaCercana > 36) {
            val numbersToIterate = when (val rango: Any = obtenerRango(casillaCercana)) {
                "ROJO" -> {
                    listOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
                }
                "NEGRO" -> {
                    listOf(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)
                }
                is Iterable<*> -> {
                    rango.mapNotNull { it.toString().toIntOrNull() }
                }
                else -> {
                    emptyList()
                }
            }
            for (i in numbersToIterate) {
                println(casillaCercana)
                bets.desapostar(i, Chip(fichaN))
                actualMoney += Chip(fichaN).price
                println("casillas clon:$actualMoney")
            }
            fichaView.eliminarConjuntoFichas(image.parent!!)
        } else {
            println(casillaCercana)
            bets.desapostar(casillaCercana, Chip(fichaN))
            actualMoney += Chip(fichaN).price
            println("casillas normales:$actualMoney")
        }
    }

    private fun apostarFichas(
            fichaView: FichaView,
            bets: MutableBets,
            casillaCercana: Casilla,
            fichaN: Int,
            image: View
    ) {
        fichaView.viewFicha.simpleAnimator.tween(fichaView.viewFicha::pos[casillaCercana.pos])

        if (casillaCercana.num > 36) {
            val numbersToIterate = when (val rango: Any = obtenerRango(casillaCercana.num)) {
                "ROJO" -> {
                    listOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
                }
                "NEGRO" -> {
                    listOf(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)
                }
                is Iterable<*> -> {
                    rango.mapNotNull { it.toString().toIntOrNull() }
                }
                else -> {
                    emptyList()
                }
            }
            for (i in numbersToIterate){
                bets.apostar(i, Chip(fichaN))
                actualMoney -= Chip(fichaN).price
            }
            fichaView.aniadirConjuntoFichas(image.parent!!, obtenerRango(casillaCercana.num))
        } else {
            bets.apostar(casillaCercana.num, Chip(fichaN))
            actualMoney -= Chip(fichaN).price
        }
    }

    private fun obtenerRango(casillaNum: Int): Any {
        return when (casillaNum) {
            37 -> (1..34) step 3
            38 -> (2..35) step 3
            39 -> (3..36) step 3
            40 -> (25..36)
            41 -> (13..24)
            42 -> (1..12)
            43 -> (1..18)
            44 -> "ROJO"
            45 -> (1..35 step 2)
            46 -> (2..36 step 2)
            47 -> "NEGRO"
            48 -> (19..36)
            else -> (0 until 1)
        }
    }

    private fun actualizarFichasApostadas(fichaN: Int, remove: Boolean, isOnGame: Boolean) {
        if (!isOnGame){
            listaApuestasCantidad[fichaN] += if (remove) -1 else 1
        }
        val cantidadActualApostada = listaApuestasCantidad[fichaN]
        val fichasApostadasView = numFichasApostadas["$fichaN"].bview
        fichasApostadasView.setText(cantidadActualApostada.toString())

        if (cantidadActualApostada >= 6){
            return
        }
        val xPosition = (fichaN * 54) - 25
        val ySpacing = 15
        val yPosition = if (cantidadActualApostada == 1) ySpacing else cantidadActualApostada * ySpacing
        val containerImg = Container().name("i${cantidadActualApostada}$fichaN").apply {
            y += 120
        }
        containerImg.addChild(Image(gameResources.fichaImages[fichaN]).scale(0.15).xy(xPosition, yPosition))
        if (isOnGame){

            for (n in 0 until 8){
                listaApuestasCantidad[n] = 0
                numFichasApostadas["$n"].bview.setText(listaApuestasCantidad[n].toString())
            }
            for (cnt in fichasApostadasContenedorTablero) {
                numFichasApostadas.removeChild(cnt)
            }
            return
        }
        if (remove) {
            numFichasApostadas.removeChild(numFichasApostadas["i${cantidadActualApostada+1}$fichaN"].bview)
            return
        }
        fichasApostadasContenedorTablero.add(containerImg)
        numFichasApostadas.addChild(containerImg)
    }

}

class Casilla(
    val num: Int,
    val pos: Point
)

class UserResources(
    val containerProfileImage:Bitmap,
    val profileImage:Bitmap,
)

class GameResources(
    val tableroImage:Bitmap,
    val ruletaImage:Bitmap,
    val contenedorApuestas:Bitmap,
    val botonPlay:Bitmap,
    val botonRetry:Bitmap,
    val bolaRuletaImage:Bitmap,
    val fichaImages: List<Bitmap>
)

class FichaView(
    var viewFicha: View,

    var coordenadas: MutableList<Casilla> = mutableListOf(),
    var isConjuntoCreated: Boolean = false,
    val conjuntoFichasClon: MutableList<View> = mutableListOf()
) {
    fun aniadirConjuntoFichas(container: Container, range: Any) {
        if (!isConjuntoCreated) {
            val numbersToIterate = when (range) {
                "ROJO" -> {
                    listOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
                }
                "NEGRO" -> {
                    listOf(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35)
                }
                is Iterable<*> -> {
                    range.mapNotNull { it.toString().toIntOrNull() }
                }

                else -> {
                    emptyList()
                }
            }
            for (i in numbersToIterate) {
                conjuntoFichasClon.add(viewFicha.clone().position(coordenadas[i].pos.x, coordenadas[i].pos.y))
            }
            conjuntoFichasClon.forEach {
                container.addChild(it)
            }
            isConjuntoCreated = true
        }
    }

    fun eliminarConjuntoFichas(container: Container) {
        conjuntoFichasClon.forEach {
            viewFicha.parent?.removeChild(it)
            container.removeChild(it)
        }
        conjuntoFichasClon.clear()
        isConjuntoCreated = false
    }
}
