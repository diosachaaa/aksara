package com.aksara.membership.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rewards")
data class Reward(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val pointCost: Int,
    val description: String
)
