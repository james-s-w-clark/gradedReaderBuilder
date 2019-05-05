package org.idiosapps

import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*

class TexUtils {

    companion object {
        fun putTexLineNumbers(
            pagesInfo: MutableList<PageInfo>
        ) {
            val outputStoryFilename = Filenames.outputTexFilename
            val inputFile = File(outputStoryFilename) // get file ready
            val scanner = Scanner(inputFile, "UTF-8")
            var pdfPageLastSentenceIndexer = 0
            var lineCount = 0

            while (scanner.hasNextLine()) {
                var line: String = scanner.nextLine()
                if (pdfPageLastSentenceIndexer < pagesInfo.size) {
                    val pageInfo = pagesInfo[pdfPageLastSentenceIndexer]
                    if (line.contains(pageInfo.pdfPageLastSentence)) {
                        pageInfo.texLinesOfPDFPagesLastSentence = lineCount
                        pageInfo.texLineIndexOfPDFPageLastSentence = line.lastIndexOf(pageInfo.pdfPageLastSentence)
                        pdfPageLastSentenceIndexer++
                    }
                    lineCount++
                }
            }
            scanner.close()
        }

        fun copyToTex(outputStoryWriter: PrintWriter, inputFilename: String) {
            val inputFile = File(inputFilename)
            val scanner = Scanner(inputFile, "UTF-8")

            while (scanner.hasNextLine()) {
                val line: String = scanner.nextLine() // read all lines
                if (line.contains("Chapter")) {   // add chapter markup if dealing with a chapter
                    outputStoryWriter.println("\\clearpage")
                    outputStoryWriter.println("{\\centering \\large")
                    outputStoryWriter.println("{\\uline{" + line + "}}\\\\}")
                } else {     // else (for now) assume we have ordinary text
                    outputStoryWriter.println(line)
                }
            }
            scanner.close()
        }

        // can't load resource as file, so we have to read from a stream of the resource
        fun copyToTex(outputStoryWriter: PrintWriter, resourceStream: InputStream) {
            val reader = InputStreamReader(resourceStream, StandardCharsets.UTF_8)
            val bufferedReader = BufferedReader(reader)
            val lines = bufferedReader.lines()
            lines.forEach { line ->
                outputStoryWriter.println(line)
            }
        }
    }
}