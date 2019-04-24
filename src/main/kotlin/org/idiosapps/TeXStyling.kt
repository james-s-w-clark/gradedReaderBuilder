package org.idiosapps

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class TeXStyling {
    companion object {
        val SUPERSCRIPT_STYLING = "superscript"
        val UNDERLINE_STYLING = "underline"

        fun addStyling(inputArray: ArrayList<ArrayList<String>>, outputStoryFilename: String, markupType: String) {
            // prepare to replace content in outputStoryFile
            val path = Paths.get(outputStoryFilename)
            val charset = StandardCharsets.UTF_8
            var content = String(Files.readAllBytes(path), charset)

            // add styling to specific words
            inputArray.forEachIndexed { index, inputArrayElement ->
                if (markupType == UNDERLINE_STYLING) {
                    content = content.replace(inputArrayElement[0].toRegex(), "\\\\uline{" + inputArrayElement[0] + "}")
                } else if (markupType == SUPERSCRIPT_STYLING) {
                    // TODO make input vocab size-dependency cleaner.
                    if (inputArrayElement.size == 3) { // for Hanzi,En,index, size is 3.
                        var firstVocabOccurance: Int =
                            Integer.parseInt(inputArrayElement[2]) - 1 //-1 because of title page
                        content = content.replace(
                            inputArrayElement[0].toRegex(),
                            inputArrayElement[0] + "\\\\textsuperscript{" + firstVocabOccurance + "." + (index + 1) + "}"
                        )
                    } else if (inputArrayElement.size == 4) {  // for Hanzi,Pinyin,En,index, size is 4.
                        var firstVocabOccurance: Int =
                            Integer.parseInt(inputArrayElement[3]) - 1 //-1 because of title page
                        content = content.replace(
                            inputArrayElement[0].toRegex(),
                            inputArrayElement[0] + "\\\\textsuperscript{" + firstVocabOccurance + "." + (index + 1) + "}"
                        )
                    }
                }
            }
            Files.write(path, content.toByteArray(charset))
        }
    }
}