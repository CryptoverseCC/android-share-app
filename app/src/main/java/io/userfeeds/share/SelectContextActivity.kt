package io.userfeeds.share

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.select_context_activity.*

class SelectContextActivity : AppCompatActivity() {

    private val shareContext by lazy(LazyThreadSafetyMode.NONE) {
        val id: String? = intent.getStringExtra("io.userfeeds.share.context")
        if (id != null) {
            contexts.firstOrNull { it.id == id } ?: ShareContext(id, "[$id]", 0)
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
            contextList.layoutManager = LinearLayoutManager(this)
            contextList.adapter = ContextListAdapter(contexts, this::startShareActivity)
        }
    }

    private fun startShareActivity(shareContext: ShareContext) {
        ShareActivity.start(this, shareContext, label, text)
        finish()
    }
}
