package com.example.imagesearcher.di

import android.content.Context
import androidx.room.Room
import com.example.imagesearcher.DATABASE_NAME
import com.example.imagesearcher.api.PixbayApi
import com.example.imagesearcher.data.PixbayRepository
import com.example.imagesearcher.data.RepositoryContract
import com.example.imagesearcher.data.local.PixbayDao
import com.example.imagesearcher.data.local.PixbayDatabase
import com.example.imagesearcher.ui.gallery.PixbayPhotoAdapter
import com.example.imagesearcher.ui.favourites.FavouritesAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(PixbayApi.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun providePixbayApi(retrofit: Retrofit): PixbayApi =
        retrofit.create(PixbayApi::class.java)

    @Provides
    @Singleton
    fun providePixbayAdapter(): PixbayPhotoAdapter =
        PixbayPhotoAdapter()

    @Provides
    @Singleton
    fun provideFavouritesAdapter(): FavouritesAdapter = FavouritesAdapter()

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, PixbayDatabase::class.java, DATABASE_NAME)
        .build()

    @Singleton
    @Provides
    fun providePixbayDao(database: PixbayDatabase) = database.pixbayDao()

    @Singleton
    @Provides
    fun provideRepository(
        api: PixbayApi,
        dao: PixbayDao
    ) = PixbayRepository(api, dao) as RepositoryContract
}
