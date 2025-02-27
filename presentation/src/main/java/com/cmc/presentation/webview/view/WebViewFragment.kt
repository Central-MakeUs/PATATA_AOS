package com.cmc.presentation.webview.view

import android.webkit.WebView
import android.webkit.WebViewClient
import com.cmc.common.base.BaseFragment
import com.cmc.common.constants.NavigationKeys
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentWebViewBinding

class WebViewFragment: BaseFragment<FragmentWebViewBinding>(R.layout.fragment_web_view) {

    private lateinit var webView: WebView

    override fun initObserving() {
    }

    override fun initView() {
        val url = arguments?.getString(NavigationKeys.WebView.ARGUMENT_WEB_VIEW_URL) ?: "https://www.notion.so"

        webView = binding.webView
        webView.apply {
            webViewClient = WebViewClient()
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportMultipleWindows(false)
            }
            loadUrl(url)
        }
    }
}