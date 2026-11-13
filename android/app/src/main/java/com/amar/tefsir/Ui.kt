package com.amar.tefsir

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Context.dp(v: Int): Int = (v * resources.displayMetrics.density + 0.5f).toInt()

fun Context.isNight(): Boolean =
    (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

/** Sadržaj ide ispod statusne i navigacijske trake (Android 15+ to i zahtijeva). */
fun Activity.setupEdgeToEdge() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).apply {
        isAppearanceLightStatusBars = !isNight()
        isAppearanceLightNavigationBars = !isNight()
    }
}

/**
 * topView dobija gornji razmak (statusna traka), bottomView donji (navigacija / tastatura).
 * Početni padding iz XML-a se čuva.
 */
fun applySystemInsets(root: View, topView: View, bottomView: View) {
    val top0 = topView.paddingTop
    val bottom0 = bottomView.paddingBottom
    val left0 = root.paddingLeft
    val right0 = root.paddingRight
    ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        root.setPadding(left0 + bars.left, root.paddingTop, right0 + bars.right, root.paddingBottom)
        topView.setPadding(topView.paddingLeft, top0 + bars.top, topView.paddingRight, topView.paddingBottom)
        bottomView.setPadding(
            bottomView.paddingLeft, bottomView.paddingTop, bottomView.paddingRight,
            bottom0 + maxOf(bars.bottom, ime.bottom)
        )
        insets
    }
}

fun View.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    requestFocus()
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}
