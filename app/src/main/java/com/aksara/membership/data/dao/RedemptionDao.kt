package com.aksara.membership.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.aksara.membership.data.entity.Redemption
import kotlinx.coroutines.flow.Flow

@Dao
interface RedemptionDao {

    @Insert
    suspend fun insert(redemption: Redemption): Long

    @Query("SELECT * FROM redemptions WHERE memberId = :memberId ORDER BY date DESC")
    fun observeRedemptions(memberId: Long): Flow<List<Redemption>>
}
