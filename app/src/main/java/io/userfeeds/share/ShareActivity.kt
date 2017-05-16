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
                Claim(text, labels),
                listOf(Credit("interface", "android:io.userfeeds.share")),
                Signature(
                        "SHA256withECDSA.secp256r1",
                        "04ad7956d1b8176e11046a32c236c39ed7869b67b8ec1c84100831495c9edbb8e3f63a828b9e353def43c03e64dd107071935fc908aaa291482ad9843d1b131a67",
                        "3045022035f989f95a07da022a07ead6b5eafe31756f6eddea7effd91c52dbca832ed457022100aa63a13f72991117b7fef1ffbe2930b80999619b3f920b0c13b5cdfbbf3eb474"
                )
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
