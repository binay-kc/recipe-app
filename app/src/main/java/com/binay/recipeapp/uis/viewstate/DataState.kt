package com.binay.recipeapp.uis.viewstate

import com.binay.recipeapp.data.model.RecipeResponseData

sealed class DataState{
    object Inactive : DataState()
    object Loading : DataState()
    data class ResponseData(val recipeResponseData: RecipeResponseData) : DataState()
    data class Error(val error : String?) : DataState()
}
