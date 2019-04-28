package org.idiosapps

import javafx.fxml.FXML
import javafx.scene.control.Alert.AlertType.INFORMATION
import org.idiosapps.OSUtils.Companion.PDFTEX
import org.idiosapps.OSUtils.Companion.XETEX
import org.idiosapps.TeXStyling.Companion.SUPERSCRIPT_STYLING
import org.idiosapps.TeXStyling.Companion.UNDERLINE_STYLING
import java.io.PrintWriter

class FXMLController {
    @FXML
    fun buildButtonClicked() {

        try { // first check dependencies & inputs - have a good idea of what to expect
            OSUtils.hasProgram(PDFTEX)
            OSUtils.hasProgram(XETEX)
            Filenames.checkInputs()

            buildGradedReader() // use our pipeline for building our graded reader!
        } catch (exception: Exception) {
            exception.printStackTrace()

            val alert = FXMLHelper.createFittedAlert(exception)
            alert.show()
        }
    }

    fun initialize() {}

    private fun buildGradedReader() {
        var languageUsed = "mandarin"

        OSUtils.tryMakeOutputDir() // Java will only make a new file if the parent folder exists (on Windows anyway)
        val outputStoryTeXWriter = PrintWriter(Filenames.outputTexFilename, "UTF-8")

        val vocab = VocabUtils.splitIntoParts(Filenames.inputVocabFilename)
        val names = VocabUtils.splitIntoParts(Filenames.inputKeyNamesFilename)

        TexUtils.copyToTex(outputStoryTeXWriter, Filenames.inputHeaderFilename)
        TexUtils.copyToTex(outputStoryTeXWriter, Filenames.inputTitleFilename)
        TexUtils.copyToTex(outputStoryTeXWriter, Filenames.inputStoryFilename)

        SummaryPageWriter.writeVocabSection(outputStoryTeXWriter, vocab)
        // todo WriteSummaryPage.writeTexGrammar

        outputStoryTeXWriter.append("\\end{document}")
        outputStoryTeXWriter.close()

        PDFUtils.xelatexToPDF()

        var pagesInfo = PDFUtils.getPdfPageInfo(vocab) // store where each page's last line of text is
        TexUtils.putTexLineNumbers(pagesInfo)

        TeXStyling.addStyling(vocab, SUPERSCRIPT_STYLING)
        TeXStyling.addStyling(names, UNDERLINE_STYLING)

        FooterUtils.addVocabFooters(
            pagesInfo,
            vocab,
            languageUsed
        )

        PDFUtils.xelatexToPDF()

        val succeedAlert = FXMLHelper.createFittedAlert("Graded Reader built!", INFORMATION)
        succeedAlert.show()
    }
}