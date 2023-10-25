package com.binay.recipeapp.data.model

data class RecipeResponseData (
    var results      : ArrayList<RecipeData> = arrayListOf(),
    var offset       : Int?               = null,
    var number       : Int?               = null,
    var totalResults : Int?               = null
)