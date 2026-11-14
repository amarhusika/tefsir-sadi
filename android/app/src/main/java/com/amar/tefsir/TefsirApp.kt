package com.amar.tefsir

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate

/** Primjenjuje odabranu temu (sistemska / svijetla / tamna) pri pokretanju aplikacije. */
class TefsirApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(Prefs.nightMode(this))
    }
}

object Theme {
    private fun systemDark(): Boolean =
        (Resources.getSystem().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES

    /**
     * Prebacuje na suprotnu temu od trenutne. Ako je nova tema ista kao sistemska,
     * aplikacija ponovo prati sistem.
     */
    fun toggle(ctx: Context) {
        val next = !ctx.isNight()
        val mode = when {
            next == systemDark() -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            next -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
        Prefs.setNightMode(ctx, mode)
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
