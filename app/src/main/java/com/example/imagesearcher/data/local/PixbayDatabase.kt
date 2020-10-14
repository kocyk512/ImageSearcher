package com.example.imagesearcher.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PixbayDBItem::class],
    version = 1
)
abstract class PixbayDatabase: RoomDatabase() {

    abstract fun pixbayDao(): PixbayDao
}