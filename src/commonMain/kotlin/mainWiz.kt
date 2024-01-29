////import korlibs.image.bitmap.*
////import korlibs.image.color.*
////import korlibs.image.format.*
////import korlibs.io.file.std.*
////import korlibs.korge.*
////import korlibs.korge.animate.*
////import korlibs.korge.input.*
////import korlibs.korge.scene.*
////import korlibs.korge.tween.*
////import korlibs.korge.ui.*
////import korlibs.korge.view.*
////import korlibs.math.geom.*
////import korlibs.render.*
////import kotlin.random.*
////import kotlin.time.Duration.Companion.seconds
////
////suspend fun main() = Korge(
////    windowSize = Size(2220, 1080),
////    virtualSize = Size(2220, 1080),
////    backgroundColor = Colors["#6e00b1"],
////    displayMode = KorgeDisplayMode.DEFAULT.copy(scaleAnchor = Anchor.TOP_LEFT, clipBorders = false),
////    ) {
////    val sceneContainer = sceneContainer()
////
////    sceneContainer.changeTo { MyScene(GameModel(money = 50)) }
////}
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
//            (0 until NFICHAS).map { resourcesVfs["ficha${it+1}.png"].readBitmap() },
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
//        for (n in 0 until NCASILLAS) {
////            solidRect(50, 50, Colors.RED).xy(coordenadas[n].pos)
////        }
//
//        fun makeDraggable(fichaN: Int, image: View) {
//            image.mouse.down {
//                val fichaApostada = image.clone()
//                fichaApostada.scale(0.15).xy(50 * fichaN, 0)
//                image.parent?.addChild(fichaApostada)
//                fichasApostadasViewList += fichaApostada
//                var casillaApostada: Int? = null
//
//                fichaApostada.draggableCloseable { info ->
//                    if (info.end) {
//                        val (distanciaCercana, casillaCercana) = coordenadas
//                            .map { it.pos.distanceTo(info.viewNextXY) to it }.minBy { it.first }
//
//                        if (casillaApostada != null) {
//                            bets.desapostar(casillaApostada!!, Chip(fichaN))
//                        }
//
//                        if (distanciaCercana > 50) {
//                            fichaApostada.simpleAnimator.sequence {
//                                tween(fichaApostada::pos[image.pos])
//                                hide(fichaApostada, time = 0.1.seconds)
//                                removeFromParent(fichaApostada)
//                                block { fichasApostadasViewList.remove(fichaApostada) }
//                            }
//
//                        } else {
//                            fichaApostada.simpleAnimator.tween(fichaApostada::pos[casillaCercana.pos])
//                            bets.apostar(casillaCercana.num, Chip(fichaN))
//                            casillaApostada = casillaCercana.num
//                        }
//                        updateApuestasText()
//                    }
//                }
//            }
//        }
//
//        for (n in 0 until NFICHAS) {
//            val image = image(resources.fichaImages[n]).scale(0.15).xy(50 * n, 0)
//            makeDraggable(n, image)
//        }
//
//        // Fase 1: colocamos fichas según lo que podemos apostar
//        for (ncasilla in 0 until NCASILLAS) {
//            container {
//                xy(0, ncasilla * 50 + 300)
//                val text = text("${bets.apostadoCasillas[ncasilla].totalPrice}").xy(200, 0)
//                textApuestas += text
//                for (n in 0 until NFICHAS) {
//                    image(resources.fichaImages[n]).scale(0.15).xy(50 * n, 0).onClick {
//                        try {
//                            bets.apostar(ncasilla, Chip(n))
//                        } catch (e: Throwable) {
//                            views.alert("${e.message}")
//                        }
//                        updateApuestasText()
//                    }
//                }
//                uiButton("reset").xy(100, 0).onClick {
//                    bets.retirarApuestas(ncasilla)
//                    updateApuestasText()
//                }
//            }
//        }
//
//        uiButton("Play").xy(300, 0).onClick {
//            jugar()
//        }
//    }
//
//    fun updateApuestasText() {
//        for (n in 0 until GameModel.NCASILLAS) {
//            textApuestas[n].text = "${bets.getTotalPriceEnNum(n)}"
//        }
//    }
//
//    fun resetGame(result: GameResult) {
//        for (n in 0 until GameModel.NCASILLAS) {
//            bets.retirarApuestas(n)
//        }
//        for (ficha in fichasApostadasViewList) {
//            ficha.removeFromParent()
//        }
//        updateApuestasText()
//    }
//
//    fun updateScores(result: GameResult) {
//        textResultado.text = "Cantidad ganada: ${result.wonAmount}"
//        textMoney.text = "Dinero ${game.money}"
//    }
//
//    suspend fun jugar() {
//        // Fase 2: La ruleta gira
//        val result = game.girarRuleta(bets.getBets())
//
//        // giraría la ruleta con ${result.numWinner}
//        updateScores(result)
//
//        views.alert("$result")
//        //delay(2.seconds)
//
//        resetGame(result)
//        //sceneContainer.changeTo { MyScene(game) }
//    }
//}
//
//
////class FichaResources(
////    val fichaImages: List<Bitmap>, // ficha0.png, ficha1.png...
////    //val ficha2: Bitmap,
////)
