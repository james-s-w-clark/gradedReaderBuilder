package org.idiosapps

import java.io.File

class Filenames {
    companion object {
        private const val inputPrefix: String = "./input/"
        private const val outputPrefix: String = "./output/"

        val inputHeaderResource = this::class.java.classLoader.getResourceAsStream("org.idiosapps/texHeader")

        const val inputTitleFilename: String = inputPrefix + "title"
        const val inputStoryFilename: String = inputPrefix + "story"

        const val inputVocabFilename: String = inputPrefix + "vocab"
        const val inputKeyNamesFilename: String = inputPrefix + "names"

        const val outputTexFilename: String = outputPrefix + "outputStory.tex"
        const val outputPDFFilename: String = outputPrefix + "outputStory.pdf"

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