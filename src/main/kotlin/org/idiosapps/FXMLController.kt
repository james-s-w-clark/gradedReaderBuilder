package org.idiosapps

import com.jfoenix.controls.JFXComboBox
import javafx.fxml.FXML
import javafx.scene.control.Alert.AlertType.INFORMATION
import javafx.scene.control.Label
import org.idiosapps.OSUtils.Companion.PDFTEX
import org.idiosapps.OSUtils.Companion.XETEX
import org.idiosapps.TeXStyling.Companion.SUPERSCRIPT_STYLING
import org.idiosapps.TeXStyling.Companion.UNDERLINE_STYLING
import java.io.PrintWriter

class FXMLController {
    @FXML
    var l1Dropdown: JFXComboBox<String>? = null
    @FXML
    var l2Dropdown: JFXComboBox<String>? = null
    @FXML
    lateinit var storyState: Label
    @FXML
    lateinit var vocabState: Label
    @FXML
    lateinit var nameState: Label

    @FXML
    fun buildButtonClicked() {

        try { // first check dependencies & inputs - have a good idea of what to expect
            OSUtils.hasProgram(PDFTEX)
            OSUtils.hasProgram(XETEX)
            Filenames.checkInputs() // just check files exist
            checkInputStory() // check these inputs have contents
            checkInputVocab()
            checkInputNames()

            buildGradedReader() // use our pipeline for building our graded reader!
        } catch (exception: Exception) {
            exception.printStackTrace()

            val alert = FXMLHelper.createFittedAlert(exception)
            alert.show()
        }
    }

    fun initialize() {
        loadLanguageComboBoxes()
    }

    private fun checkInputStory() {
        if (Filenames.hasContent(Filenames.inputStoryFilename)) {
            storyState.text = "OK"
        } else storyState.text = "No contents found"
    }

    private fun checkInputVocab() {
        if (Filenames.hasContent(Filenames.inputVocabFilename)) {
            vocabState.text = "OK"
        } else vocabState.text = "No contents found"
    }

    private fun checkInputNames(){
        if (Filenames.hasContent(Filenames.inputKeyNamesFilename)) {
            nameState.text = "OK"
        } else nameState.text = "No contents found"
    }

    private fun loadLanguageComboBoxes() {
        l1Dropdown?.items = LanguageUtils.supportedL1
        l2Dropdown?.items = LanguageUtils.supportedL2
    }

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