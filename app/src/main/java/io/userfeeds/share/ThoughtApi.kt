package io.userfeeds.share

import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.POST

interface ThoughtApi {

    @POST("storage")
    fun call(@Body body: ThoughtDto): Completable
}
