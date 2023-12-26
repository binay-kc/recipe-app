package com.binay.recipeapp.data.repository.remote

import android.util.Log
import com.binay.recipeapp.data.api.ApiHelper
import com.binay.recipeapp.data.local.favoriteDb.AppDatabase
import com.binay.recipeapp.data.local.favoriteDb.FavoriteDao
import com.binay.recipeapp.data.local.randomRecipeDb.RandomRecipeDao
import com.binay.recipeapp.data.local.recipesDb.RecipeDao
import com.binay.recipeapp.data.model.RecipeResponseData
import javax.inject.Inject

class RemoteRepo @Inject constructor(
    private val apiHelper: ApiHelper,
    private val randomRecipeDao: RandomRecipeDao, private val recipeDao: RecipeDao,
    private val favoriteDao: FavoriteDao
) {

    suspend fun getRandomRecipe(): RecipeResponseData {
        val randomRecipeData = apiHelper.getRandomRecipe()
        if (randomRecipeData.recipes.isNotEmpty()) {
            val newRandomRecipe = randomRecipeData.recipes[0]
            newRandomRecipe.isRandom = true
//                    Fetch previous random recipe
            val previousRandomRecipe = randomRecipeDao.getRandomRecipe()
            Log.e("Random recipe ", "ayo $previousRandomRecipe")
            if (previousRandomRecipe != null) randomRecipeDao.removeRandomRecipe(
                previousRandomRecipe
            )
            randomRecipeDao.addRandomRecipe(newRandomRecipe)
        }
        return randomRecipeData
    }

    suspend fun getRecipes(tag: String): RecipeResponseData {

        val recipeData = if (tag == "all")
            apiHelper.getData("")
        else apiHelper.getData(tag)

//        Fetch and remove previous recipes which are not favorite

        val previousRecipes = recipeDao.getRecipes(tag)

        val isFirstLoad = previousRecipes?.isEmpty() == true
        if (previousRecipes != null && !isFirstLoad) {
            val recipesWithNoFavoriteAndSameTag =
                previousRecipes.filter { recipe -> recipe.tagToBeSearchedBy == tag && !recipe.isFavorite && !recipe.isRandom }
            val recipeIds = ArrayList<Int>()
            recipesWithNoFavoriteAndSameTag.forEach { recipe -> recipeIds.add(recipe.id) }
            Log.e("Before Remove, Db Count ", " ${recipeDao.getRecipes(tag)?.count()}")
            recipeDao.removePreviousRecipes(recipeIds)

            Log.e(" After Remove, Db Count ", " ${recipeDao.getRecipes(tag)?.count()}")
        }

        recipeData.recipes.forEach {
            it.tagToBeSearchedBy = tag
//            Checks favorite Dao and updates data accordingly
//                Note: If room has recipe, then it is automatically favorite
            if (!isFirstLoad) {
                val favoriteRecipe = favoriteDao.getRecipe(it.id)
                Log.e("Favorite Recipe: ", " $favoriteRecipe")
                if (favoriteRecipe != null) {
                    it.isFavorite = true
                }
            }
        }
//        Add new recipes
        recipeDao.addAllRecipes(recipeData.recipes)
        return recipeData
    }

}