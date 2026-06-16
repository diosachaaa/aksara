package com.aksara.membership.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aksara.membership.data.dao.MemberDao
import com.aksara.membership.data.dao.RewardDao
import com.aksara.membership.data.dao.TransactionDao
import com.aksara.membership.data.entity.Member
import com.aksara.membership.data.entity.Reward
import com.aksara.membership.data.entity.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Member::class, Transaction::class, Reward::class],
    version = 2,
    exportSchema = false
)
abstract class AksaraDatabase : RoomDatabase() {

    abstract fun memberDao(): MemberDao
    abstract fun transactionDao(): TransactionDao
    abstract fun rewardDao(): RewardDao

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

    /** Mengisi data reward default saat database pertama kali dibuat. */
    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    database.rewardDao().insertAll(
                        listOf(
                            Reward(name = "Pembatas Buku Eksklusif", pointCost = 30, description = "Tukar poin Anda dengan pembatas buku desain eksklusif Aksara."),
                            Reward(name = "Voucher Diskon Rp15.000", pointCost = 50, description = "Potongan Rp15.000 untuk pembelian buku berikutnya."),
                            Reward(name = "Tote Bag Aksara", pointCost = 100, description = "Tote bag kanvas untuk membawa koleksi buku kesayangan Anda."),
                            Reward(name = "Voucher Buku Gratis", pointCost = 150, description = "Voucher satu buku gratis (s.d. Rp75.000) pilihan Anda di Toko Buku Aksara.")
                        )
                    )
                }
            }
        }
    }
}
