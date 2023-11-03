package com.binay.recipeapp.data.local.favoriteDb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.binay.recipeapp.data.model.RecipeData

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecipe(recipes: RecipeData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllRecipes(recipes: List<RecipeData>)

    @Query("Select * From RecipeData")
    fun getAllRecipes(): List<RecipeData>

    @Query("Select * From RecipeData WHERE id = :recipeId")
    fun getRecipe(recipeId: Int): RecipeData?

    @Delete
    suspend fun removeRecipe(recipe: RecipeData)

}