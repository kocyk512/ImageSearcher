package com.example.imagesearcher.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.example.imagesearcher.data.local.PixbayDBItem
import com.example.imagesearcher.data.remote.PixbayPhoto

interface RepositoryContract {

    fun searchPhoto(query: String): LiveData<PagingData<PixbayPhoto>>

    suspend fun insertPhoto(photo: PixbayDBItem)

    suspend fun deletePhoto(photo: PixbayDBItem)

    fun observeAllPhotos(): LiveData<List<PixbayDBItem>>
}