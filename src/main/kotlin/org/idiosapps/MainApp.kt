package org.idiosapps

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.io.File
import java.io.FileInputStream

// https://github.com/openjfx/samples/blob/master/IDE/IntelliJ/Modular/Java/hellofx/src/org/openjfx/MainApp.java
class MainApp : Application() {
    override fun start(stage: Stage) {

        val loader = FXMLLoader()
        val fxmlFile = File("./src/main/resources/org.idiosapps/UI.fxml")
        val fxmlInputStream = FileInputStream(fxmlFile)
        val rootLayout = loader.load(fxmlInputStream) as VBox
        val scene = Scene(rootLayout)

        stage.title = "gradedReaderBuilder"
        stage.scene = scene
        stage.show()
    }
}