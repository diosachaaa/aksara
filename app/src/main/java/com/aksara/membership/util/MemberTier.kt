package com.aksara.membership.util

import androidx.compose.ui.graphics.Color
import com.aksara.membership.ui.theme.TierBronze
import com.aksara.membership.ui.theme.TierGold
import com.aksara.membership.ui.theme.TierSilver

/**
 * Tier keanggotaan ditentukan dari total poin yang terkumpul (sesuai PRD).
 * - Pembaca   : < 100 poin
 * - Kutu Buku : 100 - 299 poin
 * - Bibliofil : >= 300 poin
 *
 * Catatan: nama konstanta (BRONZE/SILVER/GOLD) dipertahankan agar referensi
 * warna tema tetap stabil; yang tampil ke pengguna adalah [label].
 */
enum class MemberTier(val label: String, val color: Color) {
    BRONZE("Pembaca", TierBronze),
    SILVER("Kutu Buku", TierSilver),
    GOLD("Bibliofil", TierGold);

    companion object {
        fun of(points: Int): MemberTier = when {
            points >= 300 -> GOLD
            points >= 100 -> SILVER
            else -> BRONZE
        }
    }
}
