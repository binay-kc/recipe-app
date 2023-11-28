package com.binay.recipeapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "websites")
data class WebsiteData (
    @PrimaryKey val name: String,
    val link: String
)