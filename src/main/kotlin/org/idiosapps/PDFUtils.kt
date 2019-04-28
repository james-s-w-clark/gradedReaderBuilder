package org.idiosapps

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.idiosapps.OSUtils.Companion.LINUX
import org.idiosapps.OSUtils.Companion.LINUX_SHELL_PREFIX
import org.idiosapps.OSUtils.Companion.MACOS
import org.idiosapps.OSUtils.Companion.SPACE
import org.idiosapps.OSUtils.Companion.WINDOWS
import org.idiosapps.OSUtils.Companion.WINDOWS_COMMAND_PREFIX
import java.io.File
import java.util.*

class PDFUtils {
    companion object {
        fun xelatexToPDF() {
            val XETEX_NONSTOP = "xelatex -interaction=nonstopmode"
            val XETEX_OUTDIR = "-output-directory=./output"
            val XETEX_INFILE = "output/outputStory.tex" // this .tex is the output of previous methods
            val XETEX_COMMAND = XETEX_NONSTOP + SPACE + XETEX_OUTDIR + SPACE + XETEX_INFILE // same for all OS

            val operatingSystem = OSUtils.getOS()
            lateinit var command: String
            try {
                when {
                    operatingSystem.contains(LINUX) -> {
                        command = LINUX_SHELL_PREFIX.toString() + SPACE + XETEX_COMMAND // TODO fix when back on Ubuntu
                    }
                    operatingSystem.contains(WINDOWS) -> {
                        command = WINDOWS_COMMAND_PREFIX + SPACE + XETEX_COMMAND
                    }
                    operatingSystem.contains(MACOS) -> { // maybe same as linux?
                        command = LINUX_SHELL_PREFIX.toString() + SPACE + XETEX_COMMAND // TODO find the command for MacOS
                    }
                }
                runProcess(command)
            } catch (exception: Exception) {
                throw exception
            }
        }

        fun runProcess(command: String) {
            val process = Runtime.getRuntime().exec(command)
            val inputStream = process.inputStream
            val scanner = java.util.Scanner(inputStream, "UTF-8")
                .useDelimiter("\n")
            scanner.use {
                while (scanner.hasNext()) {
                    val line = scanner.next()
                    if (line.contains("Error:")) {
                        throw Exception(line)
                    }
                }
            }
            process.waitFor()
            scanner.close()
        }

        fun getNumberOfPDFPages(): Int {
            val pdfFile = File(Filenames.outputPDFFilename)
            PDDocument.load(pdfFile).use { pdDocument ->
                return pdDocument.numberOfPages
            }
        }

        // TODO split this into two functions: one for vocab pages, one for last sentences on pages.
        fun getPdfPageInfo(
            vocabComponentArray: ArrayList<ArrayList<String>>
        ) : MutableList<PageInfo> {
            // TODO use a method similar to fixPDFPageLastLine to fix 39->8217 immediately after reading in the PDF.
            val pdfFilename = Filenames.outputPDFFilename
            val pdfFile = File(pdfFilename)
            val documentPDF: PDDocument = PDDocument.load(pdfFile)
            val pdfNumberOfPages = getNumberOfPDFPages()

            val pagesInfo: MutableList<PageInfo> = ArrayList()

            // Find the first instance of each vocabulary word
            try {
                vocabComponentArray.forEachIndexed { index, currentVocabComponent ->
                    var pageCounter = 1 // start at page 1 for each vocab Hanzi
                    var pdfPageText = ""

                    while (!pdfPageText.contains(currentVocabComponent[0])) {
                        val stripper = PDFTextStripper()
                        stripper.startPage = pageCounter
                        stripper.endPage = pageCounter
                        pdfPageText = stripper.getText(documentPDF)

                        if (pdfPageText.contains(currentVocabComponent[0])) {
                            currentVocabComponent.add(Integer.toString(pageCounter))
                        }
                        pageCounter += 1
                        if (pageCounter > pdfNumberOfPages) {
                            println("Word not found in story: " + currentVocabComponent[0])
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Get the last sentence of each page, and save to array
            try {
                var pdfPageText = ""
                var pageCounter = 2 // start where the story starts (accounting for title page)
                while (pageCounter < pdfNumberOfPages) { // for each page
                    val stripper = PDFTextStripper()
                    var pdfPageLastLine = ""
                    stripper.startPage = pageCounter
                    stripper.endPage = pageCounter
                    pdfPageText = stripper.getText(documentPDF)

                    var textLineDelimiter = OSUtils.getDelimiter()

                    val pdfPageTextLines: List<String> = pdfPageText.split(textLineDelimiter)

                    pdfPageLastLine = fixPDFPageLastLine(pdfPageTextLines[pdfPageTextLines.size - 3])
                    // pdfPageTextLines[last] is blank, pdfPageTextLines[last-1] is page #, pdfPageTextLines[last-2] is last line of text (wanted)

                    val pageInfo = PageInfo(pageCounter,pdfPageLastLine,null,null)
                    pagesInfo.add(pageInfo) // todo improve efficiency; only need 1 (of maybe 20 lines)
                    pageCounter += 1
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            documentPDF.close()
            return pagesInfo
        }

        fun fixPDFPageLastLine(pdfPageLastTextLine: String): String {
            // Convert string to charArray (easier manipulation; String is immutable)
            var lineAsChars = pdfPageLastTextLine.toCharArray()

            // Scan through for 8217 and turn it into 39'
            // note: a 39' was converted to 25,32 (2 chars) at first. later just 8217
            // This fixes ' being misread
            var characterIndex = 0
            while (characterIndex < lineAsChars.size) {

                if (lineAsChars[characterIndex].toInt() == 8217) {
                    lineAsChars[characterIndex] = 39.toChar()
                }
                characterIndex++
            }

            // Then just scan through and remove the 0s
            // this fixes an ascii/unicode mix-up (I think).
            // this stopped appearing the same time 25,32->8217. Not sure why.
            // TODO check if we only still have a problem of 39->8217; can maybe remove this
            var cleanedString: StringBuilder = StringBuilder()
            var characterIndexForZeroes = 0

            while (characterIndexForZeroes < lineAsChars.size) {
                if (lineAsChars[characterIndexForZeroes].toInt() == 0) {
                } // don't do anything if the char has Int value of 0
                else { // add every character to stringbuilder
                    cleanedString.append(lineAsChars[characterIndexForZeroes])
                }
                characterIndexForZeroes++
            }
            return cleanedString.toString()
        }
    }
}