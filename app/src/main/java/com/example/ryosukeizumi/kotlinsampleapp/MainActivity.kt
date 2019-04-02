package com.example.ryosukeizumi.kotlinsampleapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.amazonaws.amplify.generated.graphql.CreateAndroidDemoApiMutation
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import type.CreateAndroidDemoAPIInput
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        // 現在時刻取得
        fun getNowTime(): String {
            val date = Date()
            val format = SimpleDateFormat("YYYY/MM/DD HH:mm:ss", Locale.getDefault())
            return format.format(date)
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

        // ButtonClick時の処理
        button.setOnClickListener {
            val editTextInput: String = editText.text.toString()
            textView.text = editTextInput
            runMutation(editTextInput)
        }
    }
}
