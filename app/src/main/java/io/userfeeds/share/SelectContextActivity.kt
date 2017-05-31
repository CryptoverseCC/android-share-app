package io.userfeeds.share

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.userfeeds.sdk.core.UserfeedsService
import io.userfeeds.sdk.core.context.ShareContext
import kotlinx.android.synthetic.main.select_context_activity.*

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
            UserfeedsService.get().getContexts()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally { progressBar.visibility = View.GONE }
                    .subscribe(this::onContexts, this::onError)
        }
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
