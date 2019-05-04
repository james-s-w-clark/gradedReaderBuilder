package org.idiosapps

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class FooterUtils {
    companion object {
        lateinit var pageInfo: PageInfo
        private var pageFooter = ArrayList<Footers>()

        fun addVocabFooters(
            pagesInfo: MutableList<PageInfo>,
            vocab: MutableList<Vocab>,
            languageUsed: String
        ) {
            VocabUtils.getOrderIndicies(vocab) // add vocab "order of appearance" index // todo auto-find correct order

            var languageMarker = LanguageUtils.getMarker(languageUsed)
            val texPath = Paths.get(Filenames.outputTexFilename)
            val lines = Files.readAllLines(texPath, StandardCharsets.UTF_8)

            for (pageNumber in 0 until PDFUtils.getNumberOfPDFPages() - 2) {// -1 for title page, -1 for size
                pageInfo = pagesInfo[pageNumber]
                pageFooter.add(generatePageFooters(languageMarker, vocab, pageNumber))

                var lineToChange =
                    lines[pageInfo.texLinesOfPDFPagesLastSentence!!]

                var stylingLength = getStylingLength(
                    lineToChange
                ) // styling shifted pdf-line's location in tex; accounts for this.

                var footerCallText: String =
                    " \\thispagestyle{f" + (pageNumber + 1) + "}" + "\\clearpage " // + 1 '.' title page

                var lineWithReference = addFooterCallToTexLine(lineToChange, pageInfo, stylingLength, footerCallText)

                lines[pageInfo.texLinesOfPDFPagesLastSentence!!] = lineWithReference
            }
            Files.write(texPath, lines, StandardCharsets.UTF_8)
            addFooterSections(Filenames.outputTexFilename)
        }

        // slide the text that calls the footer content into the part of TeX where the end of the PDF page is
        private fun addFooterCallToTexLine(
            lineToChange: String,
            pageInfo: PageInfo,
            stylingLength: Int,
            footerCallText: String
        ): String {
            return lineToChange.substring(
                0,
                pageInfo.texLineIndexOfPDFPageLastSentence!! + pageInfo.pdfPageLastSentence.length + stylingLength
            ) +
                    footerCallText +
                    lineToChange.substring(
                        (pageInfo.texLineIndexOfPDFPageLastSentence!! + pageInfo.pdfPageLastSentence.length + stylingLength),
                        lineToChange.length
                    )
        }

        private fun getStylingLength(lineToChange: String): Int {
            var stylingLength = 0
            var stylingRegex = """\\uline\{[a-zA-Z\d]+\}|\\text(super|sub)script\{[0-9]+\.[0-9]+\}"""
            var lineToChangeLocal = lineToChange


            while (!(lineToChangeLocal.contains(pageInfo.pdfPageLastSentence))) {
                stylingLength += lineToChangeLocal.length
                lineToChangeLocal = lineToChangeLocal.replaceFirst(stylingRegex.toRegex(), "")
                stylingLength -= lineToChangeLocal.length
            }
            return stylingLength
        }

        private fun generatePageFooters(
            languageMarker: String,
            vocab: MutableList<Vocab>,
            pageNumber: Int
        ): Footers {
            // generate a footer for a given pageNumber
            var leftFooter = StringBuilder()
            var rightFooter = StringBuilder()

            // only work with the vocabulary that's on this page
            var pagesVocab: MutableList<Vocab> = ArrayList()
            vocab.forEach { vocabItem ->
                if (vocabItem.firstOccurencePage == pageNumber + 2)
                    pagesVocab.add(vocabItem)
            }

            var doLeftFooter = true
            pagesVocab.forEach { vocabItem ->
                val vocabFooter = "${vocabItem.firstOccurencePage!! - 1}.${vocabItem.vocabOrderIndex!! + 1} " +
                        "${vocabItem.L2Word} ${LanguageUtils.getMarkedL2Extra(vocabItem)} - " +
                        "${vocabItem.L1Word}${SummaryPageWriter.endLine}"
                when (doLeftFooter) {
                    true -> {
                        leftFooter.append(vocabFooter); doLeftFooter = !doLeftFooter
                    }
                    false -> {
                        rightFooter.append(vocabFooter); doLeftFooter = !doLeftFooter
                    }
                }
            }

            leftFooter.append("}")
            rightFooter.append("}")

            return Footers(leftFooter.toString(), rightFooter.toString())
        }

        // Page-specific footerStyles get called at the end of every page - make the contents here.
        private fun makeFooterSection(pageNumber: Int): List<String> {
            val title = "% % Footer " + (pageNumber + 1) + " % %"
            val pageStyle = "\\fancypagestyle{f" + (pageNumber + 1) + "}{"
            val fancyhf = "\\fancyhf{}"
            val position = "\\cfoot{\\thepage}"
            val leftFooter = "\\lfoot{" + pageFooter[pageNumber].leftFooter
            val rightFooter = "\\rfoot{" + pageFooter[pageNumber].rightFooter

            var renewCommand = if (pageNumber == 0) "\\renewcommand{\\headrulewidth}{0pt}" else null

            return listOfNotNull(
                title,
                pageStyle,
                fancyhf,
                renewCommand,
                position,
                leftFooter,
                rightFooter,
                "}" // have to close off section start from pageStyle
            )
        }

        private fun addFooterSections(outputStoryFilename: String) {
            val texPath = Paths.get(outputStoryFilename)
            val lines = Files.readAllLines(texPath, StandardCharsets.UTF_8)
            var beginIndex =
                lines.indexOf("% Begin Document") - 1  // so we can place this all before the document begins
            var totalLinesAdded = 0

            for (pageNumber in 0 until PDFUtils.getNumberOfPDFPages() - 2) {
                val footerContents = makeFooterSection(pageNumber)

                footerContents.forEach { footerPart ->
                    lines.add(beginIndex + totalLinesAdded, footerPart)
                    totalLinesAdded++
                }
            }
            Files.write(texPath, lines, StandardCharsets.UTF_8)
        }
    }

    data class Footers(var leftFooter: String, var rightFooter: String)
}