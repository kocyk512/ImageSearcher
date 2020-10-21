package com.krzysztofkocot.imagesearcher.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.krzysztofkocot.imagesearcher.data.RepositoryContract
import com.krzysztofkocot.imagesearcher.data.remote.PixbayPhoto
import com.krzysztofkocot.imagesearcher.utils.defaultPhoto
import io.reactivex.Single

class FakePixbayRepository: RepositoryContract {

    private val allItems = mutableListOf<PixbayDBItem>()
    private val observableItems = MutableLiveData<List<PixbayDBItem>>()

    private fun refresh(){
        observableItems.postValue(allItems)
    }

    override fun searchPhoto(query: String): LiveData<PagingData<PixbayPhoto>> {
        return MutableLiveData(PagingData.from(listOf(defaultPhoto)))
    }

    override suspend fun insertPhoto(photo: PixbayDBItem) {
        allItems.add(photo)
        refresh()
    }

    override suspend fun deletePhoto(photo: PixbayDBItem) {
        allItems.remove(photo)
        refresh()
    }

    override fun observeAllPhotos(): LiveData<List<PixbayDBItem>> = observableItems


    override fun containsItem(id: Int): Single<PixbayDBItem> {
        val list = allItems.filter {
            it.id == id
        }
        return Single.just(
            list.first()
        )
    }
}