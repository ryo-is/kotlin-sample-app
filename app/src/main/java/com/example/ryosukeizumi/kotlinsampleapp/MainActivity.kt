package com.example.ryosukeizumi.kotlinsampleapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            val editTextInput: String = editText.text.toString()
            textView.text = editTextInput
        }

        val awsAppSyncClient = AWSAppSyncClient.builder()
            .context(applicationContext)
            .awsConfiguration(AWSConfiguration(applicationContext))
            .build()
        print(awsAppSyncClient)
    }
}
