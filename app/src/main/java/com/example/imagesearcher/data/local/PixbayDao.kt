package com.example.imagesearcher.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single

@Dao
interface PixbayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PixbayDBItem)

    @Delete
    suspend fun deletePhoto(photo: PixbayDBItem)

    @Query("SELECT * FROM photo_items")
    fun observeAllPhotos(): LiveData<List<PixbayDBItem>>

    @Query("SELECT * FROM photo_items WHERE id = :id")
    fun containsItem(id: Int): Single<PixbayDBItem>

}