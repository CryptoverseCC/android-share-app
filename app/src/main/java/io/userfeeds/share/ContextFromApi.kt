package io.userfeeds.share

data class ContextFromApi(
        val hashtag: String,
        val images: ImagesFromApi
)

data class ImagesFromApi(val avatar: String)
