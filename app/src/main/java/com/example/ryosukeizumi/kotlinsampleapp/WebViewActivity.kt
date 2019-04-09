package com.example.ryosukeizumi.kotlinsampleapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import com.amazonaws.amplify.generated.graphql.CreateAndroidDemoApiMutation
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import kotlinx.android.synthetic.main.activity_web_view.*
import timber.log.Timber
import type.CreateAndroidDemoAPIInput
import java.text.SimpleDateFormat
import java.util.*

class WebViewActivity : AppCompatActivity() {
    private val url = "http://vue-webview-app.s3-website-ap-northeast-1.amazonaws.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        // 現在時刻取得
        fun getNowTime(): String {
            val date = Date()
            val format = SimpleDateFormat("YYYY/MM/dd HH:mm:ss", Locale.getDefault())
            return format.format(date)
        }

        // AppSyncClientの初期化
        val awsAppSyncClient = AWSAppSyncClient.builder()
            .context(applicationContext)
            .awsConfiguration(AWSConfiguration(applicationContext))
            .build()

        // MutationのCallBack
        val mutationCallBack = object : GraphQLCall.Callback<CreateAndroidDemoApiMutation.Data>() {
            override fun onResponse(response: com.apollographql.apollo.api.Response<CreateAndroidDemoApiMutation.Data>) {
                Timber.d(response.data().toString())
            }

            override fun onFailure(e: ApolloException) {
                Timber.e(e.toString())
            }
        }

        // Mutation実行
        fun runMutation(description: String) {
            val createAndroidDemoAPIInput = CreateAndroidDemoAPIInput.builder()
                .id("admin")
                .create_time(getNowTime())
                .description(description)
                .build()

            awsAppSyncClient.mutate(CreateAndroidDemoApiMutation.builder().input(createAndroidDemoAPIInput).build())
                .enqueue(mutationCallBack)
        }

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
            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                try {
                    runMutation(message)
                    Timber.d(message)
                } catch (e: Exception) {
                    Timber.e(e.toString())
                }

                result.cancel()
                return true
            }
        }

        // NativeからWebViewにデータを渡す
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val text = "{key: \"Message from Native\", value: 100}"
                webView.evaluateJavascript("window.testFunc('$text')", null)
            }
        }

        webView.clearCache(true)
        // 指定したURLのページをLoad
        webView.loadUrl(url)

    }
}
