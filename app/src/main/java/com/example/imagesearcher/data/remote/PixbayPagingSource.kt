package com.example.imagesearcher.data.remote

import androidx.paging.PagingSource
import com.example.imagesearcher.api.PixbayApi
import java.io.IOException

class PixbayPagingSource(
    val pixbayApi: PixbayApi,
    val query: String
) : PagingSource<Int, PixbayPhoto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PixbayPhoto> {

        val position = params.key ?: STARTING_PAGE_INDEX
        return try {
            val photos = pixbayApi.searchPhotos(
                q = query,
                page = position,
                perPage = params.loadSize
            ).hits

            LoadResult.Page(
                photos,
                if (position == STARTING_PAGE_INDEX) null else position - 1,
                if (photos.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: RuntimeException) {
            LoadResult.Error(exception)
        }
    }
}

private const val STARTING_PAGE_INDEX = 1