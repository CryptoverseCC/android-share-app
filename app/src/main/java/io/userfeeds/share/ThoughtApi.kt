package io.userfeeds.share

import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.POST

interface ThoughtApi {

    @POST("storage")
    fun call(@Body body: ThoughtDto): Completable
}

data class ThoughtDto(
        val context: String,
        val type: List<String>,
        val claim: Claim,
        val credits: List<Credit>,
        val signature: Signature
)

data class Claim(val target: String)

data class Credit(val type: String, val value: String)

data class Signature(
        val type: String
)
