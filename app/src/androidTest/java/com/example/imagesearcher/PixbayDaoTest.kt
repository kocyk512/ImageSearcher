package com.example.imagesearcher

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.imagesearcher.data.local.PixbayDBItem
import com.example.imagesearcher.data.local.PixbayDao
import com.example.imagesearcher.data.local.PixbayDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class PixbayDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: PixbayDatabase
    private lateinit var dao: PixbayDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PixbayDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.pixbayDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertDBItem() = runBlockingTest {
        val photoItem = PixbayDBItem(1, "url", "tags", "user", "userImageUrl")
        dao.insertPhoto(photoItem)

        val allPhotos = dao.observeAllPhotos().getOrAwaitValue()

        assertThat(allPhotos).contains(photoItem)
    }

    @Test
    fun deleteDBItem() = runBlockingTest {
        val photoItem = PixbayDBItem(1, "url", "tags", "user", "userImageUrl")
        dao.insertPhoto(photoItem)
        dao.deletePhoto(photoItem)

        val allPhotos = dao.observeAllPhotos().getOrAwaitValue()

        assertThat(allPhotos).doesNotContain(photoItem)
    }

    @Test
    fun observeAllDBItems() = runBlockingTest {
        val photoItem = PixbayDBItem(1, "url", "tags", "user", "userImageUrl")
        val photoItem2 = PixbayDBItem(2, "url2", "tags2", "user2", "userImageUrl2")
        val photoItem3 = PixbayDBItem(3, "url3", "tags3", "user3", "userImageUrl3")

        val items = listOf(photoItem, photoItem2, photoItem3)

        items.forEach { dao.insertPhoto(it) }

        val allPhotos = dao.observeAllPhotos().getOrAwaitValue()

        assertThat(allPhotos).containsExactly(photoItem, photoItem2, photoItem3)
    }
}