package com.example.imagesearcher.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.imagesearcher.data.RepositoryContract
import com.example.imagesearcher.data.remote.PixbayPhoto
import com.example.imagesearcher.data.remote.toDbItem
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class PixbayViewModel @ViewModelInject constructor(
    private val repository: RepositoryContract,
    @Assisted state: SavedStateHandle,
) : ViewModel() {

    private val disposable = CompositeDisposable()
    val footerHeaderRefreshClickS = MutableLiveData<Unit>()
    val retryClickS = MutableLiveData<Unit>()
    private val _queryStatus = MutableLiveData<QueryEvent>()
    val queryStatus: LiveData<QueryEvent> = _queryStatus

    private val currentQuery = state.getLiveData(
        CURRENT_QUERY,
        DEFAULT_QUERY
    )

    val photos = currentQuery.switchMap { queryString ->
        repository.searchPhoto(queryString).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        if (query.length < 3) {
            _queryStatus.postValue(QueryEvent(query, QueryStatus.ErrorToShort))
            return
        }
        if(query.searchQueryError()){
            _queryStatus.postValue(QueryEvent(query, QueryStatus.ErrorFormat))
            return
        }
        _queryStatus.postValue(QueryEvent(query, QueryStatus.Valid))
        currentQuery.value = query
    }

    private fun insertPhoto(photo: PixbayPhoto) {
        viewModelScope.launch { repository.insertPhoto(photo.toDbItem()) }
    }

    private fun deletePhoto(photo: PixbayPhoto) {
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