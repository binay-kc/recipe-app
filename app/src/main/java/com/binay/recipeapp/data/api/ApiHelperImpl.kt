package com.binay.recipeapp.data.api

import android.util.Log
import com.binay.recipeapp.data.model.RecipeResponseData

class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {
    // TODO: Remove this and use as per required
    override suspend fun getData(): RecipeResponseData {
        Log.d("haancha", "getData: ")
        return apiService.getData()
    }

}