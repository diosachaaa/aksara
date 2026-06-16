package com.aksara.membership.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Memformat angka menjadi format Rupiah, contoh: 150000 -> "Rp150.000". */
fun Long.toRupiah(): String {
    val formatted = "%,d".format(this).replace(',', '.')
    return "Rp$formatted"
}

/** Memformat timestamp menjadi tanggal Indonesia, contoh: "20 Mei 2024". */
fun Long.toIndoDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    return sdf.format(Date(this))
}
