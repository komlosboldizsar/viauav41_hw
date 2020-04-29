fun main() {
    val table = GameOfLife(5, 5)
    table.setCell(2, 1, true)
    table.setCell(2, 2, true)
    table.setCell(2, 3, true)
    table.subscribe(TableObserver())
    table.start()
    Thread.sleep(10000)
}

class TableObserver : GameOfLife.Observer {

    override fun onTableChanged(game: GameOfLife) {
        println("-----")
        game.table.forEach {
            it.forEach {
                print(if (it) "X" else " ")
            }
            println("")
        }
        println("-----")
        println("")
    }

    override fun onCellChanged(game: GameOfLife, x: Int, y: Int, value: Boolean) {
    }

    override fun onRuleChanged(game: GameOfLife, index: Int, rule: GameOfLife.RuleType) {
    }

    override fun onRunningChanged(game: GameOfLife, running: Boolean) {
    }

}