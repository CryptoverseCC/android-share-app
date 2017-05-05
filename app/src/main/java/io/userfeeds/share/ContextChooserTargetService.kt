package io.userfeeds.share

import android.content.ComponentName
import android.content.IntentFilter
import android.preference.PreferenceManager
import android.service.chooser.ChooserTarget
import android.service.chooser.ChooserTargetService

class ContextChooserTargetService : ChooserTargetService() {

    override fun onGetChooserTargets(targetActivityName: ComponentName, matchedFilter: IntentFilter): List<ChooserTarget> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val contextsString: String? = prefs.getString("contexts", null)
        return if (contextsString == null) {
            emptyList()
        } else {
            TODO()
        }
    }
}
