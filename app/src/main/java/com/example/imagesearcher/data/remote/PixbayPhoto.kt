package com.example.imagesearcher.data.remote


import android.os.Parcelable
import com.example.imagesearcher.data.local.PixbayDBItem
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PixbayPhoto(
    val comments: Int,
    val downloads: Int,
    val favorites: Int,
    val id: Int,
    val imageHeight: Int,
    val imageSize: Int,
    val imageWidth: Int,
    val largeImageURL: String,
    val likes: Int,
    val pageURL: String,
    val previewHeight: Int,
    val previewURL: String,
    val previewWidth: Int,
    val tags: String,
    val type: String,
    val user: String,
    @SerializedName("user_id")
    val userId: Int,
    val userImageURL: String,
    val views: Int,
    val webformatHeight: Int,
    val webformatURL: String,
    val webformatWidth: Int
) : Parcelable

fun PixbayPhoto.toDbItem() = PixbayDBItem(
    id,
    webformatURL,
    tags,
    user,
    userImageURL
)
