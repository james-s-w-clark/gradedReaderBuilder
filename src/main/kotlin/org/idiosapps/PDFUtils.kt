package org.idiosapps

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

class PDFUtils {
    companion object {
        fun xelatexToPDF (outputStoryFilename: String) {
//    TODO IMPORTANT wait until pdf has been generated before continuing!
            val operatingSystem = OSUtils.getOS()
            if (operatingSystem.contains("ubuntu")){
                val cmdXelatexToPDF = "xelatex -output-directory=./output output/outputStory.tex"
                Runtime.getRuntime().exec(cmdXelatexToPDF)
                TimeUnit.SECONDS.sleep(5);
            }
            else if (operatingSystem.contains("windows")){
                val process = Runtime.getRuntime().exec("cmd /c start /wait buildPDF.bat")
                val exitVal = process.waitFor()
            }
            else if (operatingSystem.contains("macos")){
            }
        }

        fun getNumberOfPDFPages (PDFFilename: String, pdfNumberOfPages: Int) : Int {
            val pdfFile = File(PDFFilename)
            val documentPDF: PDDocument = PDDocument.load(pdfFile)
            var pdfNumberOfPages = documentPDF.getNumberOfPages()
            return pdfNumberOfPages
        }

        // TODO split this into two functions: one for vocab pages, one for last sentences on pages.
        fun readPDF (PDFFilename: String, vocabComponentArray: ArrayList<ArrayList<String>>, pdfPageLastSentences: ArrayList<String>, pdfNumberOfPages: Int){
            // TODO use a method similar to fixPDFPageLastLine to fix 39->8217 immediately after reading in the PDF.
            val pdfFile = File(PDFFilename)
            val documentPDF: PDDocument = PDDocument.load(pdfFile)

            // Find the first instance of each vocabulary word
            try {
                vocabComponentArray.forEachIndexed { index, currentVocabComponent ->
                    var pageCounter = 1 // start at page 1 for each vocab Hanzi
                    var pdfPageText = ""

                    while(!pdfPageText.contains(currentVocabComponent[0])) {
                        val stripper = PDFTextStripper()
                        stripper.startPage = pageCounter
                        stripper.endPage = pageCounter
                        pdfPageText = stripper.getText(documentPDF)

                        if (pdfPageText.contains(currentVocabComponent[0])){
                            currentVocabComponent.add(Integer.toString(pageCounter))
                        }
                        pageCounter +=1
                        if (pageCounter>pdfNumberOfPages){
                            println("Word not found in story: " + currentVocabComponent[0])
                            break
                        }
                    }
                }
            }
            catch(e: Exception){    }

            // Get the last sentence of each page, and save to array
            try {
                var pdfPageText = ""
                var pageCounter = 2 // start where the story starts (accounting for title page)
                while (pageCounter<pdfNumberOfPages) { // for each page
                    val stripper = PDFTextStripper()
                    var pdfPageLastLine = ""
                    stripper.startPage = pageCounter
                    stripper.endPage = pageCounter
                    pdfPageText = stripper.getText(documentPDF)

                    var pdfPageTextLines: List<String> = pdfPageText.split("\r\n")

                    pdfPageLastLine = fixPDFPageLastLine(pdfPageTextLines[pdfPageTextLines.size-3])
                    // pdfPageTextLines[last] is blank, pdfPageTextLines[last-1] is page #, pdfPageTextLines[last-2] is last line of text (wanted)
                    pdfPageLastSentences.add(pdfPageLastLine) // todo improve efficiency; only need 1 (of maybe 20 lines)
                    pageCounter +=1
                }
            }
            catch(e: Exception){}
            documentPDF.close()
        }

        fun fixPDFPageLastLine (pdfPageLastTextLine: String): String {
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