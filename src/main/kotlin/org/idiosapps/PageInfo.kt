package org.idiosapps

class PageInfo constructor(
    val pageNumber: Int,
    val pdfPageLastSentence: String,
    var texLinesOfPDFPagesLastSentence: Int? = null, // can be null '.' gets set later
    var texLineIndexOfPDFPageLastSentence: Int? = null // can be null '.' gets set later
)