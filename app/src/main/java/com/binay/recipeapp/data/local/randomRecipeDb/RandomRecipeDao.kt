package com.binay.recipeapp.data.local.randomRecipeDb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.binay.recipeapp.data.model.RecipeData

@Dao
interface RandomRecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRandomRecipe(recipe: RecipeData)

    @Query("Select * From recipes WHERE isRandom = 1 limit 1")
    suspend fun getRandomRecipe(): RecipeData?

    @Delete
    suspend fun removeRandomRecipe(recipe: RecipeData)

}