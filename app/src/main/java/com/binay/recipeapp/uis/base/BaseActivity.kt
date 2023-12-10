package com.binay.recipeapp.uis.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.binay.recipeapp.data.local.favoriteDb.AppDatabase

open class BaseActivity : AppCompatActivity() {

    protected lateinit var mDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "recipe-palette"
        ).allowMainThreadQueries().build()

    }
}