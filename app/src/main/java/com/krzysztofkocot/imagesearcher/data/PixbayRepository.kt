package com.krzysztofkocot.imagesearcher.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.krzysztofkocot.imagesearcher.api.PixbayApi
import com.krzysztofkocot.imagesearcher.data.local.PixbayDBItem
import com.krzysztofkocot.imagesearcher.data.local.PixbayDao
import com.krzysztofkocot.imagesearcher.data.remote.PixbayPagingSource
import javax.inject.Inject

class PixbayRepository @Inject constructor(
    private val pixbayApi: PixbayApi,
    private val pixbayDao: PixbayDao
) : RepositoryContract {

    override fun searchPhoto(query: String) = Pager(
        PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            PixbayPagingSource(
                pixbayApi,
                query
            )
        }
    ).liveData

    override suspend fun insertPhoto(photo: PixbayDBItem) = pixbayDao.insertPhoto(photo)

    override suspend fun deletePhoto(photo: PixbayDBItem) = pixbayDao.deletePhoto(photo)

    override fun observeAllPhotos(): LiveData<List<PixbayDBItem>> = pixbayDao.observeAllPhotos()

    override fun containsItem(id: Int) = pixbayDao.containsItem(id)
}

