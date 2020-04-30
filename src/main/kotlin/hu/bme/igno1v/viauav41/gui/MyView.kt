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

class MyView : View("Conway's Game Of Life EXTRA") {

    private val myController: MyController by inject()

    private val COLOR_BORDER = Color.BLACK
    private val COLOR_LIVING = Color.DARKBLUE
    private val COLOR_DEAD = Color.WHITE
    private val BORDER_SIZE = 2.0
    private val CELL_SIZE = 23.0

    private var cells: Array<Array<Rectangle>> = arrayOf()

    override val root = hbox {
        spacing = 10.0
        alignment = Pos.TOP_LEFT
        group {
            paddingAll = 10.0
            rectangle(0, 0) {
                fill = COLOR_BORDER
                width = myController.GAME_WIDTH * (BORDER_SIZE + CELL_SIZE) + BORDER_SIZE
                height = myController.GAME_HEIGHT * (BORDER_SIZE + CELL_SIZE) + BORDER_SIZE
            }
            for (y in 0 until myController.GAME_HEIGHT) {
                cells += Array(myController.GAME_WIDTH) { x ->
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
                myController.invertCell(cellX, cellY)
            }
        }
        vbox {
            spacing = 10.0
            paddingAll = 10.0
            vbox {
                label("Animation")
                hbox {
                    button("Start") {
                        enableWhen(myController.gameRunningProperty.not())
                        action {
                            myController.gameRunningProperty.set(true)
                        }
                    }
                    button("Stop") {
                        enableWhen(myController.gameRunningProperty)
                        action {
                            myController.gameRunningProperty.set(false)
                        }
                    }
                    label("Speed:")
                    textfield {
                        text(myController.animationIntervalProperty.asString().value)
                        filterInput { it.controlNewText.isFloat() }
                        enableWhen(myController.gameRunningProperty.not())
                        print(myController.animationIntervalProperty.value)
                        textProperty().bindBidirectional(myController.animationIntervalProperty, NumberStringConverter())
                    }
                    label("ms")
                }
                label("Rules")
                vbox {
                    for (i in 0..8) {
                        hbox {
                            label("$i neighbors:")
                            listmenu {
                                orientation = Orientation.HORIZONTAL
                                GameOfLife.RuleType.values().forEach {
                                    val currentItem = item(it.toString(), null, it)
                                    if (myController.selectedRuleProperties[i].value == it)
                                        activeItem = currentItem
                                }
                                activeItemProperty.addListener { _, _, newValue ->
                                    if (newValue != null)
                                        myController.selectedRuleProperties[i].value = newValue.tag as GameOfLife.RuleType
                                }
                                myController.selectedRuleProperties[i].addListener { _, _, newValue ->
                                    items.forEach {
                                        if (it.tag == newValue)
                                            activeItem = it
                                    }
                                }
                            }
                        }
                    }
                }
                label("Wall behavior")
                listmenu {
                    orientation = Orientation.HORIZONTAL
                    GameOfLife.WallBehavior.values().forEach {
                        val currentItem = item(it.toString(), null, it)
                        if (myController.wallBehaviorProperty.value == it)
                            activeItem = currentItem
                    }
                    activeItemProperty.addListener { _, _, newValue ->
                        if (newValue != null)
                            myController.wallBehaviorProperty.value = newValue.tag as GameOfLife.WallBehavior
                    }
                    myController.wallBehaviorProperty.addListener { _, _, newValue ->
                        items.forEach {
                            if (it.tag == newValue)
                                activeItem = it
                        }
                    }
                }
            }
        }
    }

    fun updateCell(x: Int, y: Int, living: Boolean) {
        cells[y][x].fill = if (living) COLOR_LIVING else COLOR_DEAD
    }

}
