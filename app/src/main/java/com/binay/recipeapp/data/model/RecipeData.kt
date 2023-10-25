package com.binay.recipeapp.data.model

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
    var id                       : Int?                            = null,
    var title                    : String?                         = null,
    var readyInMinutes           : Int?                            = null,
    var summary                  : String?                         = null,
    var servings                 : Int?                            = null,
    var image                    : String?                         = null,
    var cuisines                 : ArrayList<String>               = arrayListOf(),
    var dishTypes                : ArrayList<String>               = arrayListOf(),
    var instructions             : String?                         = null
)