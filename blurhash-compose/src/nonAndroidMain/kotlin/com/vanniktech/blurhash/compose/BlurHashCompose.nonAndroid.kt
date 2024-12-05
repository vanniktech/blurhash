package com.vanniktech.blurhash.compose

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.vanniktech.blurhash.BlurHash
import com.vanniktech.blurhash.CommonBlurHash
import com.vanniktech.blurhash.PixelWriter
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo

actual fun BlurHash.decodeAsImageBitmap(
  blurHash: String,
  width: Int,
  height: Int,
  punch: Float,
  useCache: Boolean,
): ImageBitmap? {
  // Four bytes per pixel.
  val rowBytes = width * 4

  val pixelWriter = PixelWriterRgba8888(
    rowBytes = rowBytes,
    height = height,
  )

  val bytes = CommonBlurHash.decode(
    blurHash = blurHash,
    pixelWriter = pixelWriter,
    width = width,
    height = height,
    punch = punch,
    useCache = useCache,
  )

  return when (bytes) {
    null -> null
    else -> Image.makeRaster(
      imageInfo = ImageInfo(
        width = width,
        height = height,
        colorType = ColorType.RGBA_8888,
        alphaType = ColorAlphaType.UNPREMUL,
      ),
      rowBytes = rowBytes,
      bytes = bytes,
    ).toComposeImageBitmap()
  }
}

internal class PixelWriterRgba8888(
  private val rowBytes: Int,
  height: Int,
) : PixelWriter<ByteArray> {
  private val pixels = ByteArray(rowBytes * height)

  override fun write(
    x: Int,
    y: Int,
    width: Int,
    red: Int,
    green: Int,
    blue: Int,
  ) {
    pixels[4 * x + 0 + y * rowBytes] = red.toByte()
    pixels[4 * x + 1 + y * rowBytes] = green.toByte()
    pixels[4 * x + 2 + y * rowBytes] = blue.toByte()
    pixels[4 * x + 3 + y * rowBytes] = -1
  }

  override fun get() = pixels
}
