package org.idiosapps

class ExceptionHelper {
    companion object {
        val exceptionOSMap: MutableMap<String, String> = makeExceptionHashmap()
        val exceptionCTEX = "ctex"
        val exceptionPDFLatex = "MIKTEX"
        val exceptionXELATEX = "XELATEX"

        fun exceptionToMessage(exception: Exception): String {
            if (exception.toString().contains(exceptionCTEX)) {
                return getExceptionMessage(exceptionCTEX)
            } else {
                return "Unknown error for ${OSUtils.getOS()}"
            }
        }

        fun getExceptionMessage(exception: String): String {
            return exceptionOSMap.getValue(OSUtils.getOS() + exception)
        }

        private fun makeExceptionHashmap(): MutableMap<String, String> {
            // for info on TeX installtions: https://tex.stackexchange.com/a/134377/103997
            val exceptionOSMap: MutableMap<String, String> = HashMap()

            // CTEX error
            exceptionOSMap.put(
                OSUtils.LINUX + exceptionCTEX, "Please install\n" +
                        "sudo apt-get install texlive-lang-chinese"
            )
            exceptionOSMap.put(OSUtils.WINDOWS + exceptionCTEX, "TODO CTEX message")
            exceptionOSMap.put(OSUtils.MACOS + exceptionCTEX, "TODO CTEX message")

            // Miktex error
            exceptionOSMap.put(
                OSUtils.LINUX + exceptionPDFLatex, "TeX Live missing!\n" +
                        "Please install: \n" +
                        "sudo apt install texlive-latex-recommended"
            )
            exceptionOSMap.put(OSUtils.WINDOWS + exceptionPDFLatex, "TODO MIKTEX message")
            exceptionOSMap.put(OSUtils.MACOS + exceptionPDFLatex, "TODO MIKTEX message")

            // XELATEX error
            exceptionOSMap.put(
                OSUtils.LINUX + exceptionXELATEX, "Please use \n" +
                        "sudo apt install texlive-xetex\n" +
                        "to get XeLaTeX"
            )
            exceptionOSMap.put(OSUtils.WINDOWS + exceptionXELATEX, "TODO XELATEX message")
            exceptionOSMap.put(OSUtils.MACOS + exceptionXELATEX, "TODO XELATEX message")

            return exceptionOSMap
        }
    }
}