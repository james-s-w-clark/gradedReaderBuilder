package org.idiosapps

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage


// https://github.com/openjfx/samples/blob/master/IDE/IntelliJ/Modular/Java/hellofx/src/org/openjfx/MainApp.java
class MainApp : Application() {
    override fun start(stage: Stage) {

        val fxmlInputStream = this::class.java.classLoader.getResourceAsStream("org.idiosapps/UI.fxml")
        val loader = FXMLLoader()
        val rootLayout = loader.load(fxmlInputStream) as VBox
        val scene = Scene(rootLayout)

        scene.stylesheets.add(this::class.java.classLoader.getResource("org.idiosapps/style.css").toExternalForm())
        stage.title = "gradedReaderBuilder"
        stage.scene = scene
        stage.show()
    }

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            launch(MainApp::class.java)
        }
    }
}