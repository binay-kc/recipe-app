package com.binay.recipeapp.uis.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.binay.recipeapp.R
import com.binay.recipeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initView()
    }

    private fun initView(){
        supportFragmentManager.beginTransaction().replace(R.id.fl_container, UnitConverterFragment()).commit()
    }
}