package com.example.imagesearcher.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.imagesearcher.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class PixbayDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    @Named("test_db")
    lateinit var database: PixbayDatabase
    private lateinit var dao: PixbayDao

    @Before
    fun setup() {
        hiltAndroidRule.inject()
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

    @Test
    fun containsItem() = runBlockingTest {
        val photoItem = PixbayDBItem(1, "url", "tags", "user", "userImageUrl")
        dao.insertPhoto(photoItem)

        dao.containsItem(photoItem.id!!)
            .test()
            .assertValue(photoItem)
    }
}