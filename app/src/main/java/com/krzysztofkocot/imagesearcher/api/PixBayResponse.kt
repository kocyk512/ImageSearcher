package com.krzysztofkocot.imagesearcher.api

import com.krzysztofkocot.imagesearcher.data.remote.PixbayPhoto

data class PixBayResponse(
    val hits: List<PixbayPhoto>
)
