package com.example.imagesearcher.api

import com.example.imagesearcher.data.remote.PixbayPhoto

data class PixBayResponse(
    val hits: List<PixbayPhoto>
)
