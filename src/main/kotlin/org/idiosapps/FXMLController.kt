package org.idiosapps

import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType


// https://github.com/openjfx/samples/blob/master/IDE/IntelliJ/Non-Modular/Gradle/hellofx/src/main/java/org/openjfx/FXMLController.java
class FXMLController {
    var messagePDFLatexMissing = "Miktex is missing!\n"
    var messageXeLaTeXMissing = "XeLaTeX is missing!\n"

    var messageUbuntuPDFLatex = "Please use \nsudo apt install texlive-latex-base\n to get Miktex"
    var messageUbuntuXeLaTeX = "Please use \nsudo apt install texlive-xetex\n\n to get XeLaTeX"


    @FXML
    fun buildButtonClicked() {
        val operatingSystem = OSUtils.getOS() // TODO make messages clean, possibly with Alert helper
        if (!DependencyChecker.hasPDFLatex())
        {
            val alert = Alert(
                AlertType.WARNING,
                messagePDFLatexMissing + messageUbuntuPDFLatex
//                , ButtonType.YES, ButtonType.NO   // could be handy
            )
            alert.show()
        } else if (!DependencyChecker.hasXeLaTeX()) {
            val alert = Alert(
                AlertType.WARNING,
                messageXeLaTeXMissing + messageUbuntuXeLaTeX)
            alert.show()
        } else {
            ContentPipeline.process()
        }
    }
    fun initialize() {}
}