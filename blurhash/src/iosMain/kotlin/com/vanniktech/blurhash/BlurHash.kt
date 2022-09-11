package com.vanniktech.blurhash

import kotlinx.cinterop.get
import kotlinx.cinterop.readValue
import kotlinx.cinterop.set
import kotlinx.cinterop.useContents
import platform.CoreFoundation.CFDataCreateMutable
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetMutableBytePtr
import platform.CoreFoundation.CFDataSetLength
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreGraphics.CGBitmapContextCreateImage
import platform.CoreGraphics.CGBitmapContextCreateWithData
import platform.CoreGraphics.CGColorRenderingIntent
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGColorSpaceCreateWithName
import platform.CoreGraphics.CGContextScaleCTM
import platform.CoreGraphics.CGContextTranslateCTM
import platform.CoreGraphics.CGDataProviderCopyData
import platform.CoreGraphics.CGDataProviderCreateWithCFData
import platform.CoreGraphics.CGFloat
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageCreate
import platform.CoreGraphics.CGImageGetBitsPerPixel
import platform.CoreGraphics.CGImageGetBytesPerRow
import platform.CoreGraphics.CGImageGetDataProvider
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGPointZero
import platform.CoreGraphics.kCGColorSpaceSRGB
import platform.UIKit.UIGraphicsPopContext
import platform.UIKit.UIGraphicsPushContext
import platform.UIKit.UIImage
import kotlin.math.roundToLong

actual object BlurHash {
  /**
   * Clear in-memory calculations.
   * The cache is not big, but will increase when many image sizes are decoded, using [decode].
   * If the app needs memory it is recommended to clear it.
   */
  actual fun clearCache() = CommonBlurHash.clearCache()

  /**
   * Calculates the blur hash from the given [uiImage].
   *
   * [componentX] number of components in the x dimension
   * [componentY] number of components in the y dimension
   */
  fun encode(
    uiImage: UIImage,
    componentX: Int,
    componentY: Int,
  ): String? {
    val uiWidth = uiImage.size.useContents { width }
    val uiScale = uiImage.scale
    val uiHeight = uiImage.size.useContents { height }

    val pixelWidth = (uiWidth * uiScale).roundToLong().toULong()
    val pixelHeight = (uiHeight * uiScale).roundToLong().toULong()

    val context = CGBitmapContextCreateWithData(
      data = null,
      width = pixelWidth,
      height = pixelHeight,
      bitsPerComponent = 8,
      bytesPerRow = pixelWidth * 4uL,
      space = CGColorSpaceCreateWithName(name = kCGColorSpaceSRGB),
      bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value,
      releaseCallback = null,
      releaseInfo = null,
    ) ?: return null

    CGContextScaleCTM(context, uiScale, -uiScale)
    CGContextTranslateCTM(context, 0.0, -uiHeight)

    UIGraphicsPushContext(context)
    uiImage.drawAtPoint(CGPointZero.readValue())
    UIGraphicsPopContext()

    val cgImage = CGBitmapContextCreateImage(context) ?: return null
    val dataProvider = CGImageGetDataProvider(cgImage) ?: return null
    val data = CGDataProviderCopyData(dataProvider) ?: return null
    val pixels = CFDataGetBytePtr(data) ?: return null

    val cgWidth = CGImageGetWidth(cgImage).toInt()
    val cgHeight = CGImageGetHeight(cgImage).toInt()
    val cgBytesPerRow = CGImageGetBytesPerRow(cgImage).toInt()
    val cgBitsPerPixel = CGImageGetBitsPerPixel(cgImage).toInt()
    val cgBytesPerPixel = cgBitsPerPixel / 8

    return CommonBlurHash.encode(
      pixelReader = object : PixelReader {
        override fun readRed(x: Int, y: Int) =
          pixels[cgBytesPerPixel * x + 0 + y * cgBytesPerRow].toInt()

        override fun readGreen(x: Int, y: Int) =
          pixels[cgBytesPerPixel * x + 1 + y * cgBytesPerRow].toInt()

        override fun readBlue(x: Int, y: Int) =
          pixels[cgBytesPerPixel * x + 2 + y * cgBytesPerRow].toInt()
      },
      width = cgWidth,
      height = cgHeight,
      componentY = componentY,
      componentX = componentX,
    )
  }

  /**
   * If [blurHash] is a valid blur hash, the method will return a [UIImage],
   * with the requested [width] as well as [height].
   *
   * [useCache] to control the caching which will improve performance (with a slight memory impact)
   */
  fun decode(
    blurHash: String,
    width: CGFloat,
    height: CGFloat,
    punch: Float = 1f,
    useCache: Boolean = true,
  ): UIImage? {
    val imageWidth = width.toLong()
    val imageHeight = height.toLong()
    val bytesPerRow = imageWidth * 3L

    val data = CFDataCreateMutable(kCFAllocatorDefault, bytesPerRow * imageHeight) ?: return null
    CFDataSetLength(data, bytesPerRow * imageHeight)
    val pixels = CFDataGetMutableBytePtr(data) ?: return null

    val pixelWriter = object : PixelWriter<Unit> {
      override fun write(
        x: Int,
        y: Int,
        width: Int,
        red: Int,
        green: Int,
        blue: Int,
      ) {
        pixels[3 * x + 0 + y * bytesPerRow.toInt()] = red.toUByte()
        pixels[3 * x + 1 + y * bytesPerRow.toInt()] = green.toUByte()
        pixels[3 * x + 2 + y * bytesPerRow.toInt()] = blue.toUByte()
      }

      override fun get() = Unit
    }

    CommonBlurHash.decode(
      blurHash = blurHash,
      pixelWriter = pixelWriter,
      width = width.toInt(),
      height = height.toInt(),
      punch = punch,
      useCache = useCache,
    ) ?: return null

    val provider = CGDataProviderCreateWithCFData(data) ?: return null

    val cgImage = CGImageCreate(
      width = imageWidth.toULong(),
      height = imageHeight.toULong(),
      bitsPerComponent = 8,
      bitsPerPixel = 24,
      bytesPerRow = bytesPerRow.toULong(),
      space = CGColorSpaceCreateDeviceRGB(),
      bitmapInfo = CGImageAlphaInfo.kCGImageAlphaNone.value,
      provider = provider,
      decode = null,
      shouldInterpolate = true,
      intent = CGColorRenderingIntent.kCGRenderingIntentDefault,
    ) ?: return null

    return UIImage.imageWithCGImage(cgImage)
  }
}
