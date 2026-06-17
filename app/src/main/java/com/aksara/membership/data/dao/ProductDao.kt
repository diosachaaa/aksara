package com.aksara.membership.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.aksara.membership.data.entity.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name")
    fun observeAll(): Flow<List<Product>>
}
