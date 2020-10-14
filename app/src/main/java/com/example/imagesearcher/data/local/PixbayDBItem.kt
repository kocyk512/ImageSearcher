package com.example.imagesearcher.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_items")
data class PixbayDBItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    var webFormatUrl: String,
    var tags: String,
    var user: String,
    var userImageUrl: String
)