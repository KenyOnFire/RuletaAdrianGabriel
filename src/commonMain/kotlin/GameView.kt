import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.io.async.*
import korlibs.io.file.std.*
import korlibs.korge.animate.*
import korlibs.korge.annotations.*
import korlibs.korge.input.*
import korlibs.korge.scene.*
import korlibs.korge.tween.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.math.geom.*
import korlibs.math.interpolation.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class GameView(
    val game: GameModel
) : Scene() {
    private lateinit var textMoney: Text
    private lateinit var textResultado: Text
    private val bets = MutableBets(game.money)
    private var actualMoney = game.money
    private var actualUsername = game.userName
    private val textApuestas = arrayListOf<Text>()
    private val imageFichaApuestas = arrayListOf<Image>()
    private val fichasApostadasViewList = arrayListOf<View>()
    private val fichasApostadasClass = arrayListOf<Chip>()
    private val listaResultadosNumeros = arrayListOf<Int>()
    private lateinit var viewRuleta: Image
    private lateinit var viewBola: Image
    private var ruletaActiva: Boolean = false
    private var cordinatePosWin = -0
    @OptIn(KorgeExperimental::class)
    override suspend fun SContainer.sceneMain() {
        val resourceWallpaper = resourcesVfs["fondo.png"].readBitmap()
        // Cargamos un contenedor que contendra el background
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

        // Guardamos el numero de casillas que tendra el tablero
        // Guardamos el numero de fichas que tenemos * teniendo en cuenta que empiezan desde el 1 *
        val NFICHAS = 8

        // Creamos la data class FichaResources con todas las fichas a cargar en la vista
        val resources = FichaResources((0 until NFICHAS).map { resourcesVfs["ficha${it+1}.png"].readBitmap() })
        val resourcesUser = UserResources(resourcesVfs["frameUser.png"].readBitmap(), resourcesVfs["imageUser.jpg"].readBitmap())
//      val resouresGame = GameResources(resourcesVfs["tablero.png"].readBitmap(), resourcesVfs["ruleta.png"].readBitmap())

        textResultado = text("",33).xy(300, 70)

        container {
            if (actualUsername.length > 9) actualUsername = actualUsername.substring(0,9) + "..."
            text(actualUsername,35).position(Point(-50,70))
            image(resourcesUser.profileImage).position(195.0,50.0).scale(0.4)
            image(resourcesUser.containerProfileImage).positionX(150.0)
            textMoney = text("Dinero: ${game.money}$", 35).position(Point(-50,110))
        }.name("Usuario").xy(1800, 45).scale(1.2)

        class Casilla(
            val num: Int,
            val pos: Point
        )

        val coordenadas = (0 until GameModel.NCASILLAS - 1).map {
            val row = it % 3
            val col = it / 3
            val xOffset = 93 * col + 954
            val yOffset = 86 * row + when {
                (it - 2) % 3 == 0 -> 100
                (it - 1) % 3 == 0 -> 300
                else -> 500
            }
//            solidRect(50,50, Colors.RED).position(xOffset, yOffset)
            Casilla(it + 1, Point(xOffset, yOffset))
        }.toMutableList()

        container {
            image(resourcesVfs["tablero.png"].readBitmap()) {
                scale(0.54)
                position(830, 250)
//                alpha(0.5)
            }

            coordenadas.add(0, Casilla(0, Point(coordenadas[1].pos.x - 80, coordenadas[1].pos.y)))
        }.name("Tablero")

        container {
            viewRuleta = image(resourcesVfs["ruleta.png"].readBitmap()) {
                rotation = ((+0).degrees)
                anchor(.5, .5)
                position(views.virtualLeft + 425, 550)
            }
            viewBola =  image(resourcesVfs["bola.png"].readBitmap()) {
                rotation = ((+0).degrees)
                anchor(.5, .5)
                position(425, 550)
//                size(500,100)
//                scale(0.1)
            }
        }.name("Ruleta")

        val fichasApostadasAnimadas = container {
            image(resourcesVfs["apuestaTablero.png"].readBitmap()){
                position(230,52)
                size(550,250)
            }
            val apuestaText = text("Apuesta actual",40).position(0,50)
            apuestaText.simpleAnimator.sequence {
                tween(apuestaText::x[500], time = 500.milliseconds)
                tween(apuestaText::x[330], time = 2.seconds)
            }
        }.name("Numero apuestas Decoracion").xy(750,1000).alpha(0.0)

        val fichasApostadas = container {
            for (n in 0 until NFICHAS) {
                container {
                    xy(n * 30 + 305, 100)
                    val text = text("0",34)
                    text.x += n * 27.2
                    textApuestas += text
                }
            }
        }.name("Numero apuestas").xy(740,1000).alpha(0.0)

        val containerResultados = container {}.name("Resultados Obtenidos")

        fun updateApuestasText() {
            if (fichasApostadasClass.size == 1 || fichasApostadasClass.size < 1) {
                val targetY = when {
                    fichasApostadasClass.size == 1 -> 725.0
                    fichasApostadasClass.size < 1 -> 1100.0
                    else -> return
                }
                val targetAlpha = when {
                    fichasApostadasClass.size == 1 -> 1.0
                    fichasApostadasClass.size < 1 -> 0.0
                    else -> return
                }
                fichasApostadasAnimadas.simpleAnimator.sequence {
                    parallel {
                        tween(fichasApostadasAnimadas::y[targetY], fichasApostadas::y[targetY], time = 1.seconds)
                        alpha(fichasApostadasAnimadas, targetAlpha, time = 1.seconds)
                        alpha(fichasApostadas, targetAlpha, time = 1.seconds)
                    }
                }
            }
            containerResultados.forEachChild {
                it.removeFromParent()
            }

//            //DEBUG
//            (0..6 ).forEach {
//                listaResultadosNumeros.add(it)
//            }

            if (listaResultadosNumeros.size > 7) {
                val lastNumber = listaResultadosNumeros.removeAt(listaResultadosNumeros.size - 1)
                listaResultadosNumeros.add(lastNumber)
                listaResultadosNumeros.removeAt(0)
            }


            // CONTINUAR MARGENES CON ESTA LOGICA...
            containerResultados.container {
                listaResultadosNumeros.forEachIndexed { index, i ->
                    val isRed: Boolean = when (i) {
                        1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36 -> true
                        else -> false
                    }
                    container {
                        solidRect(50,50, if (isRed) Colors.RED else Colors.WHITE)
                        text(
                            i.toString(),
                            35,
                            if (isRed) Colors.WHITE else Colors.BLACK
                        )
                    }.position((index * 130)/2, 180)
//                   .position(x = if(i >= 9) (index * 120)/2 else(index * 124)/2, y = 180) // Antigua logica
                }
            }.position(1300,0)



            for (n in 0 until NFICHAS){
                textApuestas[n].text = "0"
            }
            for (n in 0..imageFichaApuestas.size ){
                fichasApostadas.removeChildAt(NFICHAS)
            }

//            textMoney.text = "Dinero ${actualMoney}"
            fichasApostadasClass.groupBy { it.num }
                .mapValues { it.value.size }
                .toList()
                .sortedBy { it.first }
                .forEach { (num, count) ->
                    if (count != 0){
                        fichasApostadas.addChild(container {
                            textApuestas[num].text = count.toString()
                            if (count <= 6) {
                               (0 until count).forEach { n ->
                                    val xPosition = num * 57 + 280
                                    val yPosition = n * 15 + 130
                                    imageFichaApuestas.add(
                                        image(resources.fichaImages[num]).scale(0.18).xy(xPosition, yPosition)
                                    )
                                }

                            }else{
                                image(resources.fichaImages[num]).scale(0.18).xy(num * 57 + 280, 130)
                            }
                        }.name("FichaApuestaContenedor"))
                    }
                }
        }

        // Creamos la funcion makeDraggable para asignarle el movimiento a la imagen de la ficha
        fun makeDraggable(fichaN: Int, image: View) {
            image.mouse.down {
                val fichaApostada = image.clone()
                image.parent?.addChild(fichaApostada)
                fichasApostadasViewList += fichaApostada
                var casillaApostada: Int? = null

                fichaApostada.draggableCloseable { info ->
                    if (info.end) {
                        val (distanciaCercana, casillaCercana) = coordenadas
                            .map { it.pos.distanceTo(info.viewNextXY) to it }.minBy { it.first }

                        if (casillaApostada != null) {
                            bets.desapostar(casillaApostada!!, Chip(fichaN))
                            fichasApostadasClass.remove(Chip(fichaN))
                            actualMoney += Chip(fichaN).price
                        }
                        if ( actualMoney - Chip(fichaN).price < 0 ){
                            fichaApostada.removeFromParent()
//                            error("No podemos apostar más")
                        } else {
                            if (distanciaCercana > 50) {
                                fichaApostada.simpleAnimator.sequence {
                                    tween(fichaApostada::pos[image.pos])
                                    hide(fichaApostada, time = 0.1.seconds)
                                    removeFromParent(fichaApostada)
                                    block { fichasApostadasViewList.remove(fichaApostada) }
                                }
                            } else {
                                fichaApostada.simpleAnimator.tween(fichaApostada::pos[casillaCercana.pos])
                                fichasApostadasClass += Chip(fichaN)
                                bets.apostar(casillaCercana.num, Chip(fichaN))
                                casillaApostada = casillaCercana.num
                                actualMoney -= Chip(fichaN).price

                            }
                        }

                        textMoney.text = "Dinero: ${actualMoney}$"
                        updateApuestasText()
                    }
                }
            }
        }

        // Cargamos un contenedor que contendra todas las fichas y lo desplazamos a la derecha de la vista
        container {
            for (n in 0 until NFICHAS) {
                val xCoordinate = 120 * (n % 4) // Establecemos la cordenada x basandonos en la columna
                val yCoordinate = 910 - (120 * (n / 4)) // Establecemos la cordenada y basandonos en la fila
                // Cargamos la imagen una a una en la vista del contenedor
                val image = image(resources.fichaImages[n]).scale(0.30).position(1600.0 + xCoordinate, yCoordinate)
                makeDraggable(n, image)
            }
        }.name("Fichas")

        fun updateScores(result: GameResult) {
            textResultado.text = "Cantidad ganada: ${result.wonAmount}"
            actualMoney += result.wonAmount
            textMoney.text = "Dinero: ${actualMoney}$"
        }

        fun resetGame(result: GameResult) {

            for (n in 0 until GameModel.NCASILLAS) {
                bets.retirarApuestas(n)
            }
            for (ficha in fichasApostadasViewList) {
                ficha.removeFromParent()
            }
            for (n in 0 until NFICHAS){
                textApuestas[n].text = "0"
            }
            for (n in 0..imageFichaApuestas.size ){
                fichasApostadas.removeChildAt(NFICHAS)
            }

//            for (i in fichasApostadasClass){
            fichasApostadasClass.removeAll(fichasApostadasClass)
//            }
            updateApuestasText()
//            while (viewBola.speed > 0){
//                delay(250.milliseconds)
//            }
        }

        fun genCordenateByWin(winNumber: Int, lastPositionRuleta:Int):Int{

            val ruletaArrayOrdenada = intArrayOf(0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26)
            val position = ruletaArrayOrdenada.indexOf(winNumber)
            var rotationDegrees = (position * 10) - 2
            if (position >= ruletaArrayOrdenada.size / 2) {
                rotationDegrees -= 5
            }
            rotationDegrees += lastPositionRuleta
            return rotationDegrees
//            ruletaArrayOrdenada += intArrayOf(2, 27) + (30..36).toList().toTypedArray() + intArrayOf(1, 28, 29)
        }

        // ARREGLAR ANIMACION DE ESTO...
        suspend fun animationSpinRoulette(viewImg:Image, grados: IntArray, maxLoop:Int, lastPosition:Int) {
            for (i in 0..maxLoop){
                for (gr in grados) {
                    tween(viewImg::rotation[gr.degrees], time = (i * 0.3).seconds, easing = Easing.LINEAR)
                }
            }
            tween(viewImg::rotation[(lastPosition).degrees], time = (maxLoop * 0.9).seconds, easing = Easing.LINEAR)
        }
        suspend fun spinRoulette(winNumber: Int){
            val range1 = 0..70
            val range2 = 290..359
            val randomPositionRoulette = when ((0..1).random()) {
                0 -> range1.random()
                else -> range2.random()
            }
            animationSpinRoulette(
                viewImg = viewRuleta,
                grados = intArrayOf((+40), (+120), (+220), (+290)),
                maxLoop = 3,
                lastPosition = randomPositionRoulette
            )
        }

        suspend fun jugar() {
            // Fase 2: La ruleta gira
            ruletaActiva = true
            val result = game.girarRuleta(bets.getBets())

//            val animRul =

            spinRoulette(result.numWinner)
            delay(500.milliseconds)
//            animRul.wait()
//            while (){
//                delay(100.milliseconds)
//            }
//            delay(5.seconds)
            // giraría la ruleta con ${result.numWinner}
            updateScores(result)

//            views.alert("(DEBUG) El numero ganador es ${result.numWinner}")
            //delay(2.seconds)
            listaResultadosNumeros.add(result.numWinner)
            resetGame(result)
            ruletaActiva = false
            //sceneContainer.changeTo { MyScene(game) }
        }

        image(resourcesVfs["play.png"].readBitmap()).size(Size(150,150)).xy(30,880).onClick {
            if (!ruletaActiva){
                jugar()
            }
        }

        image(resourcesVfs["reset.png"].readBitmap()).size(Size(92,92)).xy(192, 930).onClick {
            //TODO: CREAR BOTON DE RESTAURAR ULTIMA APUESTA
        }

        //// OOOOLLDDDD METHODS FOR ANIMATIONS

//        fun animationSpinRoulette(viewImg:Image, grados: IntArray, maxLoop:Int, lastPosition:Int): Animator {
//            val myAnimator = animator()
//            for (i in 1..maxLoop){
//                for (gr in grados) {
//                    myAnimator.tween(viewImg::rotation[gr.degrees], time = (i * 0.1).seconds, easing = Easing.LINEAR)
//                }
//            }
//            myAnimator.onComplete{
//                return@onComplete
//            }
//            return myAnimator
////            myAnimator.tween(viewImg::rotation[(lastPosition).degrees], time = (maxLoop * 1.0).seconds, easing = Easing.EASE_OUT_BACK)
//        }
//
//        fun spinRoulette(winNumber: Int): Animator {
//            val range1 = 0..70
//            val range2 = 290..359
//            val randomPositionRoulette = when ((0..1).random()) {
//                0 -> range1.random()
//                else -> range2.random()
//            }
//
//            val anim1 =viewRuleta.simpleAnimator.sequence {
////                for (i in 1..2) {
//                tween(viewRuleta::rotation[(+10).degrees], time = 0.3.seconds, easing = Easing.LINEAR)
//                animationSpinRoulette(
//                    viewImg = viewRuleta,
//                    grados = intArrayOf((+40), (+120), (+220), (+290)),
//                    maxLoop = 3,
//                    lastPosition = randomPositionRoulette
//                )
////                    tween(viewRuleta::rotation[(+40).degrees], time = 0.3.seconds, easing = Easing.LINEAR)
////                    tween(viewRuleta::rotation[(+120).degrees], time = 0.3.seconds, easing = Easing.LINEAR)
////                    tween(viewRuleta::rotation[(+220).degrees], time = 0.3.seconds, easing = Easing.LINEAR)
////                    tween(viewRuleta::rotation[(+290).degrees], time = 0.3.seconds, easing = Easing.LINEAR)
////                }
//
//                tween(viewRuleta::rotation[(randomPositionRoulette).degrees], time = 0.9.seconds, easing = Easing.EASE_OUT)
//            }
//
//
//            val anim = viewBola.simpleAnimator.sequence {
//
////                for (i in 1..2) {
////
////                    tween(viewBola::rotation[(-40).degrees], time = 0.2.seconds, easing = Easing.LINEAR)
////                    tween(viewBola::rotation[(-120).degrees], time = 0.2.seconds, easing = Easing.LINEAR)
////                    tween(viewBola::rotation[(-220).degrees], time = 0.2.seconds, easing = Easing.LINEAR)
////                    tween(viewBola::rotation[(-290).degrees], time = 0.2.seconds, easing = Easing.LINEAR)
////
////                }
//                cordinatePosWin = genCordenateByWin(winNumber, randomPositionRoulette)
//                val rotationBall = animationSpinRoulette(
//                    viewImg = viewBola,
//                    grados = intArrayOf((-40), (-120), (-220), (-290)),
//                    maxLoop = 2,
//                    lastPosition = cordinatePosWin
//                )
////                tween(viewBola::rotation[0.degrees], time = 0.3.seconds, easing = Easing.LINEAR)
//                when (cordinatePosWin) {
//                    in 0..70 -> {
//                        tween(viewBola::rotation[(cordinatePosWin).degrees], time = 0.3.seconds, easing = Easing.LINEAR)
//                    }
//                    in 71..139 -> {
////                        tween(viewBola::rotation[(-100).degrees], time = 0.5.seconds, easing = Easing.LINEAR)
////                        tween(viewBola::rotation[(-200).degrees], time = 0.4.seconds, easing = Easing.LINEAR)
//
//                        tween(viewBola::rotation[(cordinatePosWin).degrees], time = 0.4.seconds, easing = Easing.LINEAR)
//                    }
//                    else -> {
//                        // Your code for other cases
//                        // This block will be executed when cordinatePosWin is outside the range (0..40).
//                        tween(viewBola::rotation[(cordinatePosWin).degrees], time = 0.3.seconds, easing = Easing.LINEAR)
//                    }
//                }
//                anim1.onComplete {
//                    ruletaActiva = false
//                }
////                tween(viewBola::rotation[(-40).degrees], time = 0.3.seconds, easing = Easing.LINEAR)
////                tween(viewBola::rotation[(-120).degrees], time = 0.3.seconds, easing = Easing.LINEAR)
////                tween(viewBola::rotation[(-220).degrees], time = 0.3.seconds, easing = Easing.LINEAR)
////                tween(viewBola::rotation[(-290).degrees], time = 0.3.seconds, easing = Easing.LINEAR)
//
//                // INTENTANDO RESOLVER "SUPUESTA APROXIMACION INEVITABLE" EN ANIMACIONES DE KORGE
//
//                // SI BOLAWIN ES SUPERIOR A 320, DARA LA VUELTA SIN REBOTE, Y LA PROXIMA TIRADA, FUNCIONA COMO SE ESPERA
//                //SI BOLAWIN ES INFERIOR A 320 O SUPERIOR A 250 DEGREES, DARA LA VUELTA SIN REBOTE, Y LA PROXIMA TIRADA, SIGUE INCORRECTA
//                //SI BOLAWIN ES INFERIOR A 250 O SUPERIOR A 139 , HARA EL REBOTE, Y LA PROXIMA TIRADA, SIGUE INCORRECTA
//                //SI BOLAWIN ES IGUAL A 139 O SUPERIOR A 70, HARA EL REBOTE, PERO LA PROXIMA TIRADA, FUNCIONA COMO SE ESPERA
//                //SI BOLAWIN ES IGUAL O INFERIOR A 70, DARA LA VUELTA SIN REBOTE, Y LA PROXIMA TIRADA, FUNCIONA COMO SE ESPERA
//
////                tween(viewBola::rotation[(139).degrees], time = 0.4.seconds, easing = Easing.LINEAR)
////                val targetRotation = if (isBelow180) cordinatePosWin.degrees else negativeCordinatePosWin.degrees
////                tween(viewBola::rotation[targetRotation], time = 0.3.seconds, easing = Easing.LINEAR)
//            }
//            return anim
//        }

        ///// ----------------------------------------------------------------------------------------------- //////

        // Fase 1: colocamos fichas según lo que podemos apostar
//        for (ncasilla in 0 until GameModel.NCASILLAS) {
//            container {
//                xy(0, ncasilla * 50 + 300)
//                val text = text("${bets.apostadoCasillas[ncasilla].totalPrice} - $ncasilla").xy(200, 0)
//                val text1 = text("${Chip.PRICES.indexOf(bets.apostadoCasillas[ncasilla].totalPrice)} - $ncasilla").xy(200, 0)
//                textApuestas += text
//                for (n in 0 until NFICHAS) {
//                    solidRect(500,500,Colors.RED).scale(0.15).xy(100 * n, 100).onClick {
//                        try {
//                            bets.apostar(ncasilla, Chip(n))
//                        } catch (e: Throwable) {
//                            views.alert("${e.message}")
//                        }
//                        updateApuestasText()
//                    }//image(resources.fichaImages[n])
//                }
//                uiButton("reset").xy(100, 0).onClick {
//                    bets.retirarApuestas(ncasilla)
//                    updateApuestasText()
//                }
//            }
//        }
//        uiButton("Play").xy(300, 0).onClick {
////            jugar()
//        }
    }
}
class FichaResources(
    val fichaImages: List<Bitmap>, // ficha0.png, ficha1.png...
)
class UserResources(
    val containerProfileImage:Bitmap,
    val profileImage:Bitmap,
)
class GameResources(
    val tableroImage:Bitmap,
    val ruletaImage:Bitmap
)
