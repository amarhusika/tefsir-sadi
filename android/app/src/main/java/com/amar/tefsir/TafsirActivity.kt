package com.amar.tefsir

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.concurrent.Executors
import kotlin.math.abs

/** Stranica ajeta: arapski tekst, prijevod značenja, tefsir es-Sa'di */
class TafsirActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_SURA = "sura"
        private const val EXTRA_AYAH = "ayah"
        private const val STATE_REF = "ref"
        const val SITE = "https://tefsir.pages.dev"

        fun intent(ctx: Context, sura: Int, ayah: Int) =
            Intent(ctx, TafsirActivity::class.java).putExtra(EXTRA_SURA, sura).putExtra(EXTRA_AYAH, ayah)
    }

    private val executor = Executors.newSingleThreadExecutor()
    private val main = Handler(Looper.getMainLooper())
    private var loadToken = 0

    private var sura = 0
    private var ayah = 0
    private var data: Ajet? = null

    private lateinit var scroll: ScrollView
    private lateinit var content: View
    private lateinit var progress: View
    private lateinit var errorBox: View
    private lateinit var tvRefAr: TextView
    private lateinit var tvRefNum: TextView
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var tvSuraAr: TextView
    private lateinit var tvSuraBs: TextView
    private lateinit var tvSuraCount: TextView
    private lateinit var tvBigRef: TextView
    private lateinit var tvAyahAr: TextView
    private lateinit var tvPrevod: TextView
    private lateinit var tvGroup: TextView
    private lateinit var tvCovered: TextView
    private lateinit var tvTafsir: TextView
    private lateinit var tvPending: TextView
    private lateinit var btnToggleAr: TextView
    private lateinit var tvTafsirAr: TextView
    private lateinit var btnCopy: TextView
    private lateinit var btnBottomPrev: TextView
    private lateinit var btnBottomNext: TextView

    private lateinit var gestures: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupEdgeToEdge()
        setContentView(R.layout.activity_tafsir)

        scroll = findViewById(R.id.scroll)
        content = findViewById(R.id.content)
        progress = findViewById(R.id.progress)
        errorBox = findViewById(R.id.errorBox)
        tvRefAr = findViewById(R.id.tvRefAr)
        tvRefNum = findViewById(R.id.tvRefNum)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        tvSuraAr = findViewById(R.id.tvSuraAr)
        tvSuraBs = findViewById(R.id.tvSuraBs)
        tvSuraCount = findViewById(R.id.tvSuraCount)
        tvBigRef = findViewById(R.id.tvBigRef)
        tvAyahAr = findViewById(R.id.tvAyahAr)
        tvPrevod = findViewById(R.id.tvPrevod)
        tvGroup = findViewById(R.id.tvGroup)
        tvCovered = findViewById(R.id.tvCovered)
        tvTafsir = findViewById(R.id.tvTafsir)
        tvPending = findViewById(R.id.tvPending)
        btnToggleAr = findViewById(R.id.btnToggleAr)
        tvTafsirAr = findViewById(R.id.tvTafsirAr)
        btnCopy = findViewById(R.id.btnCopy)
        btnBottomPrev = findViewById(R.id.btnBottomPrev)
        btnBottomNext = findViewById(R.id.btnBottomNext)

        applySystemInsets(findViewById(R.id.root), findViewById(R.id.root), content)

        tvTafsir.justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD
        tvTafsirAr.justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD
        tvCovered.movementMethod = LinkMovementMethod.getInstance()

        findViewById<View>(R.id.btnHome).setOnClickListener { goHome() }
        findViewById<View>(R.id.btnErrHome).setOnClickListener { goHome() }
        findViewById<View>(R.id.refBox).setOnClickListener { showGotoDialog() }
        findViewById<View>(R.id.btnGoto).setOnClickListener { showGotoDialog() }
        btnPrev.setOnClickListener { goPrev() }
        btnNext.setOnClickListener { goNext() }
        btnBottomPrev.setOnClickListener { goPrev() }
        btnBottomNext.setOnClickListener { goNext() }
        btnCopy.setOnClickListener { copy() }
        findViewById<View>(R.id.btnShare).setOnClickListener { share() }
        btnToggleAr.setOnClickListener {
            Prefs.setShowAr(this, !Prefs.showAr(this))
            updateArToggle()
        }

        // prevlačenje lijevo/desno = sljedeći/prethodni ajet
        val minDist = dp(90)
        gestures = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null || sura == 0) return false
                val dx = e2.x - e1.x
                val dy = e2.y - e1.y
                if (abs(dx) > minDist && abs(dx) > abs(dy) * 2 && abs(velocityX) > 700) {
                    if (dx < 0) goNext() else goPrev()
                    return true
                }
                return false
            }
        })

        val ref = savedInstanceState?.getString(STATE_REF)?.let { Quran.parseRef(it) }
            ?: refFromIntent(intent)
        if (ref == null) showNotFound() else show(ref.first, ref.second, scrollTop = savedInstanceState == null)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        refFromIntent(intent)?.let { show(it.first, it.second) }
    }

    private fun refFromIntent(i: Intent?): Pair<Int, Int>? {
        if (i == null) return null
        val s = i.getIntExtra(EXTRA_SURA, 0)
        val a = i.getIntExtra(EXTRA_AYAH, 0)
        if (Quran.isValid(s, a)) return s to a
        // link https://tefsir.pages.dev/2:255
        val path = i.data?.path?.trim('/') ?: return null
        return Quran.parseRef(path)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (sura > 0) outState.putString(STATE_REF, "$sura:$ayah")
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdownNow()
        main.removeCallbacksAndMessages(null)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        gestures.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> { goPrev(); return true }
            KeyEvent.KEYCODE_DPAD_RIGHT -> { goNext(); return true }
        }
        return super.onKeyDown(keyCode, event)
    }

    // ─── Navigacija ────────────────────────────────────────────────────────

    private fun goPrev() { if (sura > 0) Quran.prev(sura, ayah)?.let { show(it.first, it.second) } }
    private fun goNext() { if (sura > 0) Quran.next(sura, ayah)?.let { show(it.first, it.second) } }

    private fun goHome() {
        if (isTaskRoot) startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showGotoDialog() {
        val nav = AyahNavigatorView(this)
        val pad = dp(20)
        nav.setPadding(pad, dp(8), pad, 0)
        if (sura > 0) nav.setInitial(sura, ayah)
        val dialog: AlertDialog = MaterialAlertDialogBuilder(this)
            .setTitle("Idi na ajet")
            .setView(nav)
            .setNegativeButton("Zatvori", null)
            .create()
        nav.onNavigate = { s, a ->
            dialog.dismiss()
            show(s, a)
        }
        dialog.show()
    }

    // ─── Učitavanje ────────────────────────────────────────────────────────

    private fun show(s: Int, a: Int, scrollTop: Boolean = true) {
        if (!Quran.isValid(s, a)) { showNotFound(); return }
        sura = s
        ayah = a
        data = null
        updateTopbar()
        title = "$s:$a ${Quran.NAME_BS[s]} — Tefsir es-Sa'di"

        val token = ++loadToken
        errorBox.visibility = View.GONE
        // indikator samo ako učitavanje potraje (prvo otvaranje velike sure)
        val showProgress = Runnable {
            if (token == loadToken) {
                content.visibility = View.INVISIBLE
                progress.visibility = View.VISIBLE
            }
        }
        main.postDelayed(showProgress, 120)

        executor.execute {
            val result: Result<Pair<SuraFile, Ajet>> = runCatching {
                val f = TefsirRepo.loadSura(applicationContext, s)
                f to TefsirRepo.ajet(f, s, a)
            }
            main.post {
                if (token != loadToken || isFinishing) return@post
                main.removeCallbacks(showProgress)
                progress.visibility = View.GONE
                result.fold(
                    onSuccess = { (f, aj) -> bind(f, aj, scrollTop) },
                    onFailure = { e ->
                        val msg = if (e is DataException) e.message ?: "Greška" else "Greška pri učitavanju podataka"
                        showError("⚠", "", msg, false)
                    }
                )
            }
        }
    }

    private fun updateTopbar() {
        tvRefAr.text = Quran.NAME_AR[sura]
        tvRefNum.text = "$sura:$ayah"
        val prev = Quran.prev(sura, ayah)
        val next = Quran.next(sura, ayah)
        btnPrev.visibility = View.VISIBLE
        btnNext.visibility = View.VISIBLE
        btnPrev.isEnabled = prev != null
        btnPrev.alpha = if (prev != null) 1f else 0.3f
        btnNext.isEnabled = next != null
        btnNext.alpha = if (next != null) 1f else 0.3f
        setBottomBtn(btnBottomPrev, "Prethodni ajet", prev)
        setBottomBtn(btnBottomNext, "Sljedeći ajet", next)
    }

    private fun setBottomBtn(v: TextView, label: String, ref: Pair<Int, Int>?) {
        if (ref == null) {
            v.visibility = View.INVISIBLE
            return
        }
        v.visibility = View.VISIBLE
        val sb = SpannableStringBuilder(label).append("\n")
        val st = sb.length
        sb.append("${ref.first}:${ref.second}")
        sb.setSpan(StyleSpan(Typeface.BOLD), st, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sb.setSpan(RelativeSizeSpan(1.25f), st, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sb.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.sienna)), st, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        v.text = sb
    }

    private fun bind(f: SuraFile, aj: Ajet, scrollTop: Boolean) {
        data = aj
        Prefs.setLastRef(this, sura, ayah)

        tvSuraAr.text = Quran.NAME_AR[sura]
        tvSuraBs.text = f.nazivBos.ifEmpty { Quran.NAME_BS[sura] }
        tvSuraCount.text = "${Quran.NAME_BS[sura]} · ${Quran.AYAH_COUNT[sura]} ajeta"
        tvBigRef.text = "$sura:$ayah"
        tvAyahAr.text = aj.arapski
        tvPrevod.text = aj.prevod

        val hasBs = aj.tefsirBs.isNotBlank()
        val hasAr = aj.tefsirAr.isNotBlank()

        if (aj.grupa != null) {
            tvGroup.text = "Zajednički tefsir ajeta $sura:${aj.grupa.first}–${aj.grupa.second}"
            tvGroup.visibility = View.VISIBLE
        } else tvGroup.visibility = View.GONE

        if (!hasAr) {
            tvCovered.text = coveredText(aj.vidi)
            tvCovered.visibility = View.VISIBLE
        } else tvCovered.visibility = View.GONE

        if (hasBs) {
            tvTafsir.text = TextRender.renderBs(this, aj.tefsirBs)
            tvTafsir.visibility = View.VISIBLE
        } else tvTafsir.visibility = View.GONE

        tvPending.visibility = if (hasAr && !hasBs) View.VISIBLE else View.GONE
        tvTafsirAr.text = if (hasAr) TextRender.renderAr(this, aj.tefsirAr) else ""
        updateArToggle()

        errorBox.visibility = View.GONE
        content.visibility = View.VISIBLE
        if (scrollTop) {
            scroll.scrollTo(0, 0)
            content.alpha = 0f
            content.animate().alpha(1f).setDuration(160).start()
        }
    }

    private fun coveredText(vidi: List<String>): CharSequence {
        val sb = SpannableStringBuilder("Tumačenje ovog ajeta obuhvaćeno je tefsirom susjednih ajeta")
        val refs = vidi.mapNotNull { v -> Quran.parseRef(v)?.let { v to it } }
        if (refs.isNotEmpty()) {
            sb.append(" — pogledaj ")
            val color = ContextCompat.getColor(this, R.color.sienna)
            refs.forEachIndexed { i, (label, ref) ->
                if (i > 0) sb.append(" i ")
                val st = sb.length
                sb.append(label)
                sb.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) { show(ref.first, ref.second) }
                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = color
                        ds.isUnderlineText = true
                        ds.isFakeBoldText = true
                    }
                }, st, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        sb.append(".")
        return sb
    }

    private fun updateArToggle() {
        val aj = data ?: return
        val hasBs = aj.tefsirBs.isNotBlank()
        val hasAr = aj.tefsirAr.isNotBlank()
        val showAr = Prefs.showAr(this)
        btnToggleAr.visibility = if (hasAr && hasBs) View.VISIBLE else View.GONE
        btnToggleAr.text = if (showAr) "Sakrij arapski original" else "Prikaži arapski original"
        tvTafsirAr.visibility = if (hasAr && (!hasBs || showAr)) View.VISIBLE else View.GONE
    }

    private fun showNotFound() {
        sura = 0
        showError("٤٠٤", "Stranica nije pronađena", "Traženi ajet ne postoji ili je adresa pogrešna.", true)
        tvRefAr.text = ""
        tvRefNum.text = "—"
        btnPrev.visibility = View.INVISIBLE
        btnNext.visibility = View.INVISIBLE
    }

    private fun showError(icon: String, title: String, msg: String, homeBtn: Boolean) {
        content.visibility = View.INVISIBLE
        progress.visibility = View.GONE
        errorBox.visibility = View.VISIBLE
        findViewById<TextView>(R.id.tvErrIcon).text = icon
        findViewById<TextView>(R.id.tvErrTitle).apply { text = title; visibility = if (title.isEmpty()) View.GONE else View.VISIBLE }
        findViewById<TextView>(R.id.tvErrMsg).text = msg
        findViewById<View>(R.id.btnErrHome).visibility = if (homeBtn) View.VISIBLE else View.GONE
    }

    // ─── Kopiranje i dijeljenje ────────────────────────────────────────────

    /** Isti tekst kao "Kopiraj" na webu */
    private fun buildShareText(): String? {
        val aj = data ?: return null
        val parts = mutableListOf(
            "${Quran.NAME_BS[sura]} ($sura:$ayah)",
            "Prijevod značenja:\n${aj.prevod}",
        )
        if (aj.tefsirBs.isNotBlank()) {
            val grp = aj.grupa?.let { " (zajednički tefsir ajeta $sura:${it.first}–${it.second})" } ?: ""
            parts.add("Tefsir es-Sa'di$grp:\n${TextRender.plainBs(aj.tefsirBs)}")
        } else if (aj.tefsirAr.isBlank()) {
            parts.add("Tefsir es-Sa'di: tumačenje ovog ajeta obuhvaćeno je tefsirom ajeta ${aj.vidi.joinToString(" i ")}.")
        }
        parts.add("$SITE/$sura:$ayah")
        return parts.joinToString("\n\n")
    }

    private fun copy() {
        val text = buildShareText() ?: return
        val ok = try {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("Tefsir es-Sa'di $sura:$ayah", text))
            true
        } catch (e: Exception) { false }
        btnCopy.text = if (ok) "Kopirano ✓" else "Greška pri kopiranju"
        btnCopy.setTextColor(ContextCompat.getColor(this, if (ok) R.color.sage else R.color.error))
        // Android 13+ sam prikazuje potvrdu kopiranja
        if (ok && Build.VERSION.SDK_INT < 33) Toast.makeText(this, "Kopirano", Toast.LENGTH_SHORT).show()
        btnCopy.removeCallbacks(resetCopy)
        btnCopy.postDelayed(resetCopy, 2000)
    }

    private val resetCopy = Runnable {
        btnCopy.text = "Kopiraj"
        btnCopy.setTextColor(ContextCompat.getColor(this, R.color.sienna))
    }

    private fun share() {
        val text = buildShareText() ?: return
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "${Quran.NAME_BS[sura]} ($sura:$ayah) — Tefsir es-Sa'di")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(send, "Podijeli"))
    }
}
