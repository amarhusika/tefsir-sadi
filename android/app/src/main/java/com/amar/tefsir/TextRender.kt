package com.amar.tefsir

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.MetricAffectingSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

/** Pravila prikaza kao na webu (App.tsx):
 *  prazan red = novi pasus, {…} = citat ajeta, [[…]] = napomena prevodioca. */
object TextRender {

    private val PARA_SPLIT = Regex("\\n\\s*\\n")
    private val TOKEN = Regex("\\{([^{}]+)\\}|\\[\\[([^\\]]+)\\]\\]")
    private val AR_QUOTE = Regex("\\{([^{}]+)\\}")

    private var arabicTf: Typeface? = null
    fun arabic(ctx: Context): Typeface? {
        if (arabicTf == null) arabicTf = ResourcesCompat.getFont(ctx, R.font.scheherazade)
        return arabicTf
    }

    /** citat koji se nastavlja iz prethodnog / u sljedeći ajet */
    private fun fixBraces(input: String): String {
        var p = input
        val iO = p.indexOf('{')
        val iC = p.indexOf('}')
        if (iC != -1 && (iO == -1 || iC < iO)) p = "{$p"
        if (p.lastIndexOf('{') > p.lastIndexOf('}')) p = "$p}"
        return p
    }

    private fun paragraphs(text: String): List<String> =
        text.split(PARA_SPLIT).map { it.trim() }.filter { it.isNotEmpty() }
            .map { fixBraces(it.replace(Regex("\\s*\\n\\s*"), " ")) }

    fun renderBs(ctx: Context, text: String): CharSequence {
        val gold = ContextCompat.getColor(ctx, R.color.gold)
        val quote = ContextCompat.getColor(ctx, R.color.sienna_d)
        val note = ContextCompat.getColor(ctx, R.color.ink_4)
        val sb = SpannableStringBuilder()
        paragraphs(text).forEachIndexed { i, p ->
            if (i > 0) {
                val st = sb.length
                sb.append("\n\n")
                sb.setSpan(RelativeSizeSpan(0.5f), st + 1, st + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            var pos = 0
            for (m in TOKEN.findAll(p)) {
                sb.append(p, pos, m.range.first)
                val q = m.groups[1]?.value
                if (q != null) {
                    span(sb, "„", ForegroundColorSpan(gold))
                    span(sb, q.trim(), StyleSpan(Typeface.BOLD), ForegroundColorSpan(quote))
                    span(sb, "“", ForegroundColorSpan(gold))
                } else {
                    span(sb, "[" + m.groupValues[2].trim() + "]", ForegroundColorSpan(note), RelativeSizeSpan(0.92f))
                }
                pos = m.range.last + 1
            }
            sb.append(p, pos, p.length)
        }
        return sb
    }

    /** Arapski original: {…} = citat ajeta u ﴿…﴾ */
    fun renderAr(ctx: Context, text: String): CharSequence {
        val sage = ContextCompat.getColor(ctx, R.color.sage)
        val sb = SpannableStringBuilder()
        val t = text.trim()
        var pos = 0
        for (m in AR_QUOTE.findAll(t)) {
            sb.append(t, pos, m.range.first)
            span(sb, "﴿" + m.groupValues[1].trim() + "﴾", ForegroundColorSpan(sage), StyleSpan(Typeface.BOLD))
            pos = m.range.last + 1
        }
        sb.append(t, pos, t.length)
        return sb
    }

    /** Bosanski tekst tefsira kao običan tekst za kopiranje */
    fun plainBs(text: String): String =
        paragraphs(text).joinToString("\n\n") { p ->
            p.replace(Regex("\\{([^{}]+)\\}")) { "„" + it.groupValues[1].trim() + "“" }
                .replace(Regex("\\[\\[([^\\]]+)\\]\\]")) { "[" + it.groupValues[1].trim() + "]" }
        }

    /** Arapski dio teksta (npr. naziv sure u izborniku) u fontu Scheherazade New */
    fun arabicSpan(ctx: Context, text: String, scale: Float = 1.15f): CharSequence {
        val sb = SpannableStringBuilder()
        val tf = arabic(ctx)
        if (tf != null) span(sb, text, TypefaceSpanCompat(tf), RelativeSizeSpan(scale)) else sb.append(text)
        return sb
    }

    private fun span(sb: SpannableStringBuilder, s: String, vararg spans: Any) {
        val st = sb.length
        sb.append(s)
        for (sp in spans) sb.setSpan(sp, st, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

/** TypefaceSpan(Typeface) postoji tek od API 28, pa koristimo vlastiti. */
class TypefaceSpanCompat(private val tf: Typeface) : MetricAffectingSpan() {
    override fun updateDrawState(tp: TextPaint) { tp.typeface = tf }
    override fun updateMeasureState(tp: TextPaint) { tp.typeface = tf }
}
