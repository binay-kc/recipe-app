package com.binay.recipeapp.uis.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.binay.recipeapp.data.local.favoriteDb.AppDatabase

open class BaseFragment : Fragment() {

    protected lateinit var mDatabase : AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDatabase = Room.databaseBuilder(
            requireContext().applicationContext,
            AppDatabase::class.java, "recipe-palette"
        ).allowMainThreadQueries().build()
    }
}