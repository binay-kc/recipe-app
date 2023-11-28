package com.binay.recipeapp.data.model

import com.binay.recipeapp.uis.view.IngredientInstructionInterface

data class AnalyzedInstructions (
    var name  : String?          = null,
    var steps : ArrayList<Steps>? = arrayListOf(),
    var readyInMinutes : Int?
): IngredientInstructionInterface
