package com.aksara.membership.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.aksara.membership.data.entity.Member
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {

    @Insert
    suspend fun insert(member: Member): Long

    @Update
    suspend fun update(member: Member)

    @Query("SELECT * FROM members WHERE id = :id")
    fun observeMember(id: Long): Flow<Member?>

    @Query("SELECT * FROM members WHERE id = :id")
    suspend fun getMemberById(id: Long): Member?

    @Query("SELECT * FROM members WHERE email = :email LIMIT 1")
    suspend fun getMemberByEmail(email: String): Member?

    @Query("SELECT COUNT(*) FROM members WHERE email = :email")
    suspend fun countByEmail(email: String): Int
}
