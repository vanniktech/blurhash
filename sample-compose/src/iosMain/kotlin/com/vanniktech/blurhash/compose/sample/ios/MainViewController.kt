package com.vanniktech.blurhash.compose.sample.ios

import androidx.compose.ui.window.ComposeUIViewController
import com.vanniktech.blurhash.compose.sample.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
  val viewController = ComposeUIViewController {
    App()
  }

  return viewController
}
