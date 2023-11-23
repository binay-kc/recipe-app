package com.binay.recipeapp.data.local.ingredientDb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.binay.recipeapp.data.model.ExtendedIngredients
import com.binay.recipeapp.data.model.RecipeData

@Dao
interface IngredientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addIngredient(ingredient: ExtendedIngredients)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAllIngredients(ingredients: List<ExtendedIngredients>)

    @Query("Select * From ingredients")
    fun getAllIngredients(): List<ExtendedIngredients>

    @Query("Select * From ingredients WHERE id = :ingredientId")
    fun getIngredientData(ingredientId: Int): ExtendedIngredients

    @Delete
    suspend fun removeIngredient(ingredient: ExtendedIngredients)
}