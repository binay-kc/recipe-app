package com.binay.recipeapp.data.repository

import android.content.Context
import com.binay.recipeapp.data.api.ApiHelper
import com.binay.recipeapp.data.local.favoriteDb.AppDatabase
import com.binay.recipeapp.data.model.RecipeResponseData
import com.binay.recipeapp.util.NetworkUtil

class MainRepository(private val apiHelper: ApiHelper, private val mContext : Context, private val mDatabase : AppDatabase) {

    // TODO: Remove this and use as per required
    suspend fun getRecipes(tag: String) = apiHelper.getData(tag)

    suspend fun getRecipeDetail(id: Int) = apiHelper.getRecipeDetail(id)

    suspend fun searchRecipes(query: String) = apiHelper.searchRecipes(query)

    suspend fun searchRecipesByIngredients(query: String) =
        apiHelper.searchRecipesByIngredients(query)

    suspend fun getRandomRecipe() : RecipeResponseData{
        if(!NetworkUtil.isNetworkAvailable(mContext)){
            val randomRecipe = mDatabase.randomRecipeDao().getRandomRecipe()
            if (randomRecipe!=null) {
                return RecipeResponseData(arrayListOf(randomRecipe))
            }
        }
        val randomRecipeData = apiHelper.getRandomRecipe()
        if (randomRecipeData.recipes.isNotEmpty()) {
            val newRandomRecipe = randomRecipeData.recipes[0]
            newRandomRecipe.isRandom = true
            val randomDao = mDatabase.randomRecipeDao()
//                    Fetch previous random recipe
            val previousRandomRecipe = randomDao.getRandomRecipe()
            if (previousRandomRecipe!=null) randomDao.removeRandomRecipe(previousRandomRecipe)
            randomDao.addRandomRecipe(newRandomRecipe)
        }
       return randomRecipeData
    }
}