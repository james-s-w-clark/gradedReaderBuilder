package org.idiosapps

class ExceptionHelper {
    companion object {
        val exceptionOSMap: MutableMap<String, String> = makeExceptionHashmap()
        val exceptionCTEX = "ctex"
        val exceptionPdfTeX = "pdftex"
        val exceptionXeLaTeX = "xelatex"

        fun exceptionToMessage(exception: Exception): String {
            if (exception.toString().contains(exceptionCTEX)) {
                return getExceptionMessage(exceptionCTEX)
            } else {
                // log out start of stacktrace - should help with any Exception
                val stackTrace = exception.printStackTrace().toString()
                return stackTrace.substring(0, stackTrace.indexOf("at java."))
            }
        }

        fun getExceptionMessage(exception: String): String {
            return exceptionOSMap.getValue(OSUtils.getOS() + exception)
        }

        private fun makeExceptionHashmap(): MutableMap<String, String> {
            // for info on TeX installtions: https://tex.stackexchange.com/a/134377/103997
            val exceptionOSMap: MutableMap<String, String> = HashMap()

            // CTEX error - needed for Chinese typesetting
            exceptionOSMap.put(
                OSUtils.LINUX + exceptionCTEX, "Please install\n" +
                        "sudo apt-get install texlive-lang-chinese"
            )
            exceptionOSMap.put(OSUtils.WINDOWS + exceptionCTEX, "TODO CTEX message")
            exceptionOSMap.put(OSUtils.MACOS + exceptionCTEX, "TODO CTEX message")

            // pdfTeX error
            exceptionOSMap.put(
                OSUtils.LINUX + exceptionPdfTeX, "TeX Live missing!\n" +
                        "Please install: \n" +
                        "sudo apt install texlive-latex-recommended"
            )
            exceptionOSMap.put(
                OSUtils.WINDOWS + exceptionPdfTeX,
                "pdfTeX not found! Please install from\n" +
                        " https://www.tug.org/texlive/acquire-netinstall.html\n" +
                        "If you already installed, please restart!"
            )
            exceptionOSMap.put(OSUtils.MACOS + exceptionPdfTeX, "TODO PDFLatex message")

            // XeLaTeX error
            exceptionOSMap.put(
                OSUtils.LINUX + exceptionXeLaTeX, "Please use \n" +
                        "sudo apt install texlive-xetex\n" +
                        "to get XeLaTeX"
            )
            exceptionOSMap.put(OSUtils.WINDOWS + exceptionXeLaTeX, "TODO XeLaTeX message")
            exceptionOSMap.put(OSUtils.MACOS + exceptionXeLaTeX, "TODO XeLaTeX message")

            return exceptionOSMap
        }
    }
}