package com.aksara.membership.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aksara.membership.data.entity.Reward
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(rewards: List<Reward>)

    @Query("SELECT * FROM rewards ORDER BY pointCost ASC")
    fun observeRewards(): Flow<List<Reward>>

    @Query("SELECT COUNT(*) FROM rewards")
    suspend fun count(): Int
}
