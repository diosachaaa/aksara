package com.aksara.membership.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

/** Kategori transaksi pada toko buku Aksara. */
enum class TransactionCategory(val key: String, val label: String, val icon: ImageVector) {
    BUKU("BUKU", "Buku", Icons.Filled.AutoStories),
    ATK("ATK", "Alat Tulis", Icons.Filled.Edit),
    KAFE("KAFE", "Kafe", Icons.Filled.LocalCafe),
    LAINNYA("LAINNYA", "Lainnya", Icons.Filled.ShoppingBag);

    companion object {
        fun fromKey(key: String): TransactionCategory =
            entries.firstOrNull { it.key == key } ?: LAINNYA
    }
}
