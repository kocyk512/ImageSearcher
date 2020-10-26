package com.krzysztofkocot.imagesearcher.api

import retrofit2.http.GET
import retrofit2.http.Query

interface PixbayApi {

    companion object {
        val BASE_URL = "https://pixabay.com/"
        val API_KEY = "18609325-32275d7fbcd73dbcbafcc0731"
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