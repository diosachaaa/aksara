package com.aksara.membership.util

import androidx.compose.ui.graphics.Color
import com.aksara.membership.ui.theme.TierBronze
import com.aksara.membership.ui.theme.TierGold
import com.aksara.membership.ui.theme.TierSilver

/**
 * Tier keanggotaan dari total poin.
 * Bronze (<100) -> Silver (100-299) -> Gold (>=300).
 */
enum class MemberTier(val label: String, val color: Color, val threshold: Int) {
    BRONZE("Bronze", TierBronze, 0),
    SILVER("Silver", TierSilver, 100),
    GOLD("Gold", TierGold, 300);

    companion object {
        fun of(points: Int): MemberTier = when {
            points >= GOLD.threshold -> GOLD
            points >= SILVER.threshold -> SILVER
            else -> BRONZE
        }

        /** Poin yang dibutuhkan menuju tier berikutnya, null jika sudah Gold. */
        fun pointsToNext(points: Int): Int? = when (of(points)) {
            BRONZE -> SILVER.threshold - points
            SILVER -> GOLD.threshold - points
            GOLD -> null
        }

        /** Progress 0f..1f dalam tier saat ini. */
        fun progress(points: Int): Float = when (of(points)) {
            BRONZE -> points / SILVER.threshold.toFloat()
            SILVER -> (points - SILVER.threshold) / (GOLD.threshold - SILVER.threshold).toFloat()
            GOLD -> 1f
        }.coerceIn(0f, 1f)
    }
}
