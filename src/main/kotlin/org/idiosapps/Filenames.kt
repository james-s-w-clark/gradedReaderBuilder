package org.idiosapps

class Filenames {
    val inputPrefix: String = "./input/"
    val outputPrefix: String = "./output/"


    val inputHeaderFilename: String = inputPrefix + "header"
    val inputTitleFilename: String = inputPrefix + "title"
    val inputStoryFilename: String = inputPrefix + "story"

    val inputVocabFilename: String = inputPrefix + "vocab"
    val inputKeyNamesFilename: String = inputPrefix + "names"

    val outputStoryFilename: String = outputPrefix + "outputStory.tex"
    val outputPDFFilename: String = outputPrefix + "outputStory.pdf"
}