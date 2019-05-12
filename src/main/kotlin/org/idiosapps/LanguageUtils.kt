package org.idiosapps

import javafx.collections.FXCollections
import javafx.collections.ObservableList

class LanguageUtils {
    companion object {
        val supportedL1: ObservableList<String> = FXCollections.observableArrayList<String>("English")
        val supportedL2: ObservableList<String> = FXCollections.observableArrayList<String>("Chinese")

        fun getMarker(languageUsed: String): String {
            var languageMarker = ""
            when (languageUsed) {
                "mandarin" -> languageMarker = "\\pinyin{"
                "arabic" -> languageMarker = "\\arabicfont{"
                else -> System.out.println("Unrecognised language:$languageUsed")
            }
            return languageMarker
        }

        fun closeMarker(): String {
            return "}"
        }

        fun getMarkedL2Extra(vocabItem: Vocab): String{
            var L2Extra = ""
            return if (vocabItem.L2Extra == null || vocabItem.L2Extra.trim() == "")
                L2Extra
            else {
                L2Extra += getMarker("mandarin") // TODO get language properly
                L2Extra += vocabItem.L2Extra
                L2Extra += closeMarker()
                L2Extra = "($L2Extra)" // wrap with brackets for style
                L2Extra
            }
        }
    }
}