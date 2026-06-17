package com.aksara.membership.data

import com.aksara.membership.data.entity.Product

/**
 * Daftar produk awal toko Aksara (sumber tunggal kebenaran).
 * Dipakai untuk mengisi katalog saat tabel produk masih kosong.
 * Setiap produk punya [Product.imageKey] yang dipetakan ke gambar lewat
 * [com.aksara.membership.util.productImageRes].
 */
object CatalogSeed {

    val products: List<Product> = listOf(
        // ---------- BUKU ----------
        Product(category = "BUKU", name = "Laut Bercerita", author = "Leila S. Chudori", price = 98000L, colorHex = "#34386B", imageKey = "buku_laut"),
        Product(category = "BUKU", name = "Bumi Manusia", author = "Pramoedya A. Toer", price = 102000L, colorHex = "#6D2E46", imageKey = "buku_bumi"),
        Product(category = "BUKU", name = "Filosofi Teras", author = "Henry Manampiring", price = 88000L, colorHex = "#2E6D5B", imageKey = "buku_filosofi"),
        Product(category = "BUKU", name = "Atomic Habits", author = "James Clear", price = 110000L, colorHex = "#B07A3C", imageKey = "buku_atomic"),
        Product(category = "BUKU", name = "Hujan", author = "Tere Liye", price = 79000L, colorHex = "#455A8C", imageKey = "buku_hujan"),
        Product(category = "BUKU", name = "Sapiens", author = "Yuval N. Harari", price = 135000L, colorHex = "#7A3C6D", imageKey = "buku_sapiens"),
        // ---------- ALAT TULIS ----------
        Product(category = "ATK", name = "Pulpen Gel 0.5 (3 pcs)", author = "Tinta Hitam", price = 24000L, colorHex = "#5A5F99", imageKey = "atk_pulpen"),
        Product(category = "ATK", name = "Buku Tulis 100 lbr", author = "Sinar Dunia", price = 15000L, colorHex = "#8A8D9A", imageKey = "atk_bukutulis"),
        Product(category = "ATK", name = "Sticky Notes Pastel", author = "5 warna", price = 18000L, colorHex = "#C9A227", imageKey = "atk_sticky"),
        Product(category = "ATK", name = "Spidol Marker", author = "Snowman", price = 12000L, colorHex = "#455A8C", imageKey = "atk_spidol"),
        Product(category = "ATK", name = "Highlighter Set", author = "4 warna", price = 28000L, colorHex = "#2E6D5B", imageKey = "atk_highlighter"),
        // ---------- KAFE ----------
        Product(category = "KAFE", name = "Kopi Susu Aksara", author = "Signature", price = 22000L, colorHex = "#6D4C41", imageKey = "kafe_kopisusu"),
        Product(category = "KAFE", name = "Americano", author = "Hot / Ice", price = 20000L, colorHex = "#4E342E", imageKey = "kafe_americano"),
        Product(category = "KAFE", name = "Matcha Latte", author = "Premium", price = 28000L, colorHex = "#2E6D5B", imageKey = "kafe_matcha"),
        Product(category = "KAFE", name = "Croissant Butter", author = "Fresh baked", price = 18000L, colorHex = "#B07A3C", imageKey = "kafe_croissant"),
        Product(category = "KAFE", name = "Cookies Coklat", author = "Per pcs", price = 12000L, colorHex = "#8C5A3C", imageKey = "kafe_cookies"),
        // ---------- LAINNYA ----------
        Product(category = "LAINNYA", name = "Tote Bag Kanvas", author = "Merchandise", price = 45000L, colorHex = "#34386B", imageKey = "lain_totebag"),
        Product(category = "LAINNYA", name = "Pembatas Buku Set", author = "5 pcs", price = 20000L, colorHex = "#C9A227", imageKey = "lain_pembatas"),
        Product(category = "LAINNYA", name = "Gift Card Aksara", author = "Voucher", price = 50000L, colorHex = "#455A8C", imageKey = "lain_giftcard")
    )
}
