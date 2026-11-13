package com.amar.tefsir

/** Broj ajeta i nazivi sura (preuzeto iz web verzije, src/constants.ts). */
object Quran {
    val AYAH_COUNT = intArrayOf(0, 7, 286, 200, 176, 120, 165, 206, 75, 129, 109, 123, 111, 43, 52, 99, 128, 111, 110, 98, 135, 112, 78, 118, 64, 77, 227, 93, 88, 69, 60, 34, 30, 73, 54, 45, 83, 182, 88, 75, 85, 54, 53, 89, 59, 37, 35, 38, 29, 18, 45, 60, 49, 62, 55, 78, 96, 29, 22, 24, 13, 14, 11, 11, 18, 12, 12, 30, 52, 52, 44, 28, 28, 20, 56, 40, 31, 50, 40, 46, 42, 29, 19, 36, 25, 22, 17, 19, 26, 30, 20, 15, 21, 11, 8, 8, 19, 5, 8, 8, 11, 11, 8, 3, 9, 5, 4, 7, 3, 6, 3, 5, 4, 5, 6)
    val NAME_AR = arrayOf("", "الفاتحة", "البقرة", "آل عمران", "النساء", "المائدة", "الأنعام", "الأعراف", "الأنفال", "التوبة", "يونس", "هود", "يوسف", "الرعد", "إبراهيم", "الحجر", "النحل", "الإسراء", "الكهف", "مريم", "طه", "الأنبياء", "الحج", "المؤمنون", "النور", "الفرقان", "الشعراء", "النمل", "القصص", "العنكبوت", "الروم", "لقمان", "السجدة", "الأحزاب", "سبأ", "فاطر", "يس", "الصافات", "ص", "الزمر", "غافر", "فصلت", "الشورى", "الزخرف", "الدخان", "الجاثية", "الأحقاف", "محمد", "الفتح", "الحجرات", "ق", "الذاريات", "الطور", "النجم", "القمر", "الرحمن", "الواقعة", "الحديد", "المجادلة", "الحشر", "الممتحنة", "الصف", "الجمعة", "المنافقون", "التغابن", "الطلاق", "التحريم", "الملك", "القلم", "الحاقة", "المعارج", "نوح", "الجن", "المزمل", "المدثر", "القيامة", "الإنسان", "المرسلات", "النبأ", "النازعات", "عبس", "التكوير", "الانفطار", "المطففين", "الانشقاق", "البروج", "الطارق", "الأعلى", "الغاشية", "الفجر", "البلد", "الشمس", "الليل", "الضحى", "الشرح", "التين", "العلق", "القدر", "البينة", "الزلزلة", "العاديات", "القارعة", "التكاثر", "العصر", "الهمزة", "الفيل", "قريش", "الماعون", "الكوثر", "الكافرون", "النصر", "المسد", "الإخلاص", "الفلق", "الناس")
    val NAME_BS = arrayOf("", "El-Fatiha", "El-Bekare", "Alu Imran", "En-Nisa", "El-Maide", "El-En'am", "El-A'raf", "El-Enfal", "Et-Tevbe", "Junus", "Hud", "Jusuf", "Er-Ra'd", "Ibrahim", "El-Hidžr", "En-Nahl", "El-Isra", "El-Kehf", "Merjem", "Ta-Ha", "El-Enbija", "El-Hadždž", "El-Mu'minun", "En-Nur", "El-Furkan", "Eš-Šuara", "En-Neml", "El-Kasas", "El-Ankebut", "Er-Rum", "Lukman", "Es-Sedžde", "El-Ahzab", "Sebe", "Fatir", "Ja-Sin", "Es-Saffat", "Sad", "Ez-Zumer", "Gafir", "Fussilet", "Eš-Šura", "Ez-Zuhruf", "Ed-Duhan", "El-Džasije", "El-Ahkaf", "Muhammed", "El-Feth", "El-Hudžurat", "Kaf", "Ez-Zarijat", "Et-Tur", "En-Nedžm", "El-Kamer", "Er-Rahman", "El-Vakia", "El-Hadid", "El-Mudžadele", "El-Hašr", "El-Mumtehine", "Es-Saff", "El-Džumu'a", "El-Munafikun", "Et-Tegabun", "Et-Talak", "Et-Tahrim", "El-Mulk", "El-Kalem", "El-Hakka", "El-Me'aridž", "Nuh", "El-Džinn", "El-Muzzemmil", "El-Muddessir", "El-Kijame", "El-Insan", "El-Murselat", "En-Nebe", "En-Nazi'at", "Abese", "Et-Tekvir", "El-Infitar", "El-Mutaffifin", "El-Inšikak", "El-Burudž", "Et-Tarik", "El-A'la", "El-Gašije", "El-Fedžr", "El-Beled", "Eš-Šems", "El-Lejl", "Ed-Duha", "El-Inširah", "Et-Tin", "El-Alek", "El-Kadr", "El-Bejjine", "Ez-Zelzele", "El-Adijat", "El-Kari'a", "Et-Tekasur", "El-Asr", "El-Humeze", "El-Fil", "Kurejš", "El-Maun", "El-Kevser", "El-Kafirun", "En-Nasr", "El-Mesed", "El-Ihlas", "El-Felek", "En-Nas")

    fun isValid(s: Int, a: Int) = s in 1..114 && a in 1..AYAH_COUNT[s]

    fun prev(s: Int, a: Int): Pair<Int, Int>? = when {
        a > 1 -> s to a - 1
        s > 1 -> (s - 1) to AYAH_COUNT[s - 1]
        else -> null
    }

    fun next(s: Int, a: Int): Pair<Int, Int>? = when {
        a < AYAH_COUNT[s] -> s to a + 1
        s < 114 -> (s + 1) to 1
        else -> null
    }

    /** "2:255" ili "2 255" -> (2, 255), ako je ajet ispravan */
    fun parseRef(text: String?): Pair<Int, Int>? {
        val m = Regex("^\\s*(\\d+)\\s*[:\\s]\\s*(\\d+)\\s*$").find(text ?: return null) ?: return null
        val s = m.groupValues[1].toIntOrNull() ?: return null
        val a = m.groupValues[2].toIntOrNull() ?: return null
        return if (isValid(s, a)) s to a else null
    }
}
