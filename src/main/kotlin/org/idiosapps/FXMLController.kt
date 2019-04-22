package org.idiosapps

import javafx.fxml.FXML

// https://github.com/openjfx/samples/blob/master/IDE/IntelliJ/Non-Modular/Gradle/hellofx/src/main/java/org/openjfx/FXMLController.java
class FXMLController {

    @FXML
    fun clickedBuildButton() {
        ContentPipeline.process()
    }

    fun initialize() {}
}