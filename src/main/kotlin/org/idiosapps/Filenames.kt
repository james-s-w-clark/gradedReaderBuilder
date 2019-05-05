package org.idiosapps

import java.io.File
import java.net.URL

class Filenames {
    companion object {
        val inputPrefix: String = "./input/"
        val outputPrefix: String = "./output/"

        val inputHeaderResource: URL = this::class.java.classLoader.getResource("org.idiosapps/texHeader")
        val inputTitleFilename: String = inputPrefix + "title"
        val inputStoryFilename: String = inputPrefix + "story"

        val inputVocabFilename: String = inputPrefix + "vocab"
        val inputKeyNamesFilename: String = inputPrefix + "names"

        val outputTexFilename: String = outputPrefix + "outputStory.tex"
        val outputPDFFilename: String = outputPrefix + "outputStory.pdf"

        fun checkInputs() {
            val inputArray = arrayListOf(
                inputTitleFilename,
                inputStoryFilename,
                inputVocabFilename,
                inputKeyNamesFilename
            )

            for (input in inputArray) {
                val inputFile = File(input)
                if (!inputFile.exists()) { // could also check for size > 0 on required inputs
                    throw Exception("Input does not exist: $input")
                }
            }
        }

        fun hasContent(filename: String): Boolean {
            val inputFile = File(filename)
            return inputFile.length() > 0
        }
    }
}