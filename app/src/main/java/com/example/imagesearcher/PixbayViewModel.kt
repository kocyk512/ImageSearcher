package com.example.imagesearcher

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.imagesearcher.data.PixbayRepository

class PixbayViewModel @ViewModelInject constructor(
    private val repository: PixbayRepository
) : ViewModel() {

    private val currentQuery = MutableLiveData(DEFAULT_QUERY)

    val photos = currentQuery.switchMap { queryString ->
        repository.searchPhoto(queryString).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }

    val footerHeaderRefreshClickS = MutableLiveData<Unit>()
    val retryClickS = MutableLiveData<Unit>()

    companion object {
        private const val DEFAULT_QUERY = "cats"
    }
}