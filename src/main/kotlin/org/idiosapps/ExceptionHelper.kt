package org.idiosapps

import org.idiosapps.OSUtils.Companion.PDFTEX
import org.idiosapps.OSUtils.Companion.XETEX

class ExceptionHelper {
    companion object {
        val exceptionOSMap: MutableMap<String, String> = makeExceptionHashmap()
        val CTEX = "ctex"
        val EXPL3 = "expl3"

        fun exceptionToMessage(exception: Exception): String {
            if (exception.toString().contains(CTEX)) {
                return getExceptionMessage(CTEX)
            } else if (exception.toString().contains(XETEX)) {
                return getExceptionMessage(XETEX)
            } else if (exception.toString().contains(PDFTEX)) {
                return getExceptionMessage(PDFTEX)
            } else if (exception.toString().contains("Input")) {
                return exception.message.toString() // Exception already contains missing input file info
            } else {
                // log out start of stacktrace - should help with any Exception
                exception.printStackTrace()
                val stackTrace = exception.printStackTrace().toString()
                return stackTrace.substring(0, Math.min(20, stackTrace.length)) // alert Max 20 chars of Exception
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
                OSUtils.LINUX + CTEX, "Please install\n" +
                        "sudo apt-get install texlive-lang-chinese"
            )
            exceptionOSMap.put(OSUtils.WINDOWS + CTEX, "ctex.sty is missing!\n" +
                    "in TeX Live Manager \"Load default\" Repository, then\n" +
                    "install ctex.\n")
            exceptionOSMap.put(OSUtils.MACOS + CTEX, "TODO CTEX message")

            // pdfTeX error
            exceptionOSMap.put(
                OSUtils.LINUX + PDFTEX, "TeX Live missing!\n" +
                        "Please install: \n" +
                        "sudo apt install texlive-latex-recommended"
            )
            exceptionOSMap.put(
                OSUtils.WINDOWS + PDFTEX,
                "pdfTeX not found! Please install from\n" +
                        " https://www.tug.org/texlive/acquire-netinstall.html\n" +
                        "If you already installed, please restart!"
            )
            exceptionOSMap.put(OSUtils.MACOS + PDFTEX, "TODO PDFLatex message")

            // XETEX error
            exceptionOSMap.put(
                OSUtils.LINUX + XETEX, "Please use\n" +
                        "sudo apt install texlive-xetex\n" +
                        "to get XETEX"
            )
            exceptionOSMap.put(OSUtils.WINDOWS + XETEX, "XETEX is missing!\n" +
                    "in TeX Live Manager \"Load default\" Repository, then\n" +
                    "install XETEX.")
            exceptionOSMap.put(OSUtils.MACOS + XETEX, "TODO XETEX message")

            // expl3.sty error - needed for Experimental LaTeX 3
            exceptionOSMap.put(
                OSUtils.LINUX + EXPL3, "expl3.sty is missing!\n" +
                        "I'm not sure of the fix."
            )
            exceptionOSMap.put(OSUtils.WINDOWS + EXPL3, "expl3.sty is missing!\n" +
                    "Please install TeX Live with recommended/basic install.")
            exceptionOSMap.put(OSUtils.MACOS + EXPL3, "expl3.sty is missing!\n" +
                    "Please reinstall TeX Live - not sure of better fix.")


            return exceptionOSMap
        }
    }
}