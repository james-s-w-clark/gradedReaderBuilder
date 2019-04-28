package org.idiosapps

import org.idiosapps.OSUtils.Companion.SPACE
import java.io.PrintWriter

class SummaryPageWriter {
    // TODO fun writeTeXGrammarSection
    // TODO fun writeTeXQuestionsSection
    companion object {
        val endLine = "\\\\"
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