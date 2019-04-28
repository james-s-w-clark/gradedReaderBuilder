package org.idiosapps

import java.io.File
import java.util.*

class VocabUtils {
    companion object {
        fun getVocabIndicies(vocabComponentArray: ArrayList<ArrayList<String>>) {
            vocabComponentArray.forEachIndexed { index, vocabElement ->
                vocabElement.add(Integer.toString(index))
            }
        }

        fun splitVocabIntoParts(
            inputFilename: String,
            inputComponentArray: ArrayList<ArrayList<String>>
        ) {
            val inputFile = File(inputFilename)
            val scanner = Scanner(inputFile, "UTF-8")
            var vocabIndex = 0
            while (scanner.hasNextLine()) {
                var vocabLine: String = scanner.nextLine()

                // split each entry into n components (e.g. Chinese, Pinyin, and then English)
                var vocabSplitParts = ArrayList<String>()
                while (vocabLine.contains("|")) {
                    vocabSplitParts.add(vocabLine.substring(0, vocabLine.indexOf("|")))
                    vocabLine = vocabLine.substring(vocabLine.indexOf("|") + 1, vocabLine.length)
                }
                vocabSplitParts.add(vocabLine.substring(0, vocabLine.length)) // add the part after the last |

                // initialise an array, add vocabSplitParts, then remove the initialising entry. TODO clean this up (no rush!)
                var arrayListInitialiser: ArrayList<String> = ArrayList(Collections.singletonList(""))
                inputComponentArray.add(arrayListInitialiser)

                var addVocabPartIndex = 0
                while (addVocabPartIndex < vocabSplitParts.size) {
                    inputComponentArray[vocabIndex].add(vocabSplitParts[addVocabPartIndex])
                    addVocabPartIndex += 1
                }
                inputComponentArray[vocabIndex].remove(inputComponentArray[vocabIndex][0]) // "uninitilaise" ArrayList empty entry
                vocabIndex += 1
            }
            scanner.close()
        }
    }
}