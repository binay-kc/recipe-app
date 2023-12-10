package com.binay.recipeapp.data.repository.local

import com.binay.recipeapp.data.local.favoriteDb.AppDatabase
import com.binay.recipeapp.data.model.RecipeResponseData

class LocalRepo(private val mDatabase: AppDatabase) {

    fun getRandomRecipe(): RecipeResponseData? {
        val randomRecipe = mDatabase.randomRecipeDao().getRandomRecipe()
        if (randomRecipe != null) {
            return RecipeResponseData(arrayListOf(randomRecipe))
        }
        return null
    }
}