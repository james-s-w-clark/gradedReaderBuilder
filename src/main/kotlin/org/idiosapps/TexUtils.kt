package org.idiosapps

import java.io.File
import java.io.PrintWriter
import java.util.*

class TexUtils {

    companion object {
        fun getTexLineNumbers(
            outputStoryFilename: String,
            pdfPageLastSentences: ArrayList<String>,
            texLinesOfPDFPagesLastSentences: ArrayList<Int>,
            texLineIndexOfpdfPageLastSentence: ArrayList<Int>
        ) {
            val inputFile = File(outputStoryFilename) // get file ready
            val scan = Scanner(inputFile)
            var pdfPageLastSentenceIndexer = 0
            var lineCount = 0

            while (scan.hasNextLine()) {
                var line: String = scan.nextLine()
                if (pdfPageLastSentenceIndexer < pdfPageLastSentences.size) {
                    if (line.contains(pdfPageLastSentences[pdfPageLastSentenceIndexer])) {
                        texLinesOfPDFPagesLastSentences.add(lineCount)
                        texLineIndexOfpdfPageLastSentence.add(line.lastIndexOf(pdfPageLastSentences[pdfPageLastSentenceIndexer]))
                        pdfPageLastSentenceIndexer++
                    }
                    lineCount++
                }
            }
            scan.close()
        }

        fun copyToTex(outputStoryWriter: PrintWriter, inputFilename: String) {
            val inputFile = File(inputFilename)
            val scan = Scanner(inputFile)

            while (scan.hasNextLine()) {
                val line: String = scan.nextLine() // read all lines
                if (line.contains("Chapter")) {   // add chapter markup if dealing with a chapter
                    outputStoryWriter.println("\\clearpage")
                    outputStoryWriter.println("{\\centering \\large")
                    outputStoryWriter.println("{\\uline{" + line + "}}\\\\}")
                } else {     // else (for now) assume we have ordinary text
                    outputStoryWriter.println(line)
                }
            }
            scan.close()
        }
    }
}