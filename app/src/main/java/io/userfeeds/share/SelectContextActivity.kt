package io.userfeeds.share

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.userfeeds.common.mapAdapter
import kotlinx.android.synthetic.main.select_context_activity.*
import okhttp3.ResponseBody

class SelectContextActivity : AppCompatActivity() {

    private val shareContext by lazy(LazyThreadSafetyMode.NONE) {
        val id: String? = intent.getStringExtra("io.userfeeds.share.context.id")
        val hashtag: String? = intent.getStringExtra("io.userfeeds.share.context.hashtag")
        val imageUrl: String? = intent.getStringExtra("io.userfeeds.share.context.imageUrl")
        if (id != null && hashtag != null && imageUrl != null) {
            ShareContext(id, hashtag, imageUrl)
        } else {
            null
        }
    }
    private val text by lazy { intent.getStringExtra(Intent.EXTRA_TEXT) }
    private val label by lazy { intent.getStringExtra("io.userfeeds.share.label") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_context_activity)
        val shareCtx = shareContext
        if (shareCtx != null) {
            startShareActivity(shareCtx)
        } else {
            ContextsApiProvider.get()
                    .call()
                    .map(this::toContextList)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally { progressBar.visibility = View.GONE }
                    .subscribe(this::onContexts, this::onError)
        }
    }

    private fun toContextList(responseBody: ResponseBody): List<ShareContext> {
        val contextsString = responseBody.string()
        val moshi = Moshi.Builder().build()
        val adapter = moshi.mapAdapter<String, ContextFromApi>()
        val contextsMap = adapter.fromJson(contextsString)
        return contextsMap.map { (id, context) -> toContext(id, context) }
    }

    private fun toContext(id: String, context: ContextFromApi): ShareContext {
        return ShareContext(
                id = id,
                hashtag = context.hashtag,
                imageUrl = "https://beta.userfeeds.io/api/contexts${context.images.avatar}"
        )
    }

    private fun onContexts(contexts: List<ShareContext>) {
        contextList.layoutManager = LinearLayoutManager(this)
        contextList.adapter = ContextListAdapter(contexts, this::startShareActivity)
    }

    private fun startShareActivity(shareContext: ShareContext) {
        ShareActivity.start(this, shareContext, label, text)
        finish()
    }

    private fun onError(error: Throwable) {
        Log.e("ShareApp", "error", error)
    }
}
