package com.krzysztofkocot.imagesearcher.api

import com.krzysztofkocot.imagesearcher.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface PixbayApi {

    companion object {
        const val BASE_URL = BuildConfig.PIXABAY_BASE_URL
        const val API_KEY = BuildConfig.PIXABAY_API_KEY
    }

    @GET("api")
    suspend fun searchPhotos(
        @Query("key") key: String = API_KEY,
        @Query("q") q: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("image_type") imageType: String = "photo"
    ): PixBayResponse
}