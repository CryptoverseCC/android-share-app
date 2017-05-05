package io.userfeeds.share

import android.app.Application
import android.util.Log
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ShareApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val retrofit = Retrofit.Builder()
                .baseUrl("https://beta.userfeeds.io/api/")
                .client(OkHttpClient.Builder()
                        .apply { if (BuildConfig.DEBUG) addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)) }
                        .build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
        val api = retrofit.create(ContextsApi::class.java)
        api.call().subscribe(this::onContexts, this::onError)
    }

    private fun onContexts(contexts: Map<String, ContextFromApi>) {
        println(contexts)
    }

    private fun onError(error: Throwable) {
        Log.e("ShareApp", "error", error)
    }
}
