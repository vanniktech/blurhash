package com.vanniktech.blurhash.compose

import androidx.compose.ui.graphics.ImageBitmap
import com.vanniktech.blurhash.BlurHash
import com.vanniktech.blurhash.CommonBlurHash
import com.vanniktech.blurhash.DEFAULT_PUNCH
import com.vanniktech.blurhash.PixelReaderArgb8888

@Suppress("UnusedReceiverParameter")
fun BlurHash.encode(
  bufferedImage: ImageBitmap,
  componentX: Int,
  componentY: Int,
): String {
  val width = bufferedImage.width
  val height = bufferedImage.height
  val pixels = IntArray(width * height)
  bufferedImage.readPixels(pixels)

  return CommonBlurHash.encode(
    pixelReader = PixelReaderArgb8888(pixels = pixels, width = width),
    width = width,
    height = height,
    componentX = componentX,
    componentY = componentY,
  )
}

expect fun BlurHash.decodeAsImageBitmap(
  blurHash: String,
  width: Int,
  height: Int,
  punch: Float = DEFAULT_PUNCH,
  useCache: Boolean = true,
): ImageBitmap?
