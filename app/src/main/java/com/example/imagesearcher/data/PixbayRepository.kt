package com.example.imagesearcher.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.imagesearcher.api.PixbayApi
import javax.inject.Inject

class PixbayRepository @Inject constructor(private val pixbayApi: PixbayApi) {

    fun searchPhoto(query: String) = Pager(
        PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { PixbayPagingSource(pixbayApi, query) }
    ).liveData
}

