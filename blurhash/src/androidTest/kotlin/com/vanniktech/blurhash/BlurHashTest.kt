package com.vanniktech.blurhash

import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.LinearLayout
import app.cash.paparazzi.DeviceConfig.Companion.PIXEL_5
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class BlurHashTest {
  @get:Rule
  val paparazzi = Paparazzi(
    deviceConfig = PIXEL_5,
    theme = "android:Theme.Material.Light.NoActionBar",
  )

  @Test fun encodeDecode() {
    val blurHash = BlurHash.encode(BitmapFactory.decodeStream(workspaceDirectory().resolve("sample-android/src/main/res/drawable-nodpi/blueberries.jpg").inputStream()), 5, 4)
    assertEquals(expected = "V4BhTMQyJ]iErP8wM^%ht?tboYkPtSiWXtMKSrxcSst7", actual = blurHash)

    val imageView = ImageView(paparazzi.context)
    val density = paparazzi.context.resources.displayMetrics.density
    val width = (300 * density).toInt()
    val height = (200 * density).toInt()
    imageView.setImageBitmap(BlurHash.decode(blurHash, width, height))
    paparazzi.snapshot(
      LinearLayout(paparazzi.context).apply {
        addView(imageView, LinearLayout.LayoutParams(width, height))
      },
    )
  }

  private fun workspaceDirectory() = File(BlurHashTest::class.java.classLoader?.getResource(".")?.file!!)
    .parentFile
    ?.parentFile
    ?.parentFile
    ?.parentFile
    ?.parentFile!!
}
