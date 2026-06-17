package com.aksara.membership.util

import com.aksara.membership.R

/**
 * Memetakan [com.aksara.membership.data.entity.Product.imageKey] ke resource drawable
 * gambar produk. Setiap produk punya gambar berbeda (sampul buku, pensil, kopi, dll).
 * Jika key tidak dikenal, kembalikan null agar pemanggil bisa memakai fallback ikon kategori.
 */
fun productImageRes(imageKey: String): Int? = when (imageKey) {
    // Buku
    "buku_laut" -> R.drawable.prod_buku_laut
    "buku_bumi" -> R.drawable.prod_buku_bumi
    "buku_filosofi" -> R.drawable.prod_buku_filosofi
    "buku_atomic" -> R.drawable.prod_buku_atomic
    "buku_hujan" -> R.drawable.prod_buku_hujan
    "buku_sapiens" -> R.drawable.prod_buku_sapiens
    // Alat tulis
    "atk_pulpen" -> R.drawable.prod_atk_pulpen
    "atk_bukutulis" -> R.drawable.prod_atk_bukutulis
    "atk_sticky" -> R.drawable.prod_atk_sticky
    "atk_spidol" -> R.drawable.prod_atk_spidol
    "atk_highlighter" -> R.drawable.prod_atk_highlighter
    // Kafe
    "kafe_kopisusu" -> R.drawable.prod_kafe_kopisusu
    "kafe_americano" -> R.drawable.prod_kafe_americano
    "kafe_matcha" -> R.drawable.prod_kafe_matcha
    "kafe_croissant" -> R.drawable.prod_kafe_croissant
    "kafe_cookies" -> R.drawable.prod_kafe_cookies
    // Lainnya
    "lain_totebag" -> R.drawable.prod_lain_totebag
    "lain_pembatas" -> R.drawable.prod_lain_pembatas
    "lain_giftcard" -> R.drawable.prod_lain_giftcard
    else -> null
}
