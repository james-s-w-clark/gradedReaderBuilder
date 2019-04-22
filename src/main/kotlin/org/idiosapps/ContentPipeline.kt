package org.idiosapps

import java.io.PrintWriter

// Process is:
// input    ->   tex    ->    pdf    ->    tex     ->    tex     -> pdf
//                                     + styling     + footers     final
class ContentPipeline {
    companion object {
        fun process() {
            var languageUsed = "mandarin"

            val filenames = Filenames() // load defaults from class
            var pdfNumberOfPages = 0

            var vocabArray: ArrayList<String> = ArrayList() // This is a list of all the input vocabulary
            var vocabComponentArray: ArrayList<ArrayList<String>> = ArrayList<ArrayList<String>>() // This an [array of [arrays containing input vocab split into parts]]
            var keyNameArray: ArrayList<String> = ArrayList()
            var keyNameComponentArray: ArrayList<ArrayList<String>> = ArrayList<ArrayList<String>>() // This an [array of [arrays containing input key names split into parts]]
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

            PDFUtils.xelatexToPDF(filenames.outputStoryFilename)

            pdfNumberOfPages = PDFUtils.getNumberOfPDFPages(filenames.outputPDFFilename, pdfNumberOfPages)
            PDFUtils.readPDF(filenames.outputPDFFilename, vocabComponentArray, pdfPageLastSentences, pdfNumberOfPages)
            TexUtils.getTexLineNumbers(filenames.outputStoryFilename, pdfPageLastSentences, texLinesOfPDFPagesLastSentences, texLineIndexOfPDFPageLastSentence)

            TeXStyling.addStyling(vocabComponentArray, filenames.outputStoryFilename, "superscript")
            TeXStyling.addStyling(keyNameComponentArray, filenames.outputStoryFilename, "underline")

            FooterUtils.addVocabFooters(vocabComponentArray, filenames.outputStoryFilename, texLinesOfPDFPagesLastSentences, languageUsed, pdfNumberOfPages, texLineIndexOfPDFPageLastSentence, pdfPageLastSentences)
            outputStoryTeXWriter.close()

            PDFUtils.xelatexToPDF(filenames.outputStoryFilename)
        }
    }
}