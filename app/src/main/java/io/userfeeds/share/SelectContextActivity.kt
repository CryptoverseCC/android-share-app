package io.userfeeds.share

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.userfeeds.common.mapAdapter
import kotlinx.android.synthetic.main.select_context_activity.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class SelectContextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_context_activity)
        val retrofit = Retrofit.Builder()
                .baseUrl("https://beta.userfeeds.io/api/")
                .client(OkHttpClient.Builder()
                        .apply { if (BuildConfig.DEBUG) addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)) }
                        .build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
        val api = retrofit.create(ContextsApi::class.java)
        api.call()
                .map(this::toContextList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onContexts, this::onError)
    }

    private fun toContextList(responseBody: ResponseBody): List<ContextFromApi> {
        val contextsString = responseBody.string()
        val moshi = Moshi.Builder().build()
        val adapter = moshi.mapAdapter<String, ContextFromApi>()
        val contextsMap = adapter.fromJson(contextsString)
        return contextsMap.map { (_, context) -> context }
    }

    private fun onContexts(contexts: List<ContextFromApi>) {
        context_list.layoutManager = LinearLayoutManager(this)
        context_list.adapter = ContextListAdapter(contexts)
    }

    private fun onError(error: Throwable) {
        Log.e("ShareApp", "error", error)
    }
}
