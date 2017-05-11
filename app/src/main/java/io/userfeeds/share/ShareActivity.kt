package io.userfeeds.share

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.share_activity.*

class ShareActivity : AppCompatActivity() {

    companion object {

        fun start(context: Context, shareContext: ShareContext, text: String) {
            val intent = Intent(context, ShareActivity::class.java)
            intent.putExtra("context", shareContext)
            intent.putExtra(Intent.EXTRA_TEXT, text)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_activity)
        share.setOnClickListener { sendClaim() }
    }

    private fun sendClaim() {
        val shareContext = if (intent.hasExtra("context")) {
            intent.getParcelableExtra<ShareContext>("context")
        } else {
            ShareContext(
                    intent.getStringExtra("context.id"),
                    intent.getStringExtra("context.hashtag"),
                    intent.getStringExtra("context.imageUrl")
            )
        }
        val text = intent.getStringExtra(Intent.EXTRA_TEXT)
        Log.e("ShareActivity", "$shareContext / $text")
        val body = ThoughtDto(
                shareContext.id,
                listOf("Claim"),
                Claim(text),
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
