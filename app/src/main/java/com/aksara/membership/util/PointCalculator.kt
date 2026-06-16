package com.aksara.membership.util

/**
 * Aturan reward point: setiap Rp10.000 = 1 poin.
 * Contoh: pembelian Rp150.000 -> 150000 / 10000 = 15 poin.
 */
object PointCalculator {
    const val RUPIAH_PER_POINT = 10_000L

    fun calculate(amount: Long): Int = (amount / RUPIAH_PER_POINT).toInt()
}
