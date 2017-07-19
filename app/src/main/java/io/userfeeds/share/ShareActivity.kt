package io.userfeeds.share

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.userfeeds.sdk.core.UserfeedsService
import io.userfeeds.sdk.core.signing.KeyPairHex
import io.userfeeds.sdk.core.storage.Claim
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
            putString("context.name", shareContext.name)
            putInt("context.imageId", shareContext.imageId)
        }
    }

    private val shareContext by lazy(NONE) {
        if (intent.hasExtra("context")) {
            intent.getParcelableExtra<ShareContext>("context")
        } else {
            ShareContext(
                    intent.getStringExtra("context.id"),
                    intent.getStringExtra("context.name"),
                    intent.getIntExtra("context.imageId", 0)
            )
        }
    }
    private val text by lazy { intent.getStringExtra(Intent.EXTRA_TEXT) }
    private val label: String? by lazy { intent.getStringExtra("label") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_activity)
        Glide.with(this).load(shareContext.imageId).into(contextImage)
        textToShare.setText(text)
        share.setOnClickListener { sendClaim() }
    }

    private fun sendClaim() {
        UserfeedsService.get().putClaim(
                shareContext.id,
                if (label != null) listOf("labels") else emptyList(),
                Claim(
                        target = textToShare.text.toString(),
                        labels = if (label != null) listOf(label!!) else null
                ),
                "android:io.userfeeds.share",
                KeyPairHex(
                        "308193020100301306072a8648ce3d020106082a8648ce3d0301070479307702010104200f08c82cf25ff675525d5f3248a323d40b8e459d3ebde39921ea2201d3e333e0a00a06082a8648ce3d030107a14403420004c707bde221a1466ca7c43db02be98367ed2a2208adedab63f01169c203000b3de20a19d4cdc50ff46cd52718314bdba5170b4719225d7e6bae27589a699e6f1b",
                        "3059301306072a8648ce3d020106082a8648ce3d03010703420004c707bde221a1466ca7c43db02be98367ed2a2208adedab63f01169c203000b3de20a19d4cdc50ff46cd52718314bdba5170b4719225d7e6bae27589a699e6f1b")
        )
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
