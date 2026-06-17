package com.aksara.membership.data

import com.aksara.membership.data.entity.Reward

/**
 * Daftar reward awal toko Aksara (sumber tunggal kebenaran).
 * Dipakai untuk mengisi daftar reward saat tabel rewards masih kosong,
 * agar reward selalu muncul walau seeding callback database gagal.
 */
object RewardSeed {

    val rewards: List<Reward> = listOf(
        Reward(name = "Pembatas Buku Eksklusif", pointCost = 50, description = "Tukar poin Anda dengan pembatas buku desain eksklusif Aksara."),
        Reward(name = "Tote Bag Aksara", pointCost = 100, description = "Tote bag kanvas untuk membawa koleksi buku kesayangan Anda."),
        Reward(name = "Voucher Buku Gratis", pointCost = 150, description = "Voucher satu buku gratis pilihan Anda di Toko Buku Aksara."),
        Reward(name = "Diskon 50% Buku", pointCost = 250, description = "Potongan 50% untuk satu buku non-promo pilihan Anda."),
        Reward(name = "Merchandise Eksklusif", pointCost = 400, description = "Paket merchandise spesial untuk member setia Aksara.")
    )
}
