package com.krzysztofkocot.imagesearcher.data.remote

import com.google.common.truth.Truth.assertThat
import com.krzysztofkocot.imagesearcher.data.local.PixbayDBItem
import com.krzysztofkocot.imagesearcher.utils.defaultPhoto
import org.junit.Test

class PixbayPhotoKtTest {

    @Test
    fun `Given PixbayPhoto the proper description is generated`() {
        val description = defaultPhoto.toDescription()
        val expectedDescription =
            """comments: 0
downloads: 0
favorites: 0
id: 0
imageHeight: 0
imageSize: 0
imageWidth: 0
likes: 0
previewHeight: 0
previewWidth: 0
tags: 
type: 
user: 
userId: 0
views: 0
webformatHeight: 0
webformatWidth: 0
"""
        assertThat(description).isEqualTo(expectedDescription)
    }

    @Test
    fun `Given Pixbay proper PixbayDBItem is generated`() {
        val dbItem = defaultPhoto.toDbItem()
        val expectedDbItem = defaultPhoto.let {
            PixbayDBItem(
                it.id,
                it.webformatURL,
                it.tags,
                it.user,
                it.userImageURL
            )
        }

        assertThat(dbItem).isEqualTo(expectedDbItem)
    }
}