package com.aksara.membership

import android.app.Application
import com.aksara.membership.data.database.AksaraDatabase
import com.aksara.membership.data.repository.AksaraRepository

/**
 * Application class sederhana sebagai "container" dependency.
 * Database & repository dibuat sekali dan dipakai di seluruh aplikasi.
 */
class AksaraApp : Application() {

    private val database by lazy { AksaraDatabase.getInstance(this) }

    val repository by lazy {
        AksaraRepository(
            memberDao = database.memberDao(),
            transactionDao = database.transactionDao(),
            rewardDao = database.rewardDao()
        )
    }
}
