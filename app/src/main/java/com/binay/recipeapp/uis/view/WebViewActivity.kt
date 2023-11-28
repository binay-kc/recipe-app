package com.binay.recipeapp.uis.view

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.binay.recipeapp.R
import com.binay.recipeapp.databinding.ActivityWebViewBinding

class WebViewActivity: AppCompatActivity() {

    private lateinit var mBinding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initView()
    }

    private fun initView() {
        mBinding.toolbar.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        mBinding.toolbar.toolbarTitle.text = intent.getStringExtra("name")

        mBinding.webView.webViewClient = MyWebViewClient()
        mBinding.webView.settings.javaScriptEnabled = true
        mBinding.webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        mBinding.webView.loadUrl(intent.getStringExtra("url").toString())
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url ?: "")
            return true
        }
    }
}