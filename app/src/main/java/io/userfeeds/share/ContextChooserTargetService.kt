package io.userfeeds.share

import android.content.ComponentName
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.service.chooser.ChooserTarget
import android.service.chooser.ChooserTargetService

class ContextChooserTargetService : ChooserTargetService() {

    override fun onGetChooserTargets(targetActivityName: ComponentName, matchedFilter: IntentFilter): List<ChooserTarget> {
        return contexts.map {
            val icon = Icon.createWithResource("io.userfeeds.share", it.imageId)
            val intentExtras = ShareActivity.contextExtras(it)
            ChooserTarget(
                    it.name,
                    icon,
                    1.0f,
                    ComponentName(applicationContext, ShareActivity::class.java),
                    intentExtras)
        }
    }
}
