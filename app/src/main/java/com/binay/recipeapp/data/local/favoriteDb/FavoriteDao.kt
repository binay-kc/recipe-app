package com.binay.recipeapp.data.local.favoriteDb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.binay.recipeapp.data.model.RecipeData

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRecipe(recipe: RecipeData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAllRecipes(recipes: List<RecipeData>)

    @Query("Select * From recipes WHERE isFavorite = 1")
    fun getAllRecipes(): List<RecipeData>

    @Query("Select * From recipes WHERE id = :recipeId AND isFavorite = 1")
    fun getRecipe(recipeId: Int): RecipeData?

    @Update
    suspend fun removeRecipeFromFavorite(recipe: RecipeData)

}