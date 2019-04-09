package com.example.ryosukeizumi.kotlinsampleapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_web_view.*
import timber.log.Timber

import com.amazonaws.http.HttpMethodName
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory
import com.amazonaws.mobileconnectors.apigateway.ApiClientException
import com.amazonaws.mobileconnectors.apigateway.ApiResponse
import com.amazonaws.mobileconnectors.apigateway.ApiRequest
import jp.co.mock.CdkADeployedPIClient

class WebViewActivity : AppCompatActivity() {
    private val url = "http://vue-webview-app.s3-website-ap-northeast-1.amazonaws.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)


        // WebViewの設定等
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                setSupportZoom(true)
                loadWithOverviewMode = true
                useWideViewPort = true
                domStorageEnabled = true
            }
            webChromeClient = WebChromeClient()
            webViewClient = WebViewClient()
        }

        // WebViewからNative側にデータを渡す
        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                try {

                    val client: CdkADeployedPIClient = ApiClientFactory()
                        .credentialsProvider(null)
                        .build(CdkADeployedPIClient::class.java)

                    val request = ApiRequest(client.javaClass.simpleName)
                        .withHttpMethod(HttpMethodName.GET)
                        .withPath("get")
                    val response = client.execute(request)
                    Timber.d(response.toString())
                } catch (e: ApiClientException) {
                    Timber.e(e.toString())
                }

                result?.cancel()
                return true
            }
        }

        // NativeからWebViewにデータを渡す
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val text = "{key: \"Message from Native\", value: 100}"
                webView.evaluateJavascript("window.testFunc('$text')", null)
            }
        }

        // 指定したURLのページをLoad
        webView.loadUrl(url)
    }
}
