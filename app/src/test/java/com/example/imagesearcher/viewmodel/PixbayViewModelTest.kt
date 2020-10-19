package com.example.imagesearcher.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.imagesearcher.MainCoroutineRule
import com.example.imagesearcher.data.local.FakePixbayRepository
import com.example.imagesearcher.getOrAwaitValueTest
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
    fun `On search query to short, proper error status is set`() {
        val query = "ca"
        viewModel.searchPhotos(query)

        val value = viewModel.queryStatus.getOrAwaitValueTest()

        assertThat(value.status).isEqualTo(QueryStatus.ErrorToShort)
    }


    @Test
    fun `On search query with number, proper error status is set`() {
        val query = "cat9"
        viewModel.searchPhotos(query)

        val value = viewModel.queryStatus.getOrAwaitValueTest()

        assertThat(value.status).isEqualTo(QueryStatus.ErrorFormat)
    }

    @Test
    fun `On valid search query with minimal length, query status is Valid`() {
        val query = "cat"
        viewModel.searchPhotos(query)

        val value = viewModel.queryStatus.getOrAwaitValueTest()

        assertThat(value.status).isEqualTo(QueryStatus.Valid)
    }
}
