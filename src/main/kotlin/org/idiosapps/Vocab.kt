package org.idiosapps

class Vocab constructor(
    val L2Word: String,
    val L2Extra: String?,
    val L1Word: String,
    var firstOccurencePage: Int? = null, // can be null '.' gets set later
    var vocabOrderIndex: Int? = null // can be null '.' gets set later
)