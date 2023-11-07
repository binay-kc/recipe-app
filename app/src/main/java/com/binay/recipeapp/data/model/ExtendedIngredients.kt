package com.binay.recipeapp.data.model

import com.binay.recipeapp.uis.view.IngredientInstructionInterface

data class ExtendedIngredients (
    var id           : Int?              = null,
    var name         : String?           = null,
    var original     : String?           = null
): IngredientInstructionInterface