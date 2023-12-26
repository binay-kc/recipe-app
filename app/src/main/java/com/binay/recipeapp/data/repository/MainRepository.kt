package com.binay.recipeapp.data.repository

import android.content.Context
import com.binay.recipeapp.data.api.ApiHelper
import com.binay.recipeapp.data.repository.local.LocalRepo
import com.binay.recipeapp.data.local.favoriteDb.AppDatabase
import com.binay.recipeapp.data.model.RecipeResponseData
import com.binay.recipeapp.data.repository.remote.RemoteRepo
import com.binay.recipeapp.util.NetworkUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MainRepository @Inject constructor(
    @ApplicationContext private val mContext: Context,
    mDatabase: AppDatabase,
    private val apiHelper: ApiHelper,
    private val mLocalRepo: LocalRepo,
    private val mRemoteRepo: RemoteRepo
) {

    suspend fun getRecipes(tag: String): RecipeResponseData {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            val recipesData = mLocalRepo.getRecipesByTag(tag)
            if (recipesData != null) return recipesData
        }
        return mRemoteRepo.getRecipes(tag)
    }

    suspend fun getRecipeDetail(id: Int) = apiHelper.getRecipeDetail(id)

    suspend fun searchRecipes(query: String) = apiHelper.searchRecipes(query)

    suspend fun searchRecipesByIngredients(query: String) =
        apiHelper.searchRecipesByIngredients(query)

    suspend fun getRandomRecipe(): RecipeResponseData {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            val localRandomRecipe = mLocalRepo.getRandomRecipe()
            if (localRandomRecipe != null) return localRandomRecipe
        }
        return mRemoteRepo.getRandomRecipe()
    }
}