package com.binay.recipeapp.uis.viewstate

sealed class UnitState {
    object Inactive : UnitState()
    object Loading : UnitState()
    data class ResponseData(val convertedAmt: String) : UnitState()
    data class Error(val error: String?) : UnitState()
}
