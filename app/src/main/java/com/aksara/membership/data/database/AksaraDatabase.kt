package com.aksara.membership.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aksara.membership.data.dao.MemberDao
import com.aksara.membership.data.dao.ProductDao
import com.aksara.membership.data.dao.RedemptionDao
import com.aksara.membership.data.dao.RewardDao
import com.aksara.membership.data.dao.TransactionDao
import com.aksara.membership.data.entity.Member
import com.aksara.membership.data.entity.Product
import com.aksara.membership.data.entity.Redemption
import com.aksara.membership.data.entity.Reward
import com.aksara.membership.data.entity.Transaction

@Database(
    entities = [Member::class, Transaction::class, Reward::class, Redemption::class, Product::class],
    version = 5,
    exportSchema = false
)
abstract class AksaraDatabase : RoomDatabase() {

    abstract fun memberDao(): MemberDao
    abstract fun transactionDao(): TransactionDao
    abstract fun rewardDao(): RewardDao
    abstract fun redemptionDao(): RedemptionDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AksaraDatabase? = null

        fun getInstance(context: Context): AksaraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AksaraDatabase::class.java,
                    "aksara.db"
                )
                    .addCallback(SeedCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Mengisi data reward & katalog produk. Memakai SQL mentah agar tetap jalan
     * baik saat database baru dibuat (onCreate) maupun saat dibuat ulang karena
     * perubahan versi (onDestructiveMigration) -> reward dijamin selalu muncul.
     */
    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            seed(db)
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            seed(db)
        }

        private fun seed(db: SupportSQLiteDatabase) {
          try {
            // Bersihkan dulu agar idempotent (tidak dobel)
            db.execSQL("DELETE FROM rewards")
            db.execSQL("DELETE FROM products")

            val rewards = listOf(
                Triple("Pembatas Buku Eksklusif", 50, "Tukar poin Anda dengan pembatas buku desain eksklusif Aksara."),
                Triple("Tote Bag Aksara", 100, "Tote bag kanvas untuk membawa koleksi buku kesayangan Anda."),
                Triple("Voucher Buku Gratis", 150, "Voucher satu buku gratis pilihan Anda di Toko Buku Aksara."),
                Triple("Diskon 50% Buku", 250, "Potongan 50% untuk satu buku non-promo pilihan Anda."),
                Triple("Merchandise Eksklusif", 400, "Paket merchandise spesial untuk member setia Aksara.")
            )
            rewards.forEach { (name, cost, desc) ->
                db.execSQL(
                    "INSERT INTO rewards (name, pointCost, description) VALUES (?, ?, ?)",
                    arrayOf<Any>(name, cost, desc)
                )
            }

            val products = listOf(
                arrayOf<Any>("BUKU", "Laut Bercerita", "Leila S. Chudori", 98000L, "#34386B", "buku_laut"),
                arrayOf<Any>("BUKU", "Bumi Manusia", "Pramoedya A. Toer", 102000L, "#6D2E46", "buku_bumi"),
                arrayOf<Any>("BUKU", "Filosofi Teras", "Henry Manampiring", 88000L, "#2E6D5B", "buku_filosofi"),
                arrayOf<Any>("BUKU", "Atomic Habits", "James Clear", 110000L, "#B07A3C", "buku_atomic"),
                arrayOf<Any>("BUKU", "Hujan", "Tere Liye", 79000L, "#455A8C", "buku_hujan"),
                arrayOf<Any>("BUKU", "Sapiens", "Yuval N. Harari", 135000L, "#7A3C6D", "buku_sapiens"),
                arrayOf<Any>("ATK", "Pulpen Gel 0.5 (3 pcs)", "Tinta Hitam", 24000L, "#5A5F99", "atk_pulpen"),
                arrayOf<Any>("ATK", "Buku Tulis 100 lbr", "Sinar Dunia", 15000L, "#8A8D9A", "atk_bukutulis"),
                arrayOf<Any>("ATK", "Sticky Notes Pastel", "5 warna", 18000L, "#C9A227", "atk_sticky"),
                arrayOf<Any>("ATK", "Spidol Marker", "Snowman", 12000L, "#455A8C", "atk_spidol"),
                arrayOf<Any>("ATK", "Highlighter Set", "4 warna", 28000L, "#2E6D5B", "atk_highlighter"),
                arrayOf<Any>("KAFE", "Kopi Susu Aksara", "Signature", 22000L, "#6D4C41", "kafe_kopisusu"),
                arrayOf<Any>("KAFE", "Americano", "Hot / Ice", 20000L, "#4E342E", "kafe_americano"),
                arrayOf<Any>("KAFE", "Matcha Latte", "Premium", 28000L, "#2E6D5B", "kafe_matcha"),
                arrayOf<Any>("KAFE", "Croissant Butter", "Fresh baked", 18000L, "#B07A3C", "kafe_croissant"),
                arrayOf<Any>("KAFE", "Cookies Coklat", "Per pcs", 12000L, "#8C5A3C", "kafe_cookies"),
                arrayOf<Any>("LAINNYA", "Tote Bag Kanvas", "Merchandise", 45000L, "#34386B", "lain_totebag"),
                arrayOf<Any>("LAINNYA", "Pembatas Buku Set", "5 pcs", 20000L, "#C9A227", "lain_pembatas"),
                arrayOf<Any>("LAINNYA", "Gift Card Aksara", "Voucher", 50000L, "#455A8C", "lain_giftcard")
            )
            products.forEach { p ->
                db.execSQL(
                    "INSERT INTO products (category, name, author, price, colorHex, imageKey) VALUES (?, ?, ?, ?, ?, ?)",
                    p
                )
            }
          } catch (e: Exception) {
            // Jangan sampai gagal seeding menjatuhkan aplikasi.
            android.util.Log.e("AksaraDatabase", "Seed gagal: ${e.message}", e)
          }
        }
    }
}
