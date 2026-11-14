package com.amar.tefsir

import android.content.Context
import android.graphics.Rect
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat

/** Izbor ajeta: "Odaberi" (sura + ajet) ili "Ukucaj" (npr. 2:255) – kao AyahNavigator.tsx */
class AyahNavigatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    var onNavigate: ((Int, Int) -> Unit)? = null

    private val btnModeSelect: TextView
    private val btnModeType: TextView
    private val boxSelects: View
    private val boxType: View
    private val spSura: Spinner
    private val spAyah: Spinner
    private val etRef: EditText
    private val tvHint: TextView
    private var textMode = false
    private var currentSura = 1

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_navigator, this, true)
        btnModeSelect = findViewById(R.id.btnModeSelect)
        btnModeType = findViewById(R.id.btnModeType)
        boxSelects = findViewById(R.id.boxSelects)
        boxType = findViewById(R.id.boxType)
        spSura = findViewById(R.id.spSura)
        spAyah = findViewById(R.id.spAyah)
        etRef = findViewById(R.id.etRef)
        tvHint = findViewById(R.id.tvHint)

        val suraItems = (1..114).map { n ->
            SpannableStringBuilder("$n. ${Quran.NAME_BS[n]} (")
                .append(TextRender.arabicSpan(context, Quran.NAME_AR[n]))
                .append(")") as CharSequence
        }
        spSura.adapter = ArrayAdapter(context, R.layout.item_spinner, suraItems).also {
            it.setDropDownViewResource(R.layout.item_spinner_dropdown)
        }
        setAyahAdapter(1, 1)

        spSura.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val s = position + 1
                if (s != currentSura) {
                    val keep = minOf(spAyah.selectedItemPosition + 1, Quran.AYAH_COUNT[s]).coerceAtLeast(1)
                    setAyahAdapter(s, keep)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnModeSelect.setOnClickListener { setMode(false) }
        btnModeType.setOnClickListener { setMode(true) }

        findViewById<View>(R.id.btnColon).setOnClickListener {
            val t = etRef.text?.toString() ?: ""
            if (!t.contains(":")) {
                etRef.setText("$t:")
                etRef.setSelection(etRef.text.length)
            }
            etRef.showKeyboard()
        }

        etRef.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                go(); true
            } else false
        }
        etRef.setOnFocusChangeListener { _, hasFocus ->
            showError(false)
            if (hasFocus) revealInput()
        }
        etRef.setOnClickListener { revealInput() }
        etRef.doAfterTextChanged { showError(false) }

        findViewById<View>(R.id.btnGo).setOnClickListener { go() }

        ViewCompat.setWindowInsetsAnimationCallback(this,
            object : WindowInsetsAnimationCompat.Callback(WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    running: MutableList<WindowInsetsAnimationCompat>,
                ): WindowInsetsCompat = insets

                override fun onEnd(animation: WindowInsetsAnimationCompat) {
                    if ((animation.typeMask and WindowInsetsCompat.Type.ime()) != 0 && etRef.hasFocus()) {
                        post { scrollToInput() }
                    }
                }
            })
        setMode(false)
    }

    /** Postavi izbornike na trenutni ajet (npr. u dijalogu "Idi na ajet"). */
    fun setInitial(sura: Int, ayah: Int) {
        spSura.setSelection(sura - 1, false)
        setAyahAdapter(sura, ayah)
        etRef.setText("$sura:$ayah")
    }

    private fun setAyahAdapter(sura: Int, selectAyah: Int) {
        currentSura = sura
        val items = (1..Quran.AYAH_COUNT[sura]).map { it.toString() }
        spAyah.adapter = ArrayAdapter(context, R.layout.item_spinner, items).also {
            it.setDropDownViewResource(R.layout.item_spinner_dropdown)
        }
        spAyah.setSelection((selectAyah - 1).coerceIn(0, items.size - 1), false)
    }

    private fun setMode(text: Boolean) {
        textMode = text
        val active = ContextCompat.getColor(context, R.color.sienna)
        val inactive = ContextCompat.getColor(context, R.color.ink_4)
        btnModeSelect.setBackgroundResource(if (!text) R.drawable.bg_toggle_active else 0)
        btnModeType.setBackgroundResource(if (text) R.drawable.bg_toggle_active else 0)
        btnModeSelect.setTextColor(if (!text) active else inactive)
        btnModeType.setTextColor(if (text) active else inactive)
        boxSelects.visibility = if (text) View.GONE else View.VISIBLE
        boxType.visibility = if (text) View.VISIBLE else View.GONE
        showError(false)
        if (text) {
            etRef.postDelayed({
                etRef.setSelection(etRef.text.length)
                etRef.showKeyboard()
                revealInput()
            }, 80)
        } else {
            etRef.hideKeyboard()
        }
    }

    /**
     * Kad se otvori tastatura, pomjeri ekran tako da se vide polje za unos i dugme "Otvori".
     * ScrollView ne zna da ga tastatura prekriva, pa se pomjeranje računa ručno.
     */
    private fun revealInput() {
        for (delay in longArrayOf(120, 300, 500, 800)) postDelayed({ scrollToInput() }, delay)
    }

    private fun scrollToInput() {
        if (!textMode || !isAttachedToWindow) return
        val go = findViewById<View>(R.id.btnGo)
        var p = parent
        while (p != null && p !is ScrollView) p = p.parent
        val sv = p as? ScrollView
        if (sv == null) {
            requestRectangleOnScreen(Rect(0, boxType.top, width, go.bottom), false)
            return
        }
        val insets = ViewCompat.getRootWindowInsets(this) ?: return
        val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
        val navBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
        val visibleBottom = rootView.height - maxOf(imeBottom, navBottom)
        val loc = IntArray(2)
        go.getLocationInWindow(loc)
        val needBottom = loc[1] + go.height + context.dp(16)
        val diff = needBottom - visibleBottom
        if (diff > 0) sv.smoothScrollBy(0, diff)
    }

    private fun showError(on: Boolean) = showError(if (on) "Pogrešan unos. Primjer: 2:255" else null)

    private fun showError(msg: String?) {
        tvHint.text = msg ?: "Sura:Ajet"
        tvHint.setTextColor(ContextCompat.getColor(context, if (msg != null) R.color.error else R.color.ink_4))
    }

    /** null = ispravno, inače kratka poruka o grešci */
    private fun validate(text: String): String? {
        val m = Regex("^\\s*(\\d+)\\s*[:\\s]\\s*(\\d+)\\s*$").find(text)
            ?: return "Pogrešan unos. Primjer: 2:255"
        val s = m.groupValues[1].toIntOrNull() ?: return "Pogrešan unos. Primjer: 2:255"
        val a = m.groupValues[2].toIntOrNull() ?: return "Pogrešan unos. Primjer: 2:255"
        if (s !in 1..114) return "Pogrešan unos: sura mora biti 1–114."
        val max = Quran.AYAH_COUNT[s]
        if (a !in 1..max) return "Pogrešan unos: sura $s ima ajete 1–$max."
        return null
    }

    private fun go() {
        if (textMode) {
            val text = etRef.text?.toString() ?: ""
            val err = validate(text)
            val ref = Quran.parseRef(text)
            if (err != null || ref == null) {
                showError(err ?: "Pogrešan unos. Primjer: 2:255")
                return
            }
            etRef.hideKeyboard()
            onNavigate?.invoke(ref.first, ref.second)
        } else {
            onNavigate?.invoke(spSura.selectedItemPosition + 1, spAyah.selectedItemPosition + 1)
        }
    }
}
