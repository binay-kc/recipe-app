package com.binay.recipeapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeData (
    var vegetarian               : Boolean?                        = null,
    var vegan                    : Boolean?                        = null,
    var glutenFree               : Boolean?                        = null,
    var dairyFree                : Boolean?                        = null,
    var veryHealthy              : Boolean?                        = null,
    var preparationMinutes       : Int?                            = null,
    var cookingMinutes           : Int?                            = null,
    var aggregateLikes           : Int?                            = null,
    var healthScore              : Int?                            = null,
    var extendedIngredients      : ArrayList<ExtendedIngredients>  = arrayListOf(),
   @PrimaryKey var id                       : Int                            = System.currentTimeMillis().toInt(),
    var title                    : String?                         = null,
    var readyInMinutes           : Int?                            = null,
    var summary                  : String?                         = null,
    var servings                 : Int?                            = null,
    var image                    : String?                         = null,
    var nutrition                : Nutrition     ?                 = Nutrition(),
    var analyzedInstructions     : ArrayList<AnalyzedInstructions>? = arrayListOf(),
    var cuisines                 : ArrayList<String> ?              = arrayListOf(),
    var dishTypes                : ArrayList<String>?               = arrayListOf(),
    var instructions             : String?                         = null,
    var isFavorite             : Boolean?                          = null
)