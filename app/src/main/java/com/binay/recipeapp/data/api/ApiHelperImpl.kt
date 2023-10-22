package com.binay.recipeapp.data.api

class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {
    // TODO: Remove this and use as per required
    override suspend fun getData(): String {
        return apiService.getData()
    }

}