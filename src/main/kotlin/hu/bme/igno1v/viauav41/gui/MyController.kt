package hu.bme.igno1v.viauav41.gui

import hu.bme.igno1v.viauav41.model.GameOfLife
import javafx.beans.property.*
import tornadofx.Controller

class MyController: Controller(), GameOfLife.Observer {

    val myView: MyView by inject()

    val GAME_WIDTH = 30
    val GAME_HEIGHT = 30
    private val game = GameOfLife(GAME_WIDTH, GAME_HEIGHT)

    // region Beans properties
    val gameRunningProperty: BooleanProperty = SimpleBooleanProperty(game.running)
    val animationIntervalProperty: DoubleProperty = SimpleDoubleProperty(game.animationInterval)
    val selectedRuleProperties: Array<Property<GameOfLife.RuleType>> = Array(9) {
        SimpleObjectProperty(game.getRule(it))
    }
    val wallBehaviorProperty: Property<GameOfLife.WallBehavior> = SimpleObjectProperty(game.wallBehavior)

    private fun addPropertyListeners() {
        gameRunningProperty.addListener { _, _, newValue ->
            if (newValue)
                game.start()
            else
                game.stop()
        }
        animationIntervalProperty.addListener { _, _, newValue ->
            print(newValue)
            game.animationInterval = newValue.toDouble()
        }
        selectedRuleProperties.forEachIndexed { index, property ->
            property.addListener { _, _, newValue ->
                game.setRule(index, newValue)
            }
        }
        wallBehaviorProperty.addListener { _, _, newValue ->
            game.wallBehavior = newValue
        }
    }
    // endregion

    init {
        addPropertyListeners()
        game.subscribe(this)
        game.start()
    }

    // region Observer
    override fun onTableChanged(game: GameOfLife) {
        for (y in 0 until game.height)
            for (x in 0 until game.width)
                myView.updateCell(x, y, game.getCell(x, y))
    }

    override fun onCellChanged(game: GameOfLife, x: Int, y: Int, value: Boolean) {
        myView.updateCell(x, y, value)
    }

    override fun onRuleChanged(game: GameOfLife, index: Int, rule: GameOfLife.RuleType) {
        selectedRuleProperties[index].value = rule
    }

    override fun onRunningChanged(game: GameOfLife, running: Boolean) {
        gameRunningProperty.set(running)
    }

    override fun onAnimationIntervalChanged(game: GameOfLife, interval: Double) {
        animationIntervalProperty.set(interval)
    }

    override fun onWallBehaviorChanged(game: GameOfLife, behavior: GameOfLife.WallBehavior) {
        wallBehaviorProperty.value = behavior
    }

    fun invertCell(x: Int, y: Int) {
        game.setCell(x, y, !game.getCell(x, y))
    }
    // endregion

}
