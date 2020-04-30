package hu.bme.igno1v.viauav41.gui

import hu.bme.igno1v.viauav41.model.GameOfLife
import javafx.beans.property.*
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.util.converter.NumberStringConverter
import tornadofx.*

class MyView : View("Conway's Game Of Life EXTRA"), GameOfLife.Observer {

    private val GAME_WIDTH = 30
    private val GAME_HEIGHT = 30

    private val myController: MyController by inject()

    private val game = GameOfLife(GAME_WIDTH, GAME_HEIGHT)

    private val COLOR_BORDER = Color.BLACK
    private val COLOR_LIVING = Color.DARKBLUE
    private val COLOR_DEAD = Color.WHITE
    private val BORDER_SIZE = 2.0
    private val CELL_SIZE = 23.0

    private var cells: Array<Array<Rectangle>> = arrayOf()
    private var ruleMenus: Array<ListMenu> = arrayOf()

    private val gameRunningProperty: BooleanProperty = SimpleBooleanProperty(game.running)
    private val animationIntervalProperty: DoubleProperty = SimpleDoubleProperty(game.animationInterval)

    init {
        animationIntervalProperty.addListener { _, _, newValue ->
            print(newValue)
            game.animationInterval = newValue.toDouble()
        }
    }


    override val root = hbox {
        spacing = 10.0
        alignment = Pos.TOP_LEFT
        group {
            paddingAll = 10.0
            rectangle(0, 0) {
                fill = COLOR_BORDER
                width = GAME_WIDTH * (BORDER_SIZE + CELL_SIZE) + BORDER_SIZE
                height = GAME_HEIGHT * (BORDER_SIZE + CELL_SIZE) + BORDER_SIZE
            }
            for (y in 0 until GAME_HEIGHT) {
                cells += Array(GAME_WIDTH) { x ->
                    rectangle (BORDER_SIZE + x * (BORDER_SIZE + CELL_SIZE), BORDER_SIZE + y * (BORDER_SIZE + CELL_SIZE)) {
                        fill = COLOR_DEAD
                        width = CELL_SIZE
                        height = CELL_SIZE
                    }
                }
            }
            setOnMouseClicked { event ->
                if (event.button != MouseButton.PRIMARY)
                    return@setOnMouseClicked
                if (event.x % (BORDER_SIZE + CELL_SIZE) <= BORDER_SIZE)
                    return@setOnMouseClicked
                if (event.y % (BORDER_SIZE + CELL_SIZE) <= BORDER_SIZE)
                    return@setOnMouseClicked
                val cellX = ((event.x - BORDER_SIZE) / (BORDER_SIZE + CELL_SIZE)).toInt()
                val cellY = ((event.y - BORDER_SIZE) / (BORDER_SIZE + CELL_SIZE)).toInt()
                game.setCell(cellX, cellY, !game.getCell(cellX, cellY))
            }
        }
        vbox {
            spacing = 10.0
            paddingAll = 10.0
            vbox {
                label("Animation")
                hbox {
                    button("Start") {
                        enableWhen(gameRunningProperty.not())
                        action {
                            game.start()
                        }
                    }
                    button("Stop") {
                        enableWhen(gameRunningProperty)
                        action {
                            game.stop()
                        }
                    }
                    label("Speed:")
                    textfield {
                        text(animationIntervalProperty.asString().value)
                        filterInput { it.controlNewText.isFloat() }
                        enableWhen(gameRunningProperty.not())
                        print(animationIntervalProperty.value)
                        textProperty().bindBidirectional(animationIntervalProperty, NumberStringConverter())
                    }
                    label("ms")
                }
                label("Rules")
                vbox {
                    for (i in 0..8) {
                        hbox {
                            label("$i neighbors:")
                            ruleMenus += listmenu {
                                orientation = Orientation.HORIZONTAL
                                GameOfLife.RuleType.values().forEach {
                                    item(it.toString(), null, it)
                                }
                                activeItemProperty.addListener { _, _, newValue ->
                                    if (newValue != null)
                                        game.setRule(i, newValue.tag as GameOfLife.RuleType)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    init {
        game.subscribe(this)
        game.start()
    }

    fun updateCell(x: Int, y: Int, living: Boolean) {
        cells[y][x].fill = if (living) COLOR_LIVING else COLOR_DEAD
    }

    override fun onTableChanged(game: GameOfLife) {
        for (y in 0 until game.height)
            for (x in 0 until game.width)
                updateCell(x, y, game.getCell(x, y))
    }

    override fun onCellChanged(game: GameOfLife, x: Int, y: Int, value: Boolean) {
        updateCell(x, y, value)
    }

    override fun onRuleChanged(game: GameOfLife, index: Int, rule: GameOfLife.RuleType) {
        ruleMenus[index].items.forEach {
            if (it.tag == rule)
                ruleMenus[index].activeItem = it
        }
    }

    override fun onRunningChanged(game: GameOfLife, running: Boolean) {
        gameRunningProperty.set(running)
    }

    override fun onAnimationIntervalChanged(game: GameOfLife, interval: Double) {
        animationIntervalProperty.set(interval)
    }

}
