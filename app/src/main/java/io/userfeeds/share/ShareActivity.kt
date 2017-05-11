package io.userfeeds.share

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.share_activity.*

class ShareActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_activity)
        share.setOnClickListener { sendClaim() }
    }

    private fun sendClaim() {
        val id = intent.getStringExtra("id")
        val text = intent.getStringExtra(Intent.EXTRA_TEXT)
        Log.e("ShareActivity", "$id / $text")
        val body = ThoughtDto(
                id,
                listOf("Claim"),
                Claim(text),
                listOf(Credit("interface", "android:io.userfeeds.share")),
                Signature("Ethereum.Transaction")
        )
        ThoughtApiProvider.get()
                .call(body)
                .subscribe(this::onSuccess, this::onError)
    }

    private fun onSuccess() {
        Log.i("ShareActivity", "share success")
    }

    private fun onError(error: Throwable) {
        Log.e("ShareActivity", "error", error)
    }
}
