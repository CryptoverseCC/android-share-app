package io.userfeeds.share

import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.Moshi
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.userfeeds.common.mapAdapter
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.net.URL

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

    private fun onContexts(response: ResponseBody) {
        val contextsString = response.string()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit()
                .putString("contexts", contextsString)
                .apply()
        val moshi = Moshi.Builder().build()
        val adapter = moshi.mapAdapter<String, ContextFromApi>()
        val contexts = adapter.fromJson(contextsString)
        contexts.forEach { (_, context) ->
            Single.fromCallable { storeImageLocally(context) }
                    .subscribeOn(Schedulers.io())
                    .subscribe({ Log.i("ShareApp", "Loaded image. Size $it") }, this::onError)
        }
    }

    private fun storeImageLocally(context: ContextFromApi): Long {
        val url = URL("https://beta.userfeeds.io/api/contexts${context.images.avatar}")
        val input = url.openStream()
        val root = File(filesDir, "images")
        root.mkdirs()
        val output = FileOutputStream(File(root, "icon_${context.images.avatar.substringAfterLast('/')}"))
        return input.copyTo(output)
    }

    private fun onError(error: Throwable) {
        Log.e("ShareApp", "error", error)
    }
}
