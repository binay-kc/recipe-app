package com.binay.recipeapp.data.local.favoriteDb

import androidx.room.TypeConverter
import com.binay.recipeapp.data.model.ExtendedIngredients
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class RecipeTypeConverters {
    @TypeConverter
    fun stringToExtendedIngredients(value: String?): ArrayList<ExtendedIngredients?>? {
        if (value.isNullOrEmpty()) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<ExtendedIngredients>?>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun extendedIngredientsToString(ingredients: ArrayList<ExtendedIngredients>?): String? {
        if (ingredients == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<ArrayList<ExtendedIngredients>?>() {}.type
        return gson.toJson(ingredients, type)
    }

    @TypeConverter
    fun listToString(strings: ArrayList<String?>?): String? {
        if (strings.isNullOrEmpty()) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<String?>?>() {}.type
        return gson.toJson(strings, type)
    }

    @TypeConverter
    fun stringToArray(string: String?): ArrayList<String?>? {
        if (string.isNullOrEmpty()) {
            return ArrayList()
        }
        val gson = Gson()
        val type = object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson(string, type)
    }
}