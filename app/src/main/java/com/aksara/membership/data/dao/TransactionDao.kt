package com.aksara.membership.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.aksara.membership.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Query("SELECT * FROM transactions WHERE memberId = :memberId ORDER BY date DESC")
    fun observeTransactions(memberId: Long): Flow<List<Transaction>>
}
