package com.vanniktech.blurhash.compose.sample.android

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vanniktech.blurhash.compose.sample.App

class BlurHashComposeMainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.light(
        Color.TRANSPARENT,
        Color.TRANSPARENT,
      ),
      navigationBarStyle = SystemBarStyle.light(
        Color.TRANSPARENT,
        Color.TRANSPARENT,
      ),
    )
    super.onCreate(savedInstanceState)

    setContent {
      App()
    }
  }
}
