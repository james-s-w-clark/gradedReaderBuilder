package org.idiosapps

import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.Region
import org.idiosapps.OSUtils.Companion.LINUX
import org.idiosapps.OSUtils.Companion.MACOS
import org.idiosapps.OSUtils.Companion.WINDOWS
import java.io.PrintWriter


// https://github.com/openjfx/samples/blob/master/IDE/IntelliJ/Non-Modular/Gradle/hellofx/src/main/java/org/openjfx/FXMLController.java
class FXMLController {
    val exceptionOSMap: MutableMap<String, String> = HashMap<String, String>()
    val exceptionCTEX = "CTEX"
    val exceptionPDFLatex = "MIKTEX"
    val exceptionXELATEX = "XELATEX"

    @FXML
    fun buildButtonClicked() {
        val OS = OSUtils.getOS() // TODO make messages clean, possibly with Alert helper
        if (!DependencyChecker.hasPDFLatex()) {
            val alert = Alert(
                AlertType.WARNING,
                exceptionOSMap.getValue(OSUtils.getOS() + exceptionPDFLatex)
//                messagePDFLatexMissing + messageLinuxPDFLatex

//                , ButtonType.YES, ButtonType.NO   // could be handy
            )
            alert.show()
        } else if (!DependencyChecker.hasXeLaTeX()) {
            val alert = Alert(
                AlertType.WARNING,
                exceptionOSMap.getValue(OSUtils.getOS() + exceptionXELATEX)
            )
            alert.show()
        } else {
            try {
                buildGradedReader()
            } catch (exception: Exception) { // e.g. Error: ctex.sty not found!
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

    fun initialize() {
        makeExceptionHashmap()
    }

    fun makeExceptionHashmap() {
        // for info on TeX installtions: https://tex.stackexchange.com/a/134377/103997

        // CTEX error
        exceptionOSMap.put(
            LINUX + exceptionCTEX, "Please install\n" +
                    "sudo apt-get install texlive-lang-chinese"
        )
        exceptionOSMap.put(WINDOWS + exceptionCTEX, "TODO CTEX message")
        exceptionOSMap.put(MACOS + exceptionCTEX, "TODO CTEX message")

        // Miktex error
        exceptionOSMap.put(
            LINUX + exceptionPDFLatex, "Please use \n" +
                    "sudo apt install texlive-latex\n" +
                    " to get Miktex"
        )
        exceptionOSMap.put(WINDOWS + exceptionPDFLatex, "TODO MIKTEX message")
        exceptionOSMap.put(MACOS + exceptionPDFLatex, "TODO MIKTEX message")

        // XELATEX error
        exceptionOSMap.put(
            LINUX + exceptionXELATEX, "Please use \n" +
                    "sudo apt install texlive-xetex\n" +
                    "to get XeLaTeX"
        )
        exceptionOSMap.put(WINDOWS + exceptionXELATEX, "TODO XELATEX message")
        exceptionOSMap.put(MACOS + exceptionXELATEX, "TODO XELATEX message")
    }

    fun exceptionToMessage(exception: Exception): String { // TODO for each Exception, store 3 OS-specific messages
        val exceptionString = exception.toString()
        val OS = OSUtils.getOS()
        if (exceptionString.contains("ctex.sty")) {
            return exceptionOSMap.getValue(OS + "CTEX")
        } else {
            return "Unknown error for $OS"
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
    }
}