package io.userfeeds.share

import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import com.squareup.moshi.Moshi
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.userfeeds.common.listAdapter
import io.userfeeds.sdk.core.UserfeedsSdk
import io.userfeeds.sdk.core.context.ShareContext
import io.userfeeds.sdk.core.context.getContexts
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class ShareApp : Application() {

    override fun onCreate() {
        super.onCreate()
        UserfeedsSdk.initialize(
                apiKey = "59049c8fdfed920001508e2a94bad07aa8f846674ae92e8765bd926c",
                debug = BuildConfig.DEBUG)
        getContexts()
                .subscribe(this::onContexts, this::onError)
    }

    private fun onContexts(contexts: List<ShareContext>) {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.listAdapter<ShareContext>()
        val contextsString = adapter.toJson(contexts)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit()
                .putString("contexts", contextsString)
                .apply()
        contexts.forEach {
            Single.fromCallable { storeImageLocally(it) }
                    .subscribeOn(Schedulers.io())
                    .subscribe({ Log.i("ShareApp", "Loaded image. Size $it") }, this::onError)
        }
    }

    private fun storeImageLocally(context: ShareContext): Long {
        val url = URL(context.imageUrl)
        val input = url.openStream()
        val root = File(filesDir, "images")
        root.mkdirs()
        val output = FileOutputStream(File(root, "icon_${context.imageUrl.substringAfterLast('/')}"))
        return input.copyTo(output)
    }

    private fun onError(error: Throwable) {
        Log.e("ShareApp", "error", error)
    }
}
