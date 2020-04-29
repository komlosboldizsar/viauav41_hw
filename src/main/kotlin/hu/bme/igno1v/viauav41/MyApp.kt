package hu.bme.igno1v.viauav41

import hu.bme.igno1v.viauav41.gui.MyView
import hu.bme.igno1v.viauav41.gui.MyStyles

import javafx.application.Application
import tornadofx.App

class MyApp: App(MyView::class, MyStyles::class)

fun main(args: Array<String>) {
    Application.launch(MyApp::class.java, *args)
}