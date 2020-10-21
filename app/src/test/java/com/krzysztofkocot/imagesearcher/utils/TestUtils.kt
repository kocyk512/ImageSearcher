package com.krzysztofkocot.imagesearcher.utils

import com.krzysztofkocot.imagesearcher.data.local.PixbayDBItem
import com.krzysztofkocot.imagesearcher.data.remote.PixbayPhoto


fun PixbayPhoto.toDbItemTest() = PixbayDBItem(
    id,
    webformatURL,
    tags,
    user,
    userImageURL
)

val defaultPhoto = PixbayPhoto(
    0, 0, 0, 0, 0, 0, 0, "",
    0, "", 0, "", 0, "", "", "", 0,
    "", 0, 0, "", 0
)

val domainPhoto = defaultPhoto.copy(
    id = 1,
    user = "user",
    webformatURL = "url",
    userImageURL = "userUrl",
    tags = "tags"
)