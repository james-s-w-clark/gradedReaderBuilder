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

        val vocab = VocabUtils.splitIntoParts(Filenames.inputVocabFilename)
        val names = VocabUtils.splitIntoParts(Filenames.inputKeyNamesFilename)

        PrintWriter(Filenames.outputTexFilename, "UTF-8").use { texWriter ->
            TexUtils.copyToTex(texWriter, Filenames.inputHeaderResource)
            TexUtils.copyToTex(texWriter, Filenames.inputTitleFilename)
            TexUtils.copyToTex(texWriter, Filenames.inputStoryFilename)

            SummaryPageUtils.writeVocabSection(texWriter, vocab) // TODO add summary / grammar / names pages too

            texWriter.append("\\end{document}")
        }

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