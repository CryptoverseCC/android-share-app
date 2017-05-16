package io.userfeeds.share

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.share_activity.*
import kotlin.LazyThreadSafetyMode.NONE

class ShareActivity : AppCompatActivity() {

    companion object {

        fun start(context: Context, shareContext: ShareContext, label: String?, text: String) {
            val intent = Intent(context, ShareActivity::class.java)
            intent.putExtra("context", shareContext)
            intent.putExtra("label", label)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            context.startActivity(intent)
        }

        fun contextExtras(shareContext: ShareContext) = Bundle().apply {
            putString("context.id", shareContext.id)
            putString("context.hashtag", shareContext.hashtag)
            putString("context.imageUrl", shareContext.imageUrl)
        }
    }

    private val shareContext by lazy(NONE) {
        if (intent.hasExtra("context")) {
            intent.getParcelableExtra<ShareContext>("context")
        } else {
            ShareContext(
                    intent.getStringExtra("context.id"),
                    intent.getStringExtra("context.hashtag"),
                    intent.getStringExtra("context.imageUrl")
            )
        }
    }
    private val text by lazy { intent.getStringExtra(Intent.EXTRA_TEXT) }
    private val label: String? by lazy { intent.getStringExtra("label") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_activity)
        Glide.with(this).load(shareContext.imageUrl).into(contextImage)
        textToShare.setText(text)
        share.setOnClickListener { sendClaim() }
    }

    private fun sendClaim() {
        val labels = if (label != null) listOf(label!!) else null
        val type = if (label != null) listOf("Claim", "Label") else listOf("Claim")
        val body = ThoughtDto(
                shareContext.id,
                type,
                Claim("text:$text", labels),
                listOf(Credit("interface", "android:io.userfeeds.share")),
                Signature("Ethereum.Transaction")
        )
        ThoughtApiProvider.get()
                .call(body)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { share.isEnabled = false }
                .doOnError { share.isEnabled = true }
                .subscribe(this::onSuccess, this::onError)
    }

    private fun onSuccess() {
        Log.i("ShareActivity", "share success")
        finish()
    }

    private fun onError(error: Throwable) {
        Log.e("ShareActivity", "error", error)
    }
}
