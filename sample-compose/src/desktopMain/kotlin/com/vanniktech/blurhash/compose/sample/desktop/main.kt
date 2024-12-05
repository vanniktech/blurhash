package com.vanniktech.blurhash.compose.sample.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.vanniktech.blurhash.compose.sample.App

fun main() {
  application {
    Window(
      onCloseRequest = ::exitApplication,
      title = "BlurHashCompose",
    ) {
      App()
    }
  }
}
