package com.binay.recipeapp.data.repository.local

import com.binay.recipeapp.data.local.randomRecipeDb.RandomRecipeDao
import com.binay.recipeapp.data.local.recipesDb.RecipeDao
import com.binay.recipeapp.data.model.RecipeResponseData
import javax.inject.Inject

class LocalRepo @Inject constructor(
    private val randomRecipeDao: RandomRecipeDao,
    private val recipeDao: RecipeDao
) {

    suspend fun getRandomRecipe(): RecipeResponseData? {
        val randomRecipe = randomRecipeDao.getRandomRecipe()
        if (randomRecipe != null) {
            return RecipeResponseData(arrayListOf(randomRecipe))
        }
        return null
    }

    suspend fun getRecipesByTag(tag: String): RecipeResponseData? {
        val recipes = recipeDao.getRecipes(tag)
        if (recipes != null) return RecipeResponseData(ArrayList(recipes))
        return null
    }
}