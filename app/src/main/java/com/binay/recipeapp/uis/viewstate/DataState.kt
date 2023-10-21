package com.binay.recipeapp.uis.viewstate

sealed class DataState{
    object Inactive : DataState()
    object Loading : DataState()
    data class ResponseData(val string: String) : DataState()
    data class Error(val error : String?) : DataState()
}
