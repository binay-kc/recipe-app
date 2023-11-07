package com.binay.recipeapp.data.model

import com.binay.recipeapp.uis.view.IngredientInstructionInterface

data class Steps (
    var number      : Int?                   = null,
    var step        : String?                = null
): IngredientInstructionInterface
