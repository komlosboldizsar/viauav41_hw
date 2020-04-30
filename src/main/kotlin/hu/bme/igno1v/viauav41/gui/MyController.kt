package hu.bme.igno1v.viauav41.gui

import hu.bme.igno1v.viauav41.model.GameOfLife
import javafx.beans.property.*
import tornadofx.Controller
import tornadofx.tag

class MyController: Controller(), GameOfLife.Observer {

    val myView: MyView by inject()

    val GAME_WIDTH = 30
    val GAME_HEIGHT = 30
    private val game = GameOfLife(GAME_WIDTH, GAME_HEIGHT)

    val gameRunningProperty: BooleanProperty = SimpleBooleanProperty(game.running)
    val animationIntervalProperty: DoubleProperty = SimpleDoubleProperty(game.animationInterval)
    init {
        animationIntervalProperty.addListener { _, _, newValue ->
            print(newValue)
            game.animationInterval = newValue.toDouble()
        }
    }

    init {
        game.subscribe(this)
        game.start()
    }

    override fun onTableChanged(game: GameOfLife) {
        for (y in 0 until game.height)
            for (x in 0 until game.width)
                myView.updateCell(x, y, game.getCell(x, y))
    }

    override fun onCellChanged(game: GameOfLife, x: Int, y: Int, value: Boolean) {
        myView.updateCell(x, y, value)
    }

    override fun onRuleChanged(game: GameOfLife, index: Int, rule: GameOfLife.RuleType) {
        val menu = myView.ruleMenus[index]
        menu.items.forEach {
            if (it.tag == rule)
                menu.activeItem = it
        }
    }

    override fun onRunningChanged(game: GameOfLife, running: Boolean) {
        gameRunningProperty.set(running)
    }

    override fun onAnimationIntervalChanged(game: GameOfLife, interval: Double) {
        animationIntervalProperty.set(interval)
    }

    fun startGame() {
        game.start()
    }

    fun stopGame() {
        game.stop()
    }

    fun invertCell(x: Int, y: Int) {
        game.setCell(x, y, !game.getCell(x, y))
    }

    fun setRule(index: Int, rule: GameOfLife.RuleType) {
        game.setRule(index, rule)
    }

}
