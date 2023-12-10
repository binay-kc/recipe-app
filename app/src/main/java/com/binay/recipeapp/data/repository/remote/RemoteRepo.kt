package com.binay.recipeapp.data.repository.remote

import com.binay.recipeapp.data.api.ApiHelper
import com.binay.recipeapp.data.local.favoriteDb.AppDatabase
import com.binay.recipeapp.data.model.RecipeResponseData

class RemoteRepo(private val apiHelper: ApiHelper, private val mDatabase: AppDatabase) {

    suspend fun getRandomRecipe(): RecipeResponseData {
        val randomRecipeData = apiHelper.getRandomRecipe()
        if (randomRecipeData.recipes.isNotEmpty()) {
            val newRandomRecipe = randomRecipeData.recipes[0]
            newRandomRecipe.isRandom = true
            val randomDao = mDatabase.randomRecipeDao()
//                    Fetch previous random recipe
            val previousRandomRecipe = randomDao.getRandomRecipe()
            if (previousRandomRecipe != null) randomDao.removeRandomRecipe(previousRandomRecipe)
            randomDao.addRandomRecipe(newRandomRecipe)
        }
        return randomRecipeData
    }

}