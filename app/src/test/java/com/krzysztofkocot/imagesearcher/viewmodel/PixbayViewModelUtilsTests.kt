package com.krzysztofkocot.imagesearcher.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixbayViewModelUtilsTests {

    @Test
    fun `On valid search query validation pass`() {
        val query = "cat"
        val error = query.searchQueryError()

        assertThat(error).isFalse()
    }

    @Test
    fun `On search query with numbers validation doesn't pass`() {
        val query = "cat9"
        val error = query.searchQueryError()

        assertThat(error).isTrue()
    }

    @Test
    fun `On search 2 words validation pass`() {
        val query = "hot dog"
        val error = query.searchQueryError()

        assertThat(error).isFalse()
    }

    @Test
    fun `On search 3 words validation pass`() {
        val query = "two hot dogs"
        val error = query.searchQueryError()

        assertThat(error).isFalse()
    }
}
