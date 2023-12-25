package com.binay.recipeapp.uis.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.binay.recipeapp.R
import com.binay.recipeapp.data.model.WebsiteData
import com.binay.recipeapp.databinding.ActivityExternalWebsitesBinding
import com.binay.recipeapp.uis.intent.DataIntent
import com.binay.recipeapp.uis.viewmodel.MainViewModel
import com.binay.recipeapp.uis.viewstate.DataState
import com.binay.recipeapp.util.ViewModelFactory
import kotlinx.coroutines.launch

class ExternalWebsitesActivity : AppCompatActivity(),
    WebsiteRecyclerAdapter.OnWebsiteClickListener {

    private lateinit var mBinding: ActivityExternalWebsitesBinding
    private lateinit var mViewModel: MainViewModel
    private lateinit var mAdapter: WebsiteRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityExternalWebsitesBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initView()
        initViewModel()
    }

    private fun initView() {

        mBinding.toolbar.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        mBinding.toolbar.toolbarTitle.text = getString(R.string.recipe_site)

        mBinding.recyclerView.setHasFixedSize(true)
        mBinding.recyclerView.layoutManager = GridLayoutManager(this, 2)

        mAdapter = WebsiteRecyclerAdapter(this, this)
        mBinding.recyclerView.adapter = mAdapter
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[MainViewModel::class.java]

        lifecycleScope.launch {
            mViewModel.dataState.collect {
                when (it) {
                    is DataState.Loading -> {

                    }

                    is DataState.FetchWebsiteList -> {
                        val websiteList = it.websites
                        Log.e("list", "initViewModel: " + websiteList.size)
                        mAdapter.setData(websiteList)
                    }

                    else -> {

                    }
                }
            }
        }

        fetchData()
    }

    private fun fetchData() {
        lifecycleScope.launch {
            mViewModel.dataIntent.send(
                DataIntent.FetchWebsiteList
            )
        }
    }

    override fun onWebsiteClicked(website: WebsiteData) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("name", website.name)
        intent.putExtra("url", website.link)
        startActivity(intent)
    }
}