package org.idiosapps

import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.Region
import org.idiosapps.ExceptionHelper.Companion.exceptionPDFLatex
import org.idiosapps.ExceptionHelper.Companion.exceptionXELATEX
import java.io.PrintWriter

class FXMLController {
    @FXML
    fun buildButtonClicked() {
        if (!DependencyChecker.hasPDFLatex()) { // check for some prerequisites
            val alert = Alert(
                AlertType.WARNING,
                ExceptionHelper.getExceptionMessage(exceptionPDFLatex)
            )
            alert.show()
        } else if (!DependencyChecker.hasXeLaTeX()) { // another prerequisite
            val alert = Alert(
                AlertType.WARNING,
                ExceptionHelper.getExceptionMessage(exceptionXELATEX)
            )
            alert.show()
        } else {
            try {
                buildGradedReader() // our pipeline for building our graded reader!
            } catch (exception: Exception) { // e.g. "Error: ctex.sty not found!" gets Thrown as Exception
                val alert = Alert(
                    AlertType.WARNING,
                    ExceptionHelper.exceptionToMessage(exception)
                )
                alert.dialogPane.minWidth = Region.USE_PREF_SIZE // TODO default size in Alert maker?
                alert.dialogPane.minHeight = Region.USE_PREF_SIZE
                alert.show()
            }
        }
    }

    fun initialize() {}

    fun buildGradedReader() {
        var languageUsed = "mandarin"

        val filenames = Filenames() // load defaults from class
        var pdfNumberOfPages = 0

        var vocabArray: ArrayList<String> = ArrayList() // This is a list of all the input vocabulary
        var vocabComponentArray: ArrayList<ArrayList<String>> =
            ArrayList<ArrayList<String>>() // This an [array of [arrays containing input vocab split into parts]]
        var keyNameArray: ArrayList<String> = ArrayList()
        var keyNameComponentArray: ArrayList<ArrayList<String>> =
            ArrayList<ArrayList<String>>() // This an [array of [arrays containing input key names split into parts]]
        var pdfPageLastSentences: ArrayList<String> = ArrayList()
        var texLinesOfPDFPagesLastSentences: ArrayList<Int> = ArrayList()
        var texLineIndexOfPDFPageLastSentence: ArrayList<Int> = ArrayList()

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

        pdfNumberOfPages = PDFUtils.getNumberOfPDFPages(filenames.outputPDFFilename)
        PDFUtils.readPDF(filenames.outputPDFFilename, vocabComponentArray, pdfPageLastSentences, pdfNumberOfPages)
        TexUtils.getTexLineNumbers(
            filenames.outputStoryFilename,
            pdfPageLastSentences,
            texLinesOfPDFPagesLastSentences,
            texLineIndexOfPDFPageLastSentence
        )

        TeXStyling.addStyling(vocabComponentArray, filenames.outputStoryFilename, "superscript")
        TeXStyling.addStyling(keyNameComponentArray, filenames.outputStoryFilename, "underline")

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
            "Graded Reader built!")
        succeedAlert.show() // OK -> Open PDF
    }
}