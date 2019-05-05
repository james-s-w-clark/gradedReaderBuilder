package org.idiosapps

import java.io.PrintWriter

class SummaryPageUtils {
    // TODO fun writeTeXGrammarSection
    // TODO fun writeTeXQuestionsSection
    // TODO fun writeNamesSection
    companion object {
        const val endLine = "\\\\"
        fun writeVocabSection(
            outputStoryWriter: PrintWriter,
            vocab: MutableList<Vocab>
        ) {
            outputStoryWriter.println("\\clearpage")
            outputStoryWriter.println("\\setlength{\\parindent}{0ex}") // remove indenting
            outputStoryWriter.println("\\centerline{Vocabulary}")     // add page title

            vocab.forEachIndexed {index, vocabItem ->
                var L2Extra = LanguageUtils.getMarkedL2Extra(vocabItem)
                val vocabLine = "${index + 1}. ${vocabItem.L2Word} $L2Extra ${vocabItem.L1Word}$endLine"
                outputStoryWriter.println(vocabLine)
            }
        }
    }
}