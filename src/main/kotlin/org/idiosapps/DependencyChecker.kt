package org.idiosapps

import org.idiosapps.OSUtils.Companion.LINUX
import org.idiosapps.OSUtils.Companion.WINDOWS
import java.io.IOException


class DependencyChecker {
    companion object {

        val PDFTEX = "pdfTeX"
        fun hasPDFLatex(): Boolean {
            if (OSUtils.getOS() == (LINUX)) {
                try {
                    val process = Runtime.getRuntime().exec("$PDFTEX --version") //TODO $PDFLATEX --version
                    val inputStream = process.inputStream
                    val s = java.util.Scanner(inputStream).useDelimiter("\\A")
                    lateinit var line: String
                    while (s.hasNext()) {
                        line = s.next()
                        if (line.contains(PDFTEX)) // sufficiently implies installation
                            return true
                    }
                } catch (exception: IOException) {
                    exception.printStackTrace()
                    return false
                }
            } else if (OSUtils.getOS() == (WINDOWS)) {
                try {
                    val process = Runtime.getRuntime().exec("cmd.exe /c $PDFTEX --version")
                    val inputStream = process.inputStream
                    val s = java.util.Scanner(inputStream).useDelimiter("\\A")
                    lateinit var line: String
                    while (s.hasNext()) {
                        line = s.next()
                        if (line.contains(PDFTEX)) // sufficiently implies installation
                            return true
                    }
                } catch (exception: IOException) {
                    exception.printStackTrace()
                    return false
                }
            }
            return false // don't have PDFLatex
        }

        val hasXeLaTeX = "XeTeX "
        fun hasXeLaTeX(): Boolean { // TODO use exceptions?
            try {
                val process = Runtime.getRuntime().exec("xelatex --version")
                val inputStream = process.inputStream
                val s = java.util.Scanner(inputStream).useDelimiter("\\A")
                var line = ""
                while (s.hasNext()) {
                    line = s.next()
//                    System.out.println(line)
                    if (line.contains(hasXeLaTeX)) {    // sufficiently implies installation
                        return true
                    } else if (line.contains("Error:")) {
//                        System.out.println("ERROR")
//                        System.out.println(line)
                        return false
                    }
                }
            } catch (exception: IOException) {
                exception.printStackTrace()
                return false
            }
            return false
        }
    }
}