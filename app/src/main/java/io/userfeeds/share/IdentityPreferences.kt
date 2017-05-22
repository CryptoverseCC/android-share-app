package io.userfeeds.share

import android.content.SharedPreferences

class IdentityPreferences(private val prefs: SharedPreferences) {

    fun save(identity: Identity) {
        prefs.edit()
                .putString(PRIVATE_KEY, identity.private)
                .putString(PUBLIC_KEY, identity.public)
                .apply()
    }

    fun load(): Identity? {
        return if (prefs.contains(PRIVATE_KEY)) {
            Identity(
                    private = prefs.getString(PRIVATE_KEY, null),
                    public = prefs.getString(PUBLIC_KEY, null)
            )
        } else {
            null
        }
    }

    companion object {

        private const val PRIVATE_KEY = "private"
        private const val PUBLIC_KEY = "public"
    }
}
