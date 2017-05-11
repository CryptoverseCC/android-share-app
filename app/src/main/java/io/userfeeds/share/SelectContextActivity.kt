package io.userfeeds.share

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.userfeeds.common.mapAdapter
import kotlinx.android.synthetic.main.select_context_activity.*
import okhttp3.ResponseBody

class SelectContextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_context_activity)
        ContextsApiProvider.get()
                .call()
                .map(this::toContextList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onContexts, this::onError)
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
        context_list.layoutManager = LinearLayoutManager(this)
        context_list.adapter = ContextListAdapter(contexts) {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            ShareActivity.start(this, it, text)
            finish()
        }
    }

    private fun onError(error: Throwable) {
        Log.e("ShareApp", "error", error)
    }
}
