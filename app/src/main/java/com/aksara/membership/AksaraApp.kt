package com.aksara.membership

import android.app.Application
import com.aksara.membership.data.database.AksaraDatabase
import com.aksara.membership.data.repository.AksaraRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AksaraApp : Application() {

    private val database by lazy { AksaraDatabase.getInstance(this) }

    val repository by lazy {
        AksaraRepository(
            memberDao = database.memberDao(),
            transactionDao = database.transactionDao(),
            rewardDao = database.rewardDao(),
            redemptionDao = database.redemptionDao(),
            productDao = database.productDao()
        )
    }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Jaminan: jika katalog kosong (database lama / seeding callback gagal),
        // isi ulang saat aplikasi mulai sehingga daftar barang selalu muncul.
        appScope.launch {
            runCatching { repository.ensureCatalogSeeded() }
            runCatching { repository.ensureRewardsSeeded() }
        }
    }
}
