package com.aksara.membership.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberNumber: String = "",
    val name: String,
    val email: String,
    val phone: String,
    val status: String = "Active",
    val totalPoints: Int = 0,
    val joinDate: Long = System.currentTimeMillis()
)
