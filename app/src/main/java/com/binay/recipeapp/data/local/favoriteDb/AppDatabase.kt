package com.binay.recipeapp.data.local.favoriteDb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.binay.recipeapp.data.local.WebsiteDao
import com.binay.recipeapp.data.local.ingredientDb.IngredientDao
import com.binay.recipeapp.data.local.randomRecipeDb.RandomRecipeDao
import com.binay.recipeapp.data.local.recipesDb.RecipeDao
import com.binay.recipeapp.data.model.ExtendedIngredients
import com.binay.recipeapp.data.model.RecipeData
import com.binay.recipeapp.data.model.WebsiteData

@Database(
    entities = [RecipeData::class, ExtendedIngredients::class, WebsiteData::class],
    version = 1
)
@TypeConverters(RecipeTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun websiteDao(): WebsiteDao
    abstract fun randomRecipeDao(): RandomRecipeDao
    abstract fun recipeDao(): RecipeDao
}
