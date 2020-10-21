package com.krzysztofkocot.imagesearcher.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.krzysztofkocot.imagesearcher.data.local.PixbayDBItem
import com.krzysztofkocot.imagesearcher.data.remote.PixbayPhoto
import io.reactivex.Single

interface RepositoryContract {

    fun searchPhoto(query: String): LiveData<PagingData<PixbayPhoto>>

    suspend fun insertPhoto(photo: PixbayDBItem)

    suspend fun deletePhoto(photo: PixbayDBItem)

    fun observeAllPhotos(): LiveData<List<PixbayDBItem>>

    fun containsItem(id: Int): Single<PixbayDBItem>
}