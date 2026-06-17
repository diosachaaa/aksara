package com.aksara.membership.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Item yang bisa dibeli (buku, alat tulis, menu kafe, dll). */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,   // BUKU / ATK / KAFE / LAINNYA
    val name: String,
    val author: String,     // penulis (buku) / keterangan singkat
    val price: Long,
    val colorHex: String,   // warna sampul placeholder, contoh "#34386B"
    val imageKey: String = ""  // kunci gambar produk, dipetakan ke drawable (lihat ProductImage)
)
