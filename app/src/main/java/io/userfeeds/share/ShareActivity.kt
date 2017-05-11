package io.userfeeds.share

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.share_activity.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ShareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_activity)
        share.setOnClickListener { sendClaim() }
    }

    private fun sendClaim() {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://beta.userfeeds.io/api/")
                .client(OkHttpClient.Builder()
                        .apply { if (BuildConfig.DEBUG) addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)) }
                        .build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
        val api = retrofit.create(ThoughtApi::class.java)
        val id = intent.getStringExtra("id")
        val text = intent.getStringExtra(Intent.EXTRA_TEXT)
        Log.e("ShareActivity", "$id / $text")
        val body = ThoughtDto(
                id,
                listOf("Claim"),
                Claim(text),
                listOf(Credit("interface", "android:io.userfeeds.share")),
                Signature("Ethereum.Transaction")
        )
        api.call(body)
                .subscribe(this::onSuccess, this::onError)
    }

    private fun onSuccess() {
        Log.i("ShareActivity", "share success")
    }

    private fun onError(error: Throwable) {
        Log.e("ShareActivity", "error", error)
    }
}
