package io.userfeeds.share

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.preference.PreferenceManager
import android.service.chooser.ChooserTarget
import android.service.chooser.ChooserTargetService
import android.support.v4.content.FileProvider
import com.squareup.moshi.Moshi
import io.userfeeds.common.mapAdapter
import java.io.File

class ContextChooserTargetService : ChooserTargetService() {

    override fun onGetChooserTargets(targetActivityName: ComponentName, matchedFilter: IntentFilter): List<ChooserTarget> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val contextsString: String? = prefs.getString("contexts", null)
        return if (contextsString == null) {
            emptyList()
        } else {
            val moshi = Moshi.Builder().build()
            val adapter = moshi.mapAdapter<String, ContextFromApi>()
            val contexts = adapter.fromJson(contextsString)
            contexts.map { (id, context) ->
                val file = File(File(filesDir, "images"), "icon_${context.images.avatar.substringAfterLast('/')}")
                val contentUri = FileProvider.getUriForFile(this, "io.userfeeds.fileprovider", file)
                val icon = Icon.createWithContentUri(contentUri)
                grantUriPermission("android", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val intentExtras = ShareActivity.contextExtras(ShareContext(
                        id = id,
                        hashtag = context.hashtag,
                        imageUrl = "https://beta.userfeeds.io/api/contexts${context.images.avatar}"
                ))
                ChooserTarget(
                        context.hashtag,
                        icon,
                        1.0f,
                        ComponentName(applicationContext, ShareActivity::class.java),
                        intentExtras)
            }
        }
    }
}
