package com.krzysztofkocot.imagesearcher.di

import android.content.Context
import androidx.room.Room
import com.krzysztofkocot.imagesearcher.data.local.PixbayDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named

@Module
@InstallIn(ApplicationComponent::class)
object TestAppModule {

    @Provides
    @Named("test_db")
    fun provideInMemoryDB(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(context, PixbayDatabase::class.java)
            .allowMainThreadQueries()
            .build()
}