package com.binay.recipeapp.data.api


import android.util.Log
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.data.model.RecipeResponseData
import com.binay.recipeapp.data.model.SearchedRecipe
import com.binay.recipeapp.data.model.SearchedRecipeData

class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {
    override suspend fun getData(tag: String): RecipeResponseData {
        return apiService.getData(tag)
    }

    override suspend fun searchRecipes(query: String): SearchedRecipeData {
        return apiService.searchRecipes(query)
    }

    override suspend fun getRecipeDetail(id: Int): RecipeData {
        return apiService.getRecipeDetail(id)
    }

    override suspend fun searchRecipesByIngredients(query: String): ArrayList<SearchedRecipe> {
        return apiService.searchRecipesByIngredients(query)
    }

    override suspend fun getRandomRecipe(): RecipeResponseData {
        return apiService.getRandomRecipe()
    }
}