package com.aksara.membership.util

/**
 * Daftar kategori buku yang tersedia di Toko Buku Aksara.
 * Sesuai PRD: Fiksi, Non-Fiksi, Komik, Akademik.
 */
object BookCategory {
    val ALL = listOf("Fiksi", "Non-Fiksi", "Komik", "Akademik")
    val DEFAULT = ALL.first()
}
