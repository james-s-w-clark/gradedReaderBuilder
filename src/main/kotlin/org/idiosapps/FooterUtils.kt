package org.idiosapps

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayList

class FooterUtils {
    companion object {
        fun addVocabFooters(
            pagesInfo: MutableList<PageInfo>,
            vocab: MutableList<Vocab>,
            languageUsed: String
            ) {
            var pageNumber = 0 // WAS 2 '.' size + title offset
            var footers = Footers()

            VocabUtils.getOrderIndicies(vocab) // add vocab "order of appearance" index // todo create this automatically.

            var languageMarker = LanguageUtils.getMarker(languageUsed)
            val texPath = Paths.get(Filenames.outputTexFilename)
            val lines = Files.readAllLines(texPath, StandardCharsets.UTF_8)

            while (pageNumber < PDFUtils.getNumberOfPDFPages() - 2) { // -1 for size, -1 for title page
                val pageInfo = pagesInfo[pageNumber]

                generateFooters(pageNumber, languageMarker, vocab, footers)

                var footerCallText: String = " \\thispagestyle{f" + (pageNumber + 1) + "}" + "\\clearpage " // + 1 '.' title page

                var lineToChange =
                    lines[pageInfo.texLinesOfPDFPagesLastSentence!!] // texLinesPDFPageLastSentence[pageNumber - 2]
                var lineWithReference: String

                var stylingLength = getStylingLength(
                    lineToChange,
                    pageNumber,
                    pagesInfo
                ) // styling shifted pdf-line's location in tex; account for this.

                lineWithReference = lineToChange.substring(
                    0,
                    pageInfo.texLineIndexOfPDFPageLastSentence!! + pageInfo.pdfPageLastSentence.length + stylingLength
                ) +
                footerCallText +
                lineToChange.substring(
                    (pageInfo.texLineIndexOfPDFPageLastSentence!! + pageInfo.pdfPageLastSentence.length + stylingLength),
                    lineToChange.length
                )

                lines[pageInfo.texLinesOfPDFPagesLastSentence!!] = lineWithReference
                pageNumber++
            }
            Files.write(texPath, lines, StandardCharsets.UTF_8)
            addFooterContentSection(
                Filenames.outputTexFilename,
                footers
            ) // footer references need a list of footer contents
        }

        fun getStylingLength(lineToChange: String, pageNumber: Int, pageInfos: MutableList<PageInfo>): Int {
            var stylingLength = 0
            var stylingRegex = """\\uline\{[a-zA-Z\d]+\}|\\text(super|sub)script\{[0-9]+\.[0-9]+\}"""
            var lineToChangeLocal = lineToChange


            while (!(lineToChangeLocal.contains(pageInfos[pageNumber].pdfPageLastSentence))) {
                stylingLength += lineToChangeLocal.length
                lineToChangeLocal = lineToChangeLocal.replaceFirst(stylingRegex.toRegex(), "")
                stylingLength -= lineToChangeLocal.length
            }
            return stylingLength
        }

        fun generateFooters(
            pageNumber: Int,
            languageMarker: String,
            vocab: MutableList<Vocab>,
            footers: Footers
        ) {
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
                    true -> {leftFooter.append(vocabFooter); doLeftFooter = !doLeftFooter }
                    false -> {rightFooter.append(vocabFooter); doLeftFooter = !doLeftFooter}
                }
            }
            leftFooter.append("}")
            rightFooter.append("}")
            footers.lfoots.add(leftFooter.toString())
            footers.rfoots.add(rightFooter.toString())
        }

        fun addFooterContentSection(outputStoryFilename: String, footers: Footers) {
            // do this with Files.readAllLines, or scanning line-by-line?
            val texPath = Paths.get(outputStoryFilename)
            val lines = Files.readAllLines(texPath, StandardCharsets.UTF_8)
            var beginIndex =
                lines.indexOf("% Begin Document") - 1  // so we can place this all before the document begins
            var footersAddedIndex = 0
            var totalLinesAdded = 0

            while (footersAddedIndex < footers.lfoots.size) {
                if (footersAddedIndex == 0) { // first footer contents is a bit special - \\renewcommand
                    var footerContentsStore = ArrayList<String>()
                    footerContentsStore.addAll( // create page-footer styling, which gets called
                        Arrays.asList(
                            "% % Footer 1 % %)",
                            "\\fancypagestyle{f1}{", // special case with renewcommand below - for {f1} only
                            "\\fancyhf{}", "\\renewcommand{\\headrulewidth}{0pt}",
                            "\\cfoot{\\thepage}",
                            "\\lfoot{" + footers.lfoots[footersAddedIndex],
                            "\\rfoot{" + footers.rfoots[footersAddedIndex],
                            "}"
                        )
                    )
                    footerContentsStore.forEach {footerPart ->
                        lines.add(beginIndex + totalLinesAdded, footerPart)
                        totalLinesAdded++
                    }
                    footersAddedIndex++ // one footer contents (for page 1) has been added.
                }
                if (footersAddedIndex != 0) { // we've dealt with our special case
                    var footerContentsStore = ArrayList<String>()
                    footerContentsStore.addAll(
                        Arrays.asList( // create page styles
                            "% % Footer " + (footersAddedIndex + 1) + "% %",
                            "\\fancypagestyle{f" + (footersAddedIndex + 1) + "}{",
                            "\\fancyhf{}",
                            "\\cfoot{\\thepage}",
                            "\\lfoot{" + footers.lfoots[footersAddedIndex],
                            "\\rfoot{" + footers.rfoots[footersAddedIndex],
                            "}"
                        )
                    )
                    footerContentsStore.forEach { footerPart -> // add page-footer styles to TeX
                        lines.add(beginIndex + totalLinesAdded, footerPart)
                        totalLinesAdded++
                    }
                    footersAddedIndex++
                }
            }
            Files.write(texPath, lines, StandardCharsets.UTF_8)
        }
    }

    data class Footers(var lfoots: ArrayList<String> = ArrayList(), var rfoots: ArrayList<String> = ArrayList())
}