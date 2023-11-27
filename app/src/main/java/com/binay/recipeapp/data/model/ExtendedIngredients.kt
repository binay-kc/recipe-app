package com.binay.recipeapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.binay.recipeapp.uis.view.IngredientInstructionInterface

@Entity(tableName = "ingredients")
data class ExtendedIngredients (
    @PrimaryKey var id           : Int?              = null,
    var name         : String?           = null,
    var original     : String?           = null
): IngredientInstructionInterface