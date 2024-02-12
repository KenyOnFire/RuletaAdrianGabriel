////import korlibs.image.bitmap.*
////import korlibs.image.color.*
////import korlibs.image.format.*
////import korlibs.io.async.async
////import korlibs.io.file.std.*
////import korlibs.korge.animate.*
////import korlibs.korge.input.*
////import korlibs.korge.internal.*
////import korlibs.korge.scene.*
////import korlibs.korge.tween.*
////import korlibs.korge.ui.*
////import korlibs.korge.view.*
////import korlibs.korge.view.align.*
////import korlibs.math.geom.*
////import korlibs.math.interpolation.*
////import kotlinx.coroutines.*
////import kotlin.time.Duration.Companion.milliseconds
////import kotlin.time.Duration.Companion.seconds
////
////class Game(private var sceneSwitcher: SceneSwitcher) : Scene() {
////    private var ruletaActiva = false
////
////    private val game = GameModel()
////    private val bets = MutableBets(game.money)
////
////    private lateinit var numFichasApostadas: Container
////    private val listaApuestasCantidad = arrayListOf<Int>()
////    private val imageFichaApuestas = arrayListOf<Image>()
////    private val fichasApostadasObject = arrayListOf<Chip>()
////    private lateinit var coordenadas:MutableList<Casilla>
////    private val listaResultadosNumeros = arrayListOf<Int>()
////
////    private lateinit var viewRuleta: Image
////    private lateinit var viewBola: Image
////
////    private lateinit var userResources: UserResources
////    private lateinit var gameResources: GameResources
////    @OptIn(KorgeInternal::class)
////    override suspend fun SContainer.sceneMain() {
////        userResources = UserResources(
////            resourcesVfs["user/imageUser.jpg"].readBitmap(),
////            resourcesVfs["user/frameUser.png"].readBitmap()
////        )
////        gameResources = GameResources(
////            resourcesVfs["gameSrc/tablero.png"].readBitmap(),
////            resourcesVfs["gameSrc/ruleta.png"].readBitmap(),
////            resourcesVfs["gameSrc/bola.png"].readBitmap(),
////            resourcesVfs["gameSrc/apuestaTablero.png"].readBitmap(),
////            resourcesVfs["gameSrc/playButton.png"].readBitmap(),
////            resourcesVfs["gameSrc/retryButton.png"].readBitmap(),
////            (0 until 8).map { resourcesVfs["chips/ficha${it+1}.png"].readBitmap() }
////        )
////
////        coordenadas = (0 until 36).map {
////            val row = it % 3
////            val col = it / 3
////            val xOffset = 98 * col + 905 // Le damos margen horizontal a las posiciones
////            val yOffset = 86 * row + when {
////                (it - 2) % 3 == 0 -> 90
////                (it - 1) % 3 == 0 -> 300
////                else -> 500
////            } - 100 // Le damos margen vertical y lo posicionamos 100px hacia arriba
////            //solidRect(50,50, Colors.RED).position(xOffset, yOffset) // Con esto vemos la cordenadda donde caera la ficha (DEBUG)
////            Casilla(it + 1, Point(xOffset, yOffset))
////        }.toMutableList()
////        coordenadas.add(0, Casilla(0, Point(coordenadas[1].pos.x - 80, coordenadas[1].pos.y)))
////
////        container {
////            val elementsUser = listOf(
////                image(userResources.containerProfileImage){scale=1.1},
////                image(userResources.profileImage){scale=1.2},
////                text("AdrianQR", 50),
////                text("10000$", 50)
////            )
////
////            for ((index, label) in elementsUser.withIndex()) {
////                this.addChild(
////                    when (index) {
////                        0 -> {label.xy(50, 60)}
////                        1 -> {label}
////                        2 -> {label.xy(210, 80)}
////                        3 -> {label.xy(210, 135)}
////                        else -> {label}
////                    }
////                )
////            }
////
////        }.name("Label User").position(20,40)
////
////        container {
////            viewRuleta = image(gameResources.ruletaImage) {
////                rotation = ((+0).degrees)
////                anchor(.5, .5)
////                position(views.virtualLeft + 425, 550)
////            }
////            viewBola = image(gameResources.bola) {
////                rotation = ((+0).degrees)
////                anchor(.5, .5)
////                position(425, 550)
////            }
////        }.name("Ruleta").position(100,height/4.5)
////
////
////        container {
////            image(gameResources.tableroImage) {
////                scale(0.57)
////                position(780, 145)
//////                alpha(0.5)
////            }
////        }.name("Tablero")
////
////        container {
////            for (n in 0 until 8) {
////                val xCoordinate = 130 * (n % 4)
////                val yCoordinate = 875 - (130 * (n / 4))
////                val image = image(gameResources.fichaImages[n]).scale(0.35).position(1590.0 + xCoordinate, yCoordinate)
////                makeDraggable(n, image)
////            }
////        }.name("Fichas")
////
////        container {
////            val cont = image(gameResources.contenedorApuestas) {
////                position(230,52)
////                size(550,250)
////            }
////            text("Apuesta actual",40).position(cont.x * 1.65,cont.y)
////
////            numFichasApostadas = container {
////                for (n in 0 until 8) {
////                    xy((n*10), 100)
////                    val text = text("0",34).name(n.toString())
////                    text.x += n * 23.2
////                    listaApuestasCantidad.add(0)
//////                    textApuestas += text
////                }
////            }
////            numFichasApostadas.x = cont.x * 1.4
////
////        }.name("Numero apuestas Decoracion").xy(750,height/1.55)
////
////        fun genCoordinateByWin(winNumber: Int, lastPositionRuleta: Int): Int {
////            var ruletaArrayOrdenada = intArrayOf(0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26)
////            val position = ruletaArrayOrdenada.indexOf(winNumber)
////
////            var rotationDegrees = (position * 10) - 2
////
////            if (position >= ruletaArrayOrdenada.size / 2) {
////                rotationDegrees -= 5
////            }
////
////            rotationDegrees = ((rotationDegrees + lastPositionRuleta + 360) % 360 - 360)
////            return rotationDegrees
////        }
////
////        fun calculateTotalLoopTime(maxLoop: Int, speedFactor: Double): Double {
////            val dynamicTime = 0.5 * (maxLoop + 1) + 3.5
////            val totalSpeed = dynamicTime * maxLoop * speedFactor
////            return when {
////                totalSpeed in 0.0..0.825 -> totalSpeed + 2.00001000
////                else -> totalSpeed
////            }
////        }
////        //Calculamos la velocidad
////        fun calculateSpeedFactor(lastPosition: Int): Double {
////            return 1.0 / (lastPosition + 1)
////        }
////        //Creamos la animacion
////        suspend fun animationSpinRoulette(viewImg: Image, viewBola: Image, maxLoop: Int, lastPosition: Int, numWinner:Int) {
////            val speedFactor = calculateSpeedFactor(lastPosition)
////            val totalLoopTime = calculateTotalLoopTime(maxLoop, speedFactor)
////            coroutineScope {
////                val imgJob = async {
////                    repeat(maxLoop) {
////                        val dynamicTime = 0.5 * (it / 2) + 0.5
////                        viewImg.rotation = 0.degrees
////                        tween(viewImg::rotation[360.degrees].denormalized(), time = dynamicTime.seconds, easing = Easing.LINEAR)
////                    }
////                    viewImg.rotation = 1.degrees
////                    viewImg.tween(time = totalLoopTime.seconds, easing = Easing.EASE_OUT) {
////                        viewImg.rotation = (it * lastPosition).degrees
////                    }
////                }
////                val bolaJob = async {
////                    repeat(maxLoop + 1) {
////                        val dynamicTime = 0.5 * (it / 2) + 1.0
////                        viewBola.rotation = (-0).degrees
////                        tween(viewBola::rotation[(-360).degrees].denormalized(), time = dynamicTime.seconds, easing = Easing.LINEAR)
////                    }
////                    viewBola.tween(time = (totalLoopTime+1).seconds, easing = Easing.EASE_OUT) {
////                        viewBola.rotation = (it * genCoordinateByWin(numWinner, lastPosition)).degrees
////                    }
////                }
////                imgJob.await()
////                bolaJob.await()
////            }
////        }
////        //Creamos el spinrulette
////        suspend fun spinRoulette(winNumber: Int){
////            val range1 = 80..130
////            val range2 = 200..300
////            val randomPositionRoulette = when ((0..1).random()) {
////                0 -> range1.random()
////                else -> range2.random()
////            }
////            animationSpinRoulette(
////                viewImg = viewRuleta,
////                viewBola = viewBola,
////                maxLoop = 3,
////                lastPosition = randomPositionRoulette,
////                numWinner = winNumber
////            )
////        }
////
////
////
////
////
//////        containerResultados.addChild()
////
////        fun actualizarHistorial(){
////            container {
////                listaResultadosNumeros.forEachIndexed { index, i ->
////                    val isRed: Boolean = when (i) {
////                        1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36 -> true
////                        else -> false
////                    }
////                    container {
////                        val boxNum = solidRect(50,50, if (isRed) Colors.RED else Colors.WHITE)
////                        val textNum = text(
////                            i.toString(),
////                            35,
////                            if (isRed) Colors.WHITE else Colors.BLACK
////                        )
////                        textNum.centerOn(
////                            boxNum
////                        )
////                        textNum.x = if (i >= 20.0) 7.0 else 11.5
////                        textNum.y = 7.0
////                    }.position((index * 120)/2, 160)
////                }
////            }.position(1300,0).name("Resultados Obtenidos")
////        }
////        suspend fun jugar() {
////            // Fase 2: La ruleta gira
////            ruletaActiva = true
////
////            val result = 11
////
//////            val animRul =
////
////            spinRoulette(result)
////            delay(500.milliseconds)
//////            animRul.wait()
//////            while (){
//////                delay(100.milliseconds)
//////            }
//////            delay(5.seconds)
////            // giraría la ruleta con ${result.numWinner}
////
//////            views.alert("(DEBUG) El numero ganador es ${result.numWinner}")
////            //delay(2.seconds)
////            println(result)
////            listaResultadosNumeros.add(result)
////            print(listaResultadosNumeros)
////            ruletaActiva = false
////            actualizarHistorial()
////
////            //sceneContainer.changeTo { MyScene(game) }
////        }
////
////        container {
////            image(gameResources.botonPlay).xy(0,125).onClick {
////                jugar()
////            }
////            image(gameResources.botonRetry).scale(0.7).xy(15,0)
////        }.name("Botones Juego").position(0, height/1.35)
////
////        uiButton {
////            text="BACK"
////            onClick {
////                sceneSwitcher.goMenu()
////            }
////            y=300.0
////        }
////        //Generamos la cordenada
////
////        //calculamos el tiempo
////
////        //Creamos la funcion de jugar
////
////    }
////
////
////    private fun makeDraggable(fichaN: Int, image: View) {
////        image.mouse.down {
////            if (!ruletaActiva) {
////                var remove = false
////                val fichaApostada = image.clone()
////                image.parent?.addChild(fichaApostada)
//////                fichasApostadasViewList += fichaApostada
////                var casillaApostada: Int? = null
////                fichaApostada.draggableCloseable { info ->
////                    if (info.end) {
////                        val (distanciaCercana, casillaCercana) = coordenadas
////                            .map { it.pos.distanceTo(info.viewNextXY) to it }.minBy { it.first }
////
////                        if (casillaApostada != null) {
////                            bets.desapostar(casillaApostada!!, Chip(fichaN))
////                            fichasApostadasObject.remove(Chip(fichaN))
////                            actualizarFichasApostadas(fichaN,true)
//////                            actualMoney += Chip(fichaN).price
////                        }
//////                        println(distanciaCercana)
//////                        if (actualMoney - Chip(fichaN).price < 0) {
//////                            fichasApostadasViewList.remove(fichaApostada)
//////                            fichaApostada.removeFromParent()
//////                        } else {
////                        if (distanciaCercana > 50) {
////                            fichaApostada.simpleAnimator.sequence {
////                                tween(fichaApostada::pos[image.pos])
////                                hide(fichaApostada, time = 0.1.seconds)
////                                removeFromParent(fichaApostada)
////                                //                                    block { fichasApostadasViewList.remove(fichaApostada) }
////                            }
////                        } else {
////                            fichaApostada.simpleAnimator.tween(fichaApostada::pos[casillaCercana.pos])
////                            fichasApostadasObject += Chip(fichaN)
////                            bets.apostar(casillaCercana.num, Chip(fichaN))
////                            casillaApostada = casillaCercana.num
////                            //                                actualMoney -= Chip(fichaN).price
////                            actualizarFichasApostadas(fichaN,false)
////                        }
//////                        }
//////                        textMoney.text = "Dinero: ${actualMoney}$"
//////                        updateApuestasText()
////                    }
////                }
////            }
////        }
////    }
////    private fun actualizarFichasApostadas(fichaN: Int, remove: Boolean) {
////        listaApuestasCantidad[fichaN] += if (remove) -1 else 1
////        numFichasApostadas[fichaN][fichaN.toString()].setText(listaApuestasCantidad[fichaN].toString())
//////        val currentCount = textApuestas[fichaN].text.toIntOrNull() ?: 0
//////        println(currentCount)
//////        println(textApuestas[fichaN])
//////        println(textApuestas.toString())
//////        numFichasApostadas.forEachChildWithIndex { index, child ->
//////            if ( fichaN == index ) {
//////                numFichasApostadas[index].removeFromParent()
//////            }
//////        }
//////        for (t in textApuestas){
//////            println(t.text)
//////        }
////    }
////}
////
////class Casilla(
////    val num: Int,
////    val pos: Point
////)
////
////class UserResources(
////    val containerProfileImage:Bitmap,
////    val profileImage:Bitmap,
////)
////
////class GameResources(
////    val tableroImage: Bitmap,
////    val ruletaImage: Bitmap,
////    val bola:Bitmap,
////    val contenedorApuestas: Bitmap,
////    val botonPlay: Bitmap,
////    val botonRetry: Bitmap,
//////    fichaImages1: Bitmap,
////    val fichaImages: List<Bitmap>
////)
//import korlibs.image.bitmap.*
//import korlibs.image.color.*
//import korlibs.image.format.*
//import korlibs.io.file.std.*
//import korlibs.korge.*
//import korlibs.korge.animate.*
//import korlibs.korge.input.*
//import korlibs.korge.scene.*
//import korlibs.korge.tween.*
//import korlibs.korge.ui.*
//import korlibs.korge.view.*
//import korlibs.math.geom.*
//import korlibs.render.*
//import kotlin.time.Duration.Companion.seconds
//
//suspend fun main() = Korge(
//    windowSize = Size(2220, 1080),
//    virtualSize = Size(2220, 1080),
//    backgroundColor = Colors["#6e00b1"],
//    displayMode = KorgeDisplayMode.DEFAULT.copy(scaleAnchor = Anchor.TOP_LEFT, clipBorders = false),
//    ) {
//    val sceneContainer = sceneContainer()
//
//    sceneContainer.changeTo { MyScene(GameModel(money = 50)) }
//}
//
//class MyScene(
//    val game: GameModel
//) : Scene() {
//    lateinit var textMoney: Text
//    lateinit var textResultado: Text
//    val bets = MutableBets(game.money)
//    val textApuestas = arrayListOf<Text>()
//    val fichasApostadasViewList = arrayListOf<View>()
//
//    override suspend fun SContainer.sceneMain() {
//        val NCASILLAS = 5
//        val NFICHAS = 7
//
//        val resources = FichaResources(
//            (0 until NFICHAS).map { resourcesVfs["chips/ficha${it + 1}.png"].readBitmap() },
//        )
//
//        textMoney = text("Dinero ${game.money}").xy(300, 40)
//        textResultado = text("Resultado:").xy(300, 60)
//
//        class Casilla(
//            val num: Int,
//            val pos: Point
//        )
//
//        val coordenadas = (0 until NCASILLAS).map {
//            Casilla(it, Point(50 * it, 100))
//        }
//
//        fun updateApuestasText() {
//            for (n in 0 until GameModel.NCASILLAS) {
//                textApuestas[n].text = "${bets.getTotalPriceEnNum(n)}"
//            }
//        }
//
//        fun resetGame(result: GameResult) {
//            for (n in 0 until GameModel.NCASILLAS) {
//                bets.retirarApuestas(n)
//            }
//            for (ficha in fichasApostadasViewList) {
//                ficha.removeFromParent()
//            }
//            updateApuestasText()
//        }
//
//        fun updateScores(result: GameResult) {
//            textResultado.text = "Cantidad ganada: ${result.wonAmount}"
//            textMoney.text = "Dinero ${game.money}"
//        }
//
//        suspend fun jugar() {
//            // Fase 2: La ruleta gira
//            val result = game.girarRuleta(bets.getBets())
//
//            // giraría la ruleta con ${result.numWinner}
//            updateScores(result)
//
//            views.alert("$result")
//            //delay(2.seconds)
//
//            resetGame(result)
//            //sceneContainer.changeTo { MyScene(game) }
//        }
//        for (n in 0 until NCASILLAS) {
//            solidRect(50, 50, Colors.RED).xy(coordenadas[n].pos)
//        }
//
//            fun makeDraggable(fichaN: Int, image: View) {
//                image.mouse.down {
//                    val fichaApostada = image.clone()
//                    fichaApostada.scale(0.15).xy(50 * fichaN, 0)
//                    image.parent?.addChild(fichaApostada)
//                    fichasApostadasViewList += fichaApostada
//                    var casillaApostada: Int? = null
//
//                    fichaApostada.draggableCloseable { info ->
//                        if (info.end) {
//                            val (distanciaCercana, casillaCercana) = coordenadas
//                                .map { it.pos.distanceTo(info.viewNextXY) to it }.minBy { it.first }
//
//                            if (casillaApostada != null) {
//                                bets.desapostar(casillaApostada!!, Chip(fichaN))
//                            }
//
//                            if (distanciaCercana > 50) {
//                                fichaApostada.simpleAnimator.sequence {
//                                    tween(fichaApostada::pos[image.pos])
//                                    hide(fichaApostada, time = 0.1.seconds)
//                                    removeFromParent(fichaApostada)
//                                    block { fichasApostadasViewList.remove(fichaApostada) }
//                                }
//
//                            } else {
//                                fichaApostada.simpleAnimator.tween(fichaApostada::pos[casillaCercana.pos])
//                                bets.apostar(casillaCercana.num, Chip(fichaN))
//                                casillaApostada = casillaCercana.num
//                            }
////                            updateApuestasText()
//                        }
//                    }
//                }
//            }
//
//            for (n in 0 until NFICHAS) {
//                val image = image(resources.fichaImages[n]).scale(0.15).xy(50 * n, 0)
//                makeDraggable(n, image)
//            }
//
//            // Fase 1: colocamos fichas según lo que podemos apostar
//            for (ncasilla in 0 until NCASILLAS) {
//                container {
//                    xy(0, ncasilla * 50 + 300)
//                    val text = text("${bets.apostadoCasillas[ncasilla].totalPrice}").xy(200, 0)
//                    textApuestas += text
//                    for (n in 0 until NFICHAS) {
//                        image(resources.fichaImages[n]).scale(0.15).xy(50 * n, 0).onClick {
//                            try {
//                                bets.apostar(ncasilla, Chip(n))
//                            } catch (e: Throwable) {
//                                views.alert("${e.message}")
//                            }
////                            updateApuestasText()
//                        }
//                    }
//                    uiButton("reset").xy(100, 0).onClick {
//                        bets.retirarApuestas(ncasilla)
////                        updateApuestasText()
//                    }
//                }
//            }
//
//            uiButton("Play").xy(300, 0).onClick {
//                jugar()
//            }
//        }
//
//    }
//
//
//class FichaResources(
//    val fichaImages: List<Bitmap>, // ficha0.png, ficha1.png...
//    //val ficha2: Bitmap,
//)
