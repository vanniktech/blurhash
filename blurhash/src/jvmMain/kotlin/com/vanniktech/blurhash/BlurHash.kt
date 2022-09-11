package com.vanniktech.blurhash

import java.awt.Point
import java.awt.image.BufferedImage
import java.awt.image.ColorModel
import java.awt.image.DataBuffer
import java.awt.image.DataBufferInt
import java.awt.image.Raster
import java.awt.image.SinglePixelPackedSampleModel

actual object BlurHash {
  /**
   * Clear in-memory calculations.
   * The cache is not big, but will increase when many image sizes are decoded, using [decode].
   * If the app needs memory it is recommended to clear it.
   */
  actual fun clearCache() = CommonBlurHash.clearCache()

  /**
   * Calculates the blur hash from the given [bufferedImage].
   *
   * [componentX] number of components in the x dimension
   * [componentY] number of components in the y dimension
   */
  fun encode(
    bufferedImage: BufferedImage,
    componentX: Int,
    componentY: Int,
  ): String {
    val width = bufferedImage.width
    val height = bufferedImage.height
    val pixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width)
    return CommonBlurHash.encode(
      pixelReader = PixelReaderArgb8888(pixels = pixels, width = width),
      width = width,
      height = height,
      componentX = componentX,
      componentY = componentY,
    )
  }

  /**
   * If [blurHash] is a valid blur hash, the method will return a [BufferedImage],
   * with the requested [width] as well as [height].
   *
   * [useCache] to control the caching which will improve performance (with a slight memory impact)
   */
  fun decode(
    blurHash: String,
    width: Int,
    height: Int,
    punch: Float = 1f,
    useCache: Boolean = true,
  ): BufferedImage? {
    val pixels = CommonBlurHash.decode(
      blurHash = blurHash,
      pixelWriter = PixelWriterArgb8888(width = width, height = height),
      width = width,
      height = height,
      punch = punch,
      useCache = useCache,
    )

    if (pixels != null) {
      val bitMasks = intArrayOf(0xFF0000, 0xFF00, 0xFF, 0xFF000000.toInt())
      val singlePixelPackedSampleModel = SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, width, height, bitMasks)
      val dataBufferInt = DataBufferInt(pixels, pixels.size)
      val writableRaster = Raster.createWritableRaster(singlePixelPackedSampleModel, dataBufferInt, Point())
      return BufferedImage(ColorModel.getRGBdefault(), writableRaster, false, null)
    }

    return null
  }
}
