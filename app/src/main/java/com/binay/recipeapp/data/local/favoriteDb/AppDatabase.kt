package com.binay.recipeapp.data.local.favoriteDb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.binay.recipeapp.data.local.ingredientDb.IngredientDao
import com.binay.recipeapp.data.model.ExtendedIngredients
import com.binay.recipeapp.data.model.RecipeData

@Database(entities = [RecipeData::class, ExtendedIngredients::class], version = 1)
@TypeConverters(RecipeTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun ingredientDao(): IngredientDao
}