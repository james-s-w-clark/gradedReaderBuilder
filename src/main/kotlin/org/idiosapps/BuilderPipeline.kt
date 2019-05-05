package org.idiosapps

import javafx.concurrent.Task
import javafx.scene.Parent
import org.idiosapps.TeXStyling.Companion.SUPERSCRIPT_STYLING
import org.idiosapps.TeXStyling.Companion.UNDERLINE_STYLING
import java.io.PrintWriter

class BuilderPipeline : Task<Parent>() {
    override fun call(): Parent? {
        buildGradedReader()
        return null
    }

    private fun buildGradedReader() {
        updateProgress(0.1, 1.0)

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

        updateProgress(0.5, 1.0)

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
        updateProgress(1.0, 1.0)
        succeeded()
    }
}