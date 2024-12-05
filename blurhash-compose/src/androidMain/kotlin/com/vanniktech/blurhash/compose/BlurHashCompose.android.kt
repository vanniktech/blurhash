package com.vanniktech.blurhash.compose

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.vanniktech.blurhash.BlurHash
import com.vanniktech.blurhash.CommonBlurHash
import com.vanniktech.blurhash.PixelWriterArgb8888

actual fun BlurHash.decodeAsImageBitmap(
  blurHash: String,
  width: Int,
  height: Int,
  punch: Float,
  useCache: Boolean,
): ImageBitmap? {
  val pixels = CommonBlurHash.decode(
    blurHash = blurHash,
    pixelWriter = PixelWriterArgb8888(width = width, height = height),
    width = width,
    height = height,
    punch = punch,
    useCache = useCache,
  )

  return when (pixels) {
    null -> null
    else -> Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888).asImageBitmap()
  }
}
