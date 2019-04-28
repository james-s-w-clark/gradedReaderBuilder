package org.idiosapps

class LanguageUtils {
    companion object {
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
            if (vocabItem.L2Extra == null)
                return L2Extra
            else {
                L2Extra += getMarker("mandarin") // TODO get language properly
                L2Extra += vocabItem.L2Extra
                L2Extra += closeMarker()
                L2Extra = "($L2Extra)" // wrap with brackets for style
                return L2Extra
            }
        }
    }
}