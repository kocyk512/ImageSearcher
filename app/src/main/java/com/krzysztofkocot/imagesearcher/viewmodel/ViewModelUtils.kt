package com.krzysztofkocot.imagesearcher.viewmodel

fun String.searchQueryError(): Boolean  {
    return !(PATTERN matches this)
}

private val PATTERN = Regex("[a-zA-Z]*")