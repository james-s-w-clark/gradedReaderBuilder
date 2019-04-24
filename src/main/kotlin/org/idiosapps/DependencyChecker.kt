package org.idiosapps

import java.io.IOException


class DependencyChecker {
    companion object {

        val HAS_pdfTeX = "pdfTeX"
        fun hasPDFLatex(): Boolean {
            if (OSUtils.getOS().equals(OSUtils.LINUX)) {
                try {
                    val process = Runtime.getRuntime().exec("pdflatex --version")
                    val inputStream = process.inputStream
                    val s = java.util.Scanner(inputStream).useDelimiter("\\A")
                    var line = ""
                    while (s.hasNext()) {
                        line = s.next()
//                        System.out.println(line)
                        if (line.contains(HAS_pdfTeX)) // sufficiently implies installation
                            return true
                    }
                } catch (exception: IOException) {
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
                return false
            }
            return false
        }
    }
}