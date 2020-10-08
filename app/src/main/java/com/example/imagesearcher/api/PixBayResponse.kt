package com.example.imagesearcher.api

import com.example.imagesearcher.data.PixbayPhoto

data class PixBayResponse(
    val hits: List<PixbayPhoto>
)
