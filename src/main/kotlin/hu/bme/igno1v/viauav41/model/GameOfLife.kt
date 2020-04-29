package hu.bme.igno1v.viauav41.model

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class GameOfLife(val width: Int, val height: Int) {

    // region "Game" logic
    // Initializer initializes all elements to false
    private var table_: Array<BooleanArray> = arrayOf()
    val table: Array<BooleanArray>
        get() {
            return this.table_
        }

    private var rules: Array<RuleType>

    init {
        for (y in 0 until height)
            this.table_ += BooleanArray(width)
        this.rules = Array(9) { i ->
            when (i) {
                in 0..1 -> RuleType.DIE // underpopulation
                2 -> RuleType.KEEP
                3 -> RuleType.BORN // reproduction
                in 3..8 -> RuleType.DIE // overpopulation
                else -> RuleType.KEEP
            }
        }
    }

    fun step() {
        var nextTable: Array<BooleanArray> = arrayOf()
        for (y in 0 until height) {
            nextTable += BooleanArray(width)
            for (x in 0 until width) {
                var countLivingCells = 0
                for (dy in -1..1)
                    for (dx in -1..1)
                        if (!((dx == 0) && (dy == 0)))
                            countLivingCells += if (getCell(x + dx, y + dy)) 1 else 0
                nextTable[y][x] = when(this.rules[countLivingCells]) {
                    RuleType.BORN -> true
                    RuleType.KEEP -> this.table_[y][x]
                    RuleType.INVERT -> !this.table_[y][x]
                    RuleType.DIE -> false
                }
            }
        }
        this.table_ = nextTable
        notifyObserversTableChanged()
    }

    fun getCell(x: Int, y: Int): Boolean {
        if ((x < 0) || (x >= width))
            return false
        if ((y < 0) || (y >= height))
            return false
        return this.table[y][x]
    }

    fun setCell(x: Int, y: Int, value: Boolean) {
        if ((x < 0) || (x >= width))
            throw IllegalArgumentException()
        if ((y < 0) || (y >= height))
            throw IllegalArgumentException()
        this.table[y][x] = value
        notifyObserversCellChanged(x, y, value)
    }

    enum class RuleType {
        BORN,
        KEEP,
        INVERT,
        DIE
    }

    fun getRule(index: Int): RuleType {
        if ((index < 0) || (index > 8))
            throw IllegalArgumentException()
        return this.rules[index]
    }

    fun setRule(index: Int, rule: RuleType) {
        if ((index < 0) || (index > 8))
            throw IllegalArgumentException()
        if (this.rules[index] == rule)
            return
        this.rules[index] = rule
        notifyObserversRuleChanged(index, rule)
    }
    // endregion

    // region "Animation"
    var running: Boolean by Delegates.observable(false) { _, _, newValue ->
        notifyObserversRunningChanged(newValue)
    }

    var animationInterval: Float by Delegates.vetoable(1000.0f) { _, _, newValue ->
        newValue > 0.0f
    }

    private var animationTime = 0

    fun start() {
        this.running = true
    }

    fun stop() {
        this.running = false
    }

    private val ANIMATION_GRANULARITY: Long = 10

    private suspend fun animationRoutine() {
        if (this.running) {
            animationTime++
            if (animationTime >= (animationInterval / ANIMATION_GRANULARITY)) {
                animationTime = 0
                step()
            }
        }
        delay(ANIMATION_GRANULARITY)
    }

    private fun startAnimationRoutine() {
        GlobalScope.launch {
            while(true)
                animationRoutine()
        }
    }

    init {
        startAnimationRoutine()
    }
    // endregion


    // region Observer
    interface Observer {
        fun onTableChanged(game: GameOfLife)
        fun onCellChanged(game: GameOfLife, x: Int, y: Int, value: Boolean)
        fun onRuleChanged(game: GameOfLife, index: Int, rule: RuleType)
        fun onRunningChanged(game: GameOfLife, running: Boolean)
    }

    val observers: MutableList<GameOfLife.Observer> = mutableListOf()

    fun subscribe(observer: Observer) {
        observers.add(observer)
    }

    fun unsubscribe(observer: Observer) {
        observers.remove(observer)
    }

    fun notifyObserversTableChanged() {
        observers.forEach {
            it.onTableChanged(this)
        }
    }

    fun notifyObserversCellChanged(x: Int, y: Int, value: Boolean) {
        observers.forEach {
            it.onCellChanged(this, x, y, value)
        }
    }

    fun notifyObserversRuleChanged(index: Int, rule: RuleType) {
        observers.forEach {
            it.onRuleChanged(this, index, rule)
        }
    }

    fun notifyObserversRunningChanged(running: Boolean) {
        observers.forEach {
            it.onRunningChanged(this, running)
        }
    }
    // endregion

}