package org.idiosapps

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayList

class FooterUtils {
    companion object {
        fun addVocabFooters(
            vocabComponentArray: ArrayList<ArrayList<String>>,
            outputStoryFilename: String,
            texLinesPDFPageLastSentence: ArrayList<Int>,
            languageUsed: String,
            pdfNumberOfPages: Int,
            texLinesLastSentenceIndex: ArrayList<Int>,
            pdfPageLastSentences: ArrayList<String>
        ) {
            var pageNumber = 2
            val outputStoryFile = File(outputStoryFilename)
            var footers = Footers()

            VocabUtils.getVocabIndicies(vocabComponentArray) // add vocab "order of appearance" index // todo create this automatically.

            var languageMarker = LanguageUtils.prefixLaTeXLanguageMarker(languageUsed)
            val texPath = Paths.get(outputStoryFilename)
            val lines = Files.readAllLines(texPath, StandardCharsets.UTF_8)

            while (pageNumber < pdfNumberOfPages) {
                generateFooters(vocabComponentArray, pageNumber, languageMarker, footers)

                var footerCallText: String = " \\thispagestyle{f" + (pageNumber - 1) + "}" + "\\clearpage "

                var lineToChange = lines[texLinesPDFPageLastSentence[pageNumber - 2]]
                var lineWithReference = ""

                var stylingLength = getStylingLength(
                    lineToChange,
                    pdfPageLastSentences,
                    pageNumber
                ) // styling shifted pdf-line's location in tex; account for this.

                lineWithReference = lineToChange.substring(
                    0,
                    texLinesLastSentenceIndex[pageNumber - 2] + pdfPageLastSentences[pageNumber - 2].length + stylingLength
                ) +
                        footerCallText +
                        lineToChange.substring(
                            (texLinesLastSentenceIndex[pageNumber - 2] + pdfPageLastSentences[pageNumber - 2].length + stylingLength),
                            lineToChange.length
                        )

                lines[texLinesPDFPageLastSentence[pageNumber - 2]] = lineWithReference
                pageNumber++
            }
            Files.write(texPath, lines, StandardCharsets.UTF_8)
            addFooterContentSection(outputStoryFilename, footers) // footer references need a list of footer contents
        }

        fun getStylingLength(lineToChange: String, pdfPageLastSentences: ArrayList<String>, pageNumber: Int): Int {
            var stylingLength = 0
            var stylingRegex = """\\uline\{[a-zA-Z\d]+\}|\\text(super|sub)script\{[0-9]+\.[0-9]+\}"""
            var lineToChangeLocal = lineToChange

            while (!(lineToChangeLocal.contains(pdfPageLastSentences[pageNumber - 2]))) {
                stylingLength += lineToChangeLocal.length
                lineToChangeLocal = lineToChangeLocal.replaceFirst(stylingRegex.toRegex(), "")
                stylingLength -= lineToChangeLocal.length
            }
            return stylingLength
        }

        fun generateFooters(
            vocabComponentArray: ArrayList<ArrayList<String>>,
            pageNumber: Int,
            languageMarker: String,
            footers: Footers
        ) {
            // generate a footer for a given pageNumber
            var pagesVocab: ArrayList<ArrayList<String>> = ArrayList<ArrayList<String>>()
            var vocabInFooterIndex = 0
            var leftFooter = StringBuilder()
            var rightFooter = StringBuilder()

            // store any vocabulary that's on this page, from all the vocab.
            vocabComponentArray.forEachIndexed { index, currentVocab ->
                if (Integer.parseInt(currentVocab[currentVocab.size - 2]) == pageNumber) { // last component of CurrentVocab is the index, 2nd from last is page number.
                    pagesVocab.add(currentVocab)
                }
            }

            // two "if {while/while}" sections deal out vocab to left/right footers
            if ((pagesVocab.size % 2) == 0) {    // left/right footers have 50/50 vocab
                while (vocabInFooterIndex < (pagesVocab.size / 2)) { // left (even)
                    appendFooterParts(leftFooter, pagesVocab, vocabInFooterIndex, languageMarker)
                    vocabInFooterIndex++
                }
                while (vocabInFooterIndex < (pagesVocab.size)) {  // right (even)
                    appendFooterParts(rightFooter, pagesVocab, vocabInFooterIndex, languageMarker)
                    vocabInFooterIndex++
                }
            } else {  // left footer has an extra vocab
                while (vocabInFooterIndex < (((pagesVocab.size) + 1) / 2)) { // left (odd)
                    appendFooterParts(leftFooter, pagesVocab, vocabInFooterIndex, languageMarker)
                    vocabInFooterIndex++
                }
                while (vocabInFooterIndex < (pagesVocab.size)) { // right (odd)
                    appendFooterParts(rightFooter, pagesVocab, vocabInFooterIndex, languageMarker)
                    vocabInFooterIndex++
                }
            }
            leftFooter.append("}")
            rightFooter.append("}")
            footers.lfoots.add(leftFooter.toString())
            footers.rfoots.add(rightFooter.toString())
        }

        // check how many parts are provided for vocabulary, then pass to appendFooter2/3+Parts
        fun appendFooterParts(
            footerToBuild: StringBuilder,
            pagesVocab: ArrayList<ArrayList<String>>,
            vocabInFooterIndex: Int,
            languageMarker: String
        ) {
            if (pagesVocab[0].size == 4) {
                appendFooterTwoPartsHanEn(footerToBuild, pagesVocab, vocabInFooterIndex, languageMarker)
            } else if (pagesVocab[0].size == 5) {
                appendFooterThreePartsHanPinEn(footerToBuild, pagesVocab, vocabInFooterIndex, languageMarker)
            }
        }

        // First add the vocabulary index, then e.g. hanzi then english
        fun appendFooterTwoPartsHanEn(
            footerToBuild: StringBuilder,
            pagesVocab: ArrayList<ArrayList<String>>,
            vocabInFooterIndex: Int,
            languageMarker: String
        ) {
            footerToBuild.append(Integer.parseInt(pagesVocab[vocabInFooterIndex][pagesVocab[0].size - 1]) + 1)
                .append(". ").append(pagesVocab[vocabInFooterIndex][0]).append(" ")
                .append(pagesVocab[vocabInFooterIndex][1]).append("\\\\ ")
        }

        // First add the vocabulary index, then e.g. hanzi (0), then pinyin (1) then english (2)
        fun appendFooterThreePartsHanPinEn(
            footerToBuild: StringBuilder,
            pagesVocab: ArrayList<ArrayList<String>>,
            vocabInFooterIndex: Int,
            languageMarker: String
        ) {
            footerToBuild.append(Integer.parseInt(pagesVocab[vocabInFooterIndex][pagesVocab[0].size - 1]) + 1)
                .append(". ").append(pagesVocab[vocabInFooterIndex][0]).append(" ").append(languageMarker)
                .append(pagesVocab[vocabInFooterIndex][1]).append("}) ").append(pagesVocab[vocabInFooterIndex][2])
                .append("\\\\ ")
        }

        fun addFooterContentSection(outputStoryFilename: String, footers: Footers) {
            // do this with Files.readAllLines, or scanning line-by-line?
            val texPath = Paths.get(outputStoryFilename)
            val lines = Files.readAllLines(texPath, StandardCharsets.UTF_8)
            var beginIndex =
                lines.indexOf("% Begin Document") - 1  // so we can place this all before the document begins
            var footersAddedIndex = 0
            var totalLinesAdded = 0
            var linesAdded = 0

            while (footersAddedIndex < footers.lfoots.size) {
                if (footersAddedIndex == 0) { // first footer contents is a bit special
                    var linesAdded = 0
                    var footerContentsStore = ArrayList<String>()
                    footerContentsStore.addAll(
                        Arrays.asList(
                            "% % Footer 1 % %)",
                            "\\fancypagestyle{f1}{",
                            "\\fancyhf{}", "\\renewcommand{\\headrulewidth}{0pt}",
                            "\\cfoot{\\thepage}",
                            "\\lfoot{" + footers.lfoots[footersAddedIndex],
                            "\\rfoot{" + footers.rfoots[footersAddedIndex],
                            "}"
                        )
                    )
                    while (linesAdded < footerContentsStore.size) { // add each of the stored lines to tex
                        lines.add(beginIndex + totalLinesAdded, footerContentsStore[linesAdded])
                        totalLinesAdded++
                        linesAdded++
                    }
                    footersAddedIndex++ // one footer contents (for page 1) has been added.
                }
                if (footersAddedIndex != 0) {
                    var linesAdded = 0
                    var footerContentsStore = ArrayList<String>()
                    footerContentsStore.addAll(
                        Arrays.asList(
                            "% % Footer " + (footersAddedIndex + 1) + "% %",
                            "\\fancypagestyle{f" + (footersAddedIndex + 1) + "}{",
                            "\\fancyhf{}",
                            "\\cfoot{\\thepage}",
                            "\\lfoot{" + footers.lfoots[footersAddedIndex],
                            "\\rfoot{" + footers.rfoots[footersAddedIndex],
                            "}"
                        )
                    )
                    while (linesAdded < footerContentsStore.size) {
                        lines.add(beginIndex + totalLinesAdded, footerContentsStore[linesAdded])
                        totalLinesAdded++
                        linesAdded++
                    }
                    footersAddedIndex++
                }
            }
            Files.write(texPath, lines, StandardCharsets.UTF_8)
        }
    }

    data class Footers(var lfoots: ArrayList<String> = ArrayList(), var rfoots: ArrayList<String> = ArrayList())
}