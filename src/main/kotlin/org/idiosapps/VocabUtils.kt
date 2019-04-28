package org.idiosapps

import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class VocabUtils {
    companion object {
        fun getOrderIndicies(vocab: MutableList<Vocab>) { // TODO get from story rather than just vocab order
            vocab.forEachIndexed { index, vocabItem ->
                vocabItem.vocabOrderIndex = index
            }
        }

        fun splitIntoParts(
            inputFilename: String
        ) : MutableList<Vocab> {
            val vocabList: MutableList<Vocab> = ArrayList()

            val inputFile = File(inputFilename)
            val scanner = Scanner(inputFile, "UTF-8")
            while (scanner.hasNextLine()) {
                var vocabLine: String = scanner.nextLine()

                // split each entry into n components (e.g. Chinese, Pinyin, and then English)
                var vocabSplitParts = ArrayList<String>()
                while (vocabLine.contains("|")) {
                    vocabSplitParts.add(vocabLine.substring(0, vocabLine.indexOf("|")))
                    vocabLine = vocabLine.substring(vocabLine.indexOf("|") + 1, vocabLine.length)
                }
                vocabSplitParts.add(vocabLine.substring(0, vocabLine.length)) // add the part after the last |

                lateinit var vocab: Vocab
                when (vocabSplitParts.size) {
                    2 -> vocab = Vocab(vocabSplitParts[0],
                        null,
                        vocabSplitParts[1])
                    3 -> vocab = Vocab(vocabSplitParts[0],
                        vocabSplitParts[1],
                        vocabSplitParts[2])
                }
                vocabList.add(vocab)
            }
            scanner.close()
            return vocabList
        }
    }
}