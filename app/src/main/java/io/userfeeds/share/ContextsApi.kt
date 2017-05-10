package io.userfeeds.share

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET

interface ContextsApi {

    @GET("contexts")
    fun call(): Single<ResponseBody>
}
