package org.idiosapps

class Filenames {
    val resourcePrefix: String = "./src/main/resources/org.idiosapps/"
    val outputPrefix: String = "./output/"


    val inputHeaderFilename: String = resourcePrefix + "header"
    val inputTitleFilename: String = resourcePrefix + "title"
    val inputStoryFilename: String = resourcePrefix + "story"

    val inputVocabFilename: String = resourcePrefix + "vocab"
    val inputKeyNamesFilename: String = resourcePrefix + "names"

    val outputStoryFilename: String = outputPrefix + "outputStory.tex"
    val outputPDFFilename: String = outputPrefix + "outputStory.pdf"
}