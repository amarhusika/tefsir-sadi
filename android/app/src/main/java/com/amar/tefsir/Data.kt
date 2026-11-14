package com.amar.tefsir

import android.content.Context
import org.json.JSONObject
import java.util.Locale

/** Jedan ajet iz assets/data/sura_XXX.json (isti format kao public/data na webu). */
data class Ajet(
    val arapski: String,
    val prevod: String,
    val tefsirAr: String,
    val tefsirBs: String,
    val vidi: List<String>,
    val grupa: Pair<Int, Int>?,
)

class SuraFile(val nazivAr: String, val nazivBos: String, val ajeti: JSONObject)

class DataException(message: String) : Exception(message)

object TefsirRepo {
    // nekoliko posljednjih sura u memoriji, da listanje bude trenutno
    private val cache = object : LinkedHashMap<Int, SuraFile>(4, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, SuraFile>?) = size > 3
    }

    @Synchronized
    fun isCached(sura: Int) = cache.containsKey(sura)

    @Synchronized
    fun loadSura(ctx: Context, sura: Int): SuraFile {
        cache[sura]?.let { return it }
        val name = String.format(Locale.ROOT, "data/sura_%03d.json", sura)
        val text = try {
            ctx.assets.open(name).bufferedReader(Charsets.UTF_8).use { it.readText() }
        } catch (e: Exception) {
            throw DataException("Fajl nije pronađen")
        }
        val j = JSONObject(text)
        val f = SuraFile(str(j, "naziv_ar"), str(j, "naziv_bos"), j.optJSONObject("ajeti") ?: JSONObject())
        cache[sura] = f
        return f
    }

    fun ajet(file: SuraFile, sura: Int, ayah: Int): Ajet {
        val key = "$sura:$ayah"
        val o = file.ajeti.optJSONObject(key) ?: throw DataException("Ajet $key nije u bazi")
        val vidi = mutableListOf<String>()
        o.optJSONArray("vidi")?.let { arr -> for (i in 0 until arr.length()) vidi.add(arr.optString(i)) }
        val grupa = o.optJSONArray("grupa")?.let { arr ->
            if (arr.length() >= 2) arr.optInt(0) to arr.optInt(1) else null
        }
        return Ajet(str(o, "arapski"), str(o, "prevod"), str(o, "tefsir_ar"), str(o, "tefsir_bs"), vidi, grupa)
    }

    private fun str(o: JSONObject, k: String) = if (o.isNull(k)) "" else o.optString(k, "")
}

object Prefs {
    private fun sp(c: Context) = c.getSharedPreferences("tefsir", Context.MODE_PRIVATE)

    fun lastRef(c: Context): Pair<Int, Int>? = Quran.parseRef(sp(c).getString("last_ref", null))
    fun setLastRef(c: Context, s: Int, a: Int) = sp(c).edit().putString("last_ref", "$s:$a").apply()

    fun nightMode(c: Context) =
        sp(c).getInt("night_mode", androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    fun setNightMode(c: Context, mode: Int) = sp(c).edit().putInt("night_mode", mode).apply()

    fun showAr(c: Context) = sp(c).getBoolean("show_ar", false)
    fun setShowAr(c: Context, v: Boolean) = sp(c).edit().putBoolean("show_ar", v).apply()
}
