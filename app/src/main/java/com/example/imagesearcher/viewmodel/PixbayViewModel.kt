package com.example.imagesearcher.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.imagesearcher.data.PixbayRepository
import com.example.imagesearcher.data.remote.PixbayPhoto
import com.example.imagesearcher.data.remote.toDbItem
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class PixbayViewModel @ViewModelInject constructor(
    private val repository: PixbayRepository,
    @Assisted state: SavedStateHandle,
) : ViewModel() {

    private val disposable = CompositeDisposable()

    private val currentQuery = state.getLiveData(
        CURRENT_QUERY,
        DEFAULT_QUERY
    )

    val photos = currentQuery.switchMap { queryString ->
        repository.searchPhoto(queryString).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }

    val footerHeaderRefreshClickS = MutableLiveData<Unit>()
    val retryClickS = MutableLiveData<Unit>()

    fun insertPhoto(photo: PixbayPhoto) {
        viewModelScope.launch { repository.insertPhoto(photo.toDbItem()) }
    }

    fun deletePhoto(photo: PixbayPhoto) {
        viewModelScope.launch { repository.deletePhoto(photo.toDbItem()) }
    }

    fun allFavouritesPhoto() = repository.observeAllPhotos()

    fun favouriteClick(photo: PixbayPhoto) {
        disposable.add(
            repository.containsItem(photo.id)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    deletePhoto(photo)
                }, {
                    insertPhoto(photo)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    companion object {
        private const val DEFAULT_QUERY = "cats"
        private const val CURRENT_QUERY = "CURRENT_QUERY"
    }
}