package org.idiosapps

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class TeXStyling {
    companion object {
        val SUPERSCRIPT_STYLING = "superscript"
        val UNDERLINE_STYLING = "underline"

        fun addStyling(vocab: MutableList<Vocab>,
                       markupType: String) {
            // prepare to replace content in outputStoryFile
            val outputStoryFilename = Filenames.outputTexFilename
            val path = Paths.get(outputStoryFilename)
            val charset = StandardCharsets.UTF_8
            var content = String(Files.readAllBytes(path), charset)
            val underline = "\\uline{"
            val superscript = "\\textsuperscript"

            vocab.forEachIndexed { index, vocabItem ->
                if (markupType == UNDERLINE_STYLING) {
                    content = content.replace(vocabItem.L2Word, "$underline+${vocabItem.L2Word}}")
                } else if (markupType == SUPERSCRIPT_STYLING) {
                    content = content.replace(vocabItem.L2Word,
                        "${vocabItem.L2Word}$superscript{${vocabItem.firstOccurencePage!! - 1}.${index + 1}}")
                }
            }
            Files.write(path, content.toByteArray(charset))
        }
    }
}