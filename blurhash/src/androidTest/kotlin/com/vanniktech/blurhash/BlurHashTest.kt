package com.vanniktech.blurhash

import android.graphics.Color
import android.widget.ImageView
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.androidHome
import app.cash.paparazzi.detectEnvironment
import org.junit.Ignore
import org.junit.Rule
import kotlin.test.Test

class BlurHashTest {
  @get:Rule
  val paparazzi = Paparazzi(
    deviceConfig = PIXEL_5,
    theme = "android:Theme.Material.Light.NoActionBar",
    environment = detectEnvironment().copy(
      platformDir = "${androidHome()}/platforms/android-32",
      compileSdkVersion = 32,
    ),
  )

  @Test @Ignore("Layout lib crash") fun launchView() {
    val view = ImageView(paparazzi.context)
    view.layoutParams.height = 50
    view.layoutParams.width = 50
    view.setBackgroundColor(Color.RED)
    paparazzi.snapshot(view)
  }
}
