package com.binay.recipeapp.data.local.recipesDb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.binay.recipeapp.data.model.RecipeData

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAllRecipes(recipes: List<RecipeData>)

    @Query("Select * From recipes WHERE tagToBeSearchedBy = :tag")
    fun getRecipes(tag: String): List<RecipeData>?

    @Query("Select * From recipes WHERE id = :recipeId")
    fun getRecipe(recipeId: Int): RecipeData?

    @Query("Delete FROM recipes WHERE id in (:recipeIds)")
    fun removePreviousRecipes(recipeIds: List<Int>)
}