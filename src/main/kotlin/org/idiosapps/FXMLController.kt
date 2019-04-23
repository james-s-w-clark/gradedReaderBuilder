package org.idiosapps

import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.Region
import javafx.scene.text.Text
import java.io.PrintWriter


// https://github.com/openjfx/samples/blob/master/IDE/IntelliJ/Non-Modular/Gradle/hellofx/src/main/java/org/openjfx/FXMLController.java
class FXMLController {
    // for info on TeX installtions: https://tex.stackexchange.com/a/134377/103997
    var messagePDFLatexMissing = "Miktex is missing!\n"
    var messageXeLaTeXMissing = "XeLaTeX is missing!\n"

    var messageLinuxPDFLatex =
        "Please use \nsudo apt install texlive-latex-full\n to get Miktex" // TODO per-language install
    var messageLinuxXeLaTeX = "Please use \nsudo apt install texlive-xetex\n\n to get XeLaTeX"


    @FXML
    fun buildButtonClicked() {
        val operatingSystem = OSUtils.getOS() // TODO make messages clean, possibly with Alert helper
        if (!DependencyChecker.hasPDFLatex()) {
            val alert = Alert(
                AlertType.WARNING,
                messagePDFLatexMissing + messageLinuxPDFLatex
//                , ButtonType.YES, ButtonType.NO   // could be handy
            )
            alert.show()
        } else if (false) {
            val xeLaTeXMessage: String = DependencyChecker.hasXeLaTeX()
            val alert = Alert(
                AlertType.WARNING,
                xeLaTeXMessage
            )
            alert.show()
        } else {
            try {
                buildGradedReader()
            } catch (exception: Exception) { // e.g. Error: ctex.sty not found!
                val text = Text(exceptionToMessage(exception))
                text.setWrappingWidth(1000.0)
                val alert = Alert(
                    AlertType.WARNING,
                    exceptionToMessage(exception)
                )
                alert.dialogPane.minWidth = Region.USE_PREF_SIZE // TODO default size in Alert maker?
                alert.dialogPane.minHeight = Region.USE_PREF_SIZE
                alert.show()
            }
        }
    }

    fun initialize() {}

    fun exceptionToMessage(exception: Exception): String { // TODO for each Exception, store 3 OS-specific messages
        val exceptionString = exception.toString()
        val operatingSystem = OSUtils.getOS()
        if (operatingSystem == OSUtils.LINUX) {
            if (exceptionString.contains("ctex.sty")) {
                return "Please install\nsudo apt-get install texlive-lang-chinese"
            } else
                return "Unknown error for $operatingSystem"
        } else {
            return "Unknown error for $operatingSystem," +
                    " please leave issue on gradedReaderBuilder Github"
        }
    }

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

        System.out.println("Copied to TeX")


        SummaryPageWriter.writeVocabSection(outputStoryTeXWriter, filenames.inputVocabFilename, vocabComponentArray)
        // todo WriteSummaryPage.writeTexGrammar

        outputStoryTeXWriter.append("\\end{document}")
        outputStoryTeXWriter.close()

        PDFUtils.xelatexToPDF()

        pdfNumberOfPages = PDFUtils.getNumberOfPDFPages(filenames.outputPDFFilename, pdfNumberOfPages)
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
    }
}