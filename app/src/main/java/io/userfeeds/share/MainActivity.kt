package io.userfeeds.share

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.main_activity.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        importIdentity.setOnClickListener {
            IntentIntegrator(this).initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (scanResult != null) {
            debugView.text = scanResult.contents
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(Identity::class.java)
            val identity = adapter.fromJson(scanResult.contents)
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            IdentityPreferences(prefs).save(identity)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
