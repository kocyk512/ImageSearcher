package com.example.imagesearcher

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.imagesearcher.data.PixbayRepository
import kotlinx.coroutines.Dispatchers

class PixbayViewModel @ViewModelInject constructor(
    private val repository: PixbayRepository
) : ViewModel() {

    fun searchPhoto(query: String) = liveData(Dispatchers.IO) {
        val response = repository.searchPhoto(query)
        emit(response)
    }
}