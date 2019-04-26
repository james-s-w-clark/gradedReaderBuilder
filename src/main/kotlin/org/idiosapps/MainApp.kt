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

        stage.title = "gradedReaderBuilder"
        stage.scene = scene
        stage.show()
    }
}