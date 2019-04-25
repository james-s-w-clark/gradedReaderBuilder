package org.idiosapps

import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.Region
import org.idiosapps.OSUtils.Companion.PDFTEX
import org.idiosapps.OSUtils.Companion.XETEX
import org.idiosapps.TeXStyling.Companion.SUPERSCRIPT_STYLING
import org.idiosapps.TeXStyling.Companion.UNDERLINE_STYLING
import java.io.PrintWriter

class FXMLController {
    @FXML
    fun buildButtonClicked() {

        try { // first check dependencies - have a good idea of what to expect
            OSUtils.hasProgram(PDFTEX)
            OSUtils.hasProgram(XETEX)
            buildGradedReader() // our pipeline for building our graded reader!
        } catch (exception: Exception) {
            exception.printStackTrace()

            val alert = Alert(
                AlertType.WARNING,
                ExceptionHelper.exceptionToMessage(exception)
            )
            alert.dialogPane.minWidth = Region.USE_PREF_SIZE
            alert.dialogPane.minHeight = Region.USE_PREF_SIZE
            alert.show()
        }
    }

    fun initialize() {}

    private fun buildGradedReader() {
        var languageUsed = "mandarin"

        val filenames = Filenames() // load defaults from class

        var vocabArray: ArrayList<String> = ArrayList() // This is a list of all the input vocabulary
        var vocabComponentArray: ArrayList<ArrayList<String>> =
            ArrayList() // This an [array of [arrays containing input vocab split into parts]]
        var keyNameArray: ArrayList<String> = ArrayList()
        var keyNameComponentArray: ArrayList<ArrayList<String>> =
            ArrayList() // This an [array of [arrays containing input key names split into parts]]
        var pdfPageLastSentences: ArrayList<String> = ArrayList()
        var texLinesOfPDFPagesLastSentences: ArrayList<Int> = ArrayList()
        var texLineIndexOfPDFPageLastSentence: ArrayList<Int> = ArrayList()


        OSUtils.tryMakeOutputDir() // Java will only make a new file if the parent folder exists (on Windows anyway)
        val outputStoryTeXWriter = PrintWriter(filenames.outputStoryFilename, "UTF-8")

        VocabUtils.splitVocabIntoParts(filenames.inputVocabFilename, vocabArray, vocabComponentArray)
        VocabUtils.splitVocabIntoParts(filenames.inputKeyNamesFilename, keyNameArray, keyNameComponentArray)

        TexUtils.copyToTex(outputStoryTeXWriter, filenames.inputHeaderFilename)
        TexUtils.copyToTex(outputStoryTeXWriter, filenames.inputTitleFilename)
        TexUtils.copyToTex(outputStoryTeXWriter, filenames.inputStoryFilename)

        SummaryPageWriter.writeVocabSection(outputStoryTeXWriter, filenames.inputVocabFilename, vocabComponentArray)
        // todo WriteSummaryPage.writeTexGrammar

        outputStoryTeXWriter.append("\\end{document}")
        outputStoryTeXWriter.close()

        PDFUtils.xelatexToPDF()

        val pdfNumberOfPages = PDFUtils.getNumberOfPDFPages(filenames.outputPDFFilename)
        PDFUtils.readPDF(filenames.outputPDFFilename, vocabComponentArray, pdfPageLastSentences, pdfNumberOfPages)
        TexUtils.getTexLineNumbers(
            filenames.outputStoryFilename,
            pdfPageLastSentences,
            texLinesOfPDFPagesLastSentences,
            texLineIndexOfPDFPageLastSentence
        )

        TeXStyling.addStyling(vocabComponentArray, filenames.outputStoryFilename, SUPERSCRIPT_STYLING)
        TeXStyling.addStyling(keyNameComponentArray, filenames.outputStoryFilename, UNDERLINE_STYLING)

        FooterUtils.addVocabFooters(
            vocabComponentArray,
            filenames.outputStoryFilename,
            texLinesOfPDFPagesLastSentences,
            languageUsed,
            pdfNumberOfPages,
            texLineIndexOfPDFPageLastSentence,
            pdfPageLastSentences
        )
        outputStoryTeXWriter.close()

        PDFUtils.xelatexToPDF()

        val succeedAlert = Alert(
            AlertType.CONFIRMATION,
            "Graded Reader built!"
        )
        succeedAlert.show() // TODO OK -> Open PDF
    }
}