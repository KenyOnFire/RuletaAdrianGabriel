import kotlin.random.*

class GameModel(
    var money: Int = 0,
    var userName: String = ""
) {
    companion object {
        val NCASILLAS = 37

    }

    fun girarRuleta(
        bets: List<Bet>
    ): GameResult {
        val numWinner = Random.nextInt(NCASILLAS)
        val result = GameResult(numWinner, bets)
        money -= bets.sumOf { it.totalPrice }
        money += result.wonAmount
        return result
    }
}

data class Chip(val num: Int) {
    companion object {
        val PRICES = listOf(1, 5, 10, 20, 50, 100, 500, 1000)
    }
    val price: Int get() = PRICES[num]
}

data class Bet(
    /** Número al que apuestas */
    val casillaNum: Int,
    /** Fichas apostadas */
    val chips: List<Chip>,
) {
    val totalPrice: Int get() = chips.sumOf { it.price }
}

class MutableBets(
    val maxMoney: Int
) {
    val apostadoCasillas = Array<Bet>(GameModel.NCASILLAS) { Bet(it, emptyList()) }

    val dineroApostado get() = apostadoCasillas.sumOf { it.totalPrice }

    fun getBets(): List<Bet> {
        return apostadoCasillas.filter { it.chips.isNotEmpty() }
    }

    fun getTotalPriceEnNum(num: Int): Int {
        return apostadoCasillas[num].totalPrice
    }

    fun desapostar(num: Int, chip: Chip) {
        apostadoCasillas[num] = apostadoCasillas[num].copy(chips = apostadoCasillas[num].chips - chip)
    }

    fun apostar(num: Int, chip: Chip) {
        if (dineroApostado + chip.price < maxMoney) {
            apostadoCasillas[num] = apostadoCasillas[num].copy(chips = apostadoCasillas[num].chips + chip)
        }
    }

    fun retirarApuestas(num: Int) {
        apostadoCasillas[num] = apostadoCasillas[num].copy(chips = emptyList())
    }
}

data class GameResult(
    val numWinner: Int,
    val bets: List<Bet>,
) {
    val wonAmount: Int get() = bets.filter { it.casillaNum == numWinner }.sumOf { it.totalPrice }
}
