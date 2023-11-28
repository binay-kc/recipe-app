package com.binay.recipeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.binay.recipeapp.data.model.WebsiteData

@Dao
interface WebsiteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(website: List<WebsiteData>)

    @Query("SELECT * FROM websites")
    fun getAll(): List<WebsiteData>
}