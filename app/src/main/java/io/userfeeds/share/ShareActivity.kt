package io.userfeeds.share

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Patterns
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.userfeeds.sdk.core.UserfeedsService
import io.userfeeds.sdk.core.storage.Claim
import io.userfeeds.sdk.core.storage.ClaimWrapper
import io.userfeeds.sdk.core.storage.Signature
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

        private const val SIGN_REQUEST_CODE = 1001
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
        contextImage.setImageResource(shareContext.imageId)
        if (savedInstanceState == null) parseText()
        switchTitleSummaryButton.setOnClickListener { switchTitleSummary() }
        share.setOnClickListener { requestClaimSign() }
    }

    private fun parseText() {
        val matcher = Patterns.WEB_URL.matcher(text)
        if (matcher.find()) {
            val url = matcher.group()
            val summary = matcher.replaceFirst("").trim()
            urlView.setText(url)
            summaryView.setText(summary)
        } else {
            summaryView.setText(text)
        }
    }

    private fun switchTitleSummary() {
        val tmp = titleView.text
        titleView.text = summaryView.text
        summaryView.text = tmp
    }

    private fun requestClaimSign() {
        val intent = Intent("io.userfeeds.identity.SIGN_MESSAGE")
        intent.putExtra("io.userfeeds.identity.message", claimWrapper.toJson())
        startActivityForResult(intent, SIGN_REQUEST_CODE)
    }

    private val claimWrapper get() = ClaimWrapper.create(
            context = shareContext.id,
            type = if (label != null) listOf("link", "labels") else listOf("link"),
            claim = Claim(
                    target = urlView.text.toString(),
                    title = titleView.text.toString(),
                    summary = summaryView.text.toString(),
                    labels = if (label != null) listOf(label!!) else null
            ),
            clientId = "android:io.userfeeds.share")

    private inline fun <reified T> T.toJson(): String {
        return Moshi.Builder()
                .build()
                .adapter(T::class.java)
                .toJson(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SIGN_REQUEST_CODE && resultCode == RESULT_OK) {
            val signature = Signature.fromIntentData(data!!)
            sendClaim(signature)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sendClaim(signature: Signature) {
        UserfeedsService.get().putClaim(claimWrapper, signature)
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
