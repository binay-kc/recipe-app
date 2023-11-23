package com.binay.recipeapp.data.model

import androidx.room.Entity
import com.binay.recipeapp.uis.view.IngredientInstructionInterface

@Entity(tableName = "ingredients")
data class ExtendedIngredients (
    var id           : Int?              = null,
    var name         : String?           = null,
    var original     : String?           = null
): IngredientInstructionInterface