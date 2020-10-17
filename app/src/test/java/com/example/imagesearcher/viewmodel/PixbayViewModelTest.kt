package com.example.imagesearcher.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.imagesearcher.MainCoroutineRule
import com.example.imagesearcher.data.local.FakePixbayRepository
import com.example.imagesearcher.getOrAwaitValueTest
import com.example.imagesearcher.utils.domainPhoto
import com.example.imagesearcher.utils.toDbItemTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PixbayViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: PixbayViewModel

    @Before
    fun setUp() {
        viewModel = PixbayViewModel(
            FakePixbayRepository(),
            SavedStateHandle()
        )
    }

    @Test
    fun `given database, when item inserted, then database contain item`() {
        viewModel.insertPhoto(domainPhoto)
        val value = viewModel.allFavouritesPhoto().getOrAwaitValueTest()

        assertThat(value).contains(domainPhoto.toDbItemTest())
    }

    @Test
    fun `given database, when favourite button on item clicked, then database contains item`() {
        viewModel.favouriteClick(domainPhoto)
        val value = viewModel.allFavouritesPhoto().getOrAwaitValueTest()

        assertThat(value).contains(domainPhoto.toDbItemTest())
    }
}
