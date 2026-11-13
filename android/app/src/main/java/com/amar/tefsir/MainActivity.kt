package com.amar.tefsir

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/** Početna: nastavi čitati, odaberi ajet, o tefsiru */
class MainActivity : AppCompatActivity() {

    private lateinit var continueCard: View
    private lateinit var ccRef: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupEdgeToEdge()
        setContentView(R.layout.activity_main)

        val scroll = findViewById<View>(R.id.scroll)
        val content = findViewById<View>(R.id.content)
        applySystemInsets(scroll, content, content)

        continueCard = findViewById(R.id.continueCard)
        ccRef = findViewById(R.id.ccRef)

        findViewById<AyahNavigatorView>(R.id.navigator).onNavigate = { s, a -> open(s, a) }
        continueCard.setOnClickListener {
            Prefs.lastRef(this)?.let { (s, a) -> open(s, a) }
        }
    }

    override fun onResume() {
        super.onResume()
        val last = Prefs.lastRef(this)
        if (last != null) {
            val (s, a) = last
            ccRef.text = "${Quran.NAME_BS[s]} · $s:$a"
            continueCard.visibility = View.VISIBLE
        } else {
            continueCard.visibility = View.GONE
        }
    }

    private fun open(s: Int, a: Int) {
        startActivity(TafsirActivity.intent(this, s, a))
    }
}
