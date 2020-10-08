package com.example.imagesearcher.data

import com.example.imagesearcher.api.PixbayApi
import javax.inject.Inject

class PixbayRepository @Inject constructor(private val pixbayApi: PixbayApi) {

    suspend fun searchPhoto(query: String) = pixbayApi.searchPhotos(q = query, page = 1, perPage = 20 )
}

