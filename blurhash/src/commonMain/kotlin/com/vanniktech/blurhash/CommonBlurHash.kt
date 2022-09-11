@file:JvmName("BlurHash")

package com.vanniktech.blurhash

import com.vanniktech.blurhash.Utils.applyBasisFunction
import com.vanniktech.blurhash.Utils.encodeAc
import com.vanniktech.blurhash.Utils.encodeDc
import kotlin.jvm.JvmName
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt

internal object CommonBlurHash {
  // Cache Math.cos() calculations to improve performance.
  // The number of calculations can be huge for many bitmaps: width * height * numCompX * numCompY * 2 * nBitmaps
  // The cache is enabled by default, it is recommended to disable it only when just a few images are displayed
  private val cacheCosinesX = HashMap<Int, DoubleArray>()
  private val cacheCosinesY = HashMap<Int, DoubleArray>()

  internal fun clearCache() {
    cacheCosinesX.clear()
    cacheCosinesY.clear()
  }

  internal fun <T : Any> decode(
    blurHash: String,
    pixelWriter: PixelWriter<T>,
    width: Int,
    height: Int,
    punch: Float,
    useCache: Boolean,
  ): T? {
    if (blurHash.length < 6) {
      return null
    }

    val numCompEnc = Base83.decode83(blurHash, 0, 1)
    val componentX = (numCompEnc % 9) + 1
    val componentY = (numCompEnc / 9) + 1

    if (blurHash.length != 4 + 2 * componentX * componentY) {
      return null
    }

    val maxAcEnc = Base83.decode83(blurHash, 1, 2)
    val maxAc = (maxAcEnc + 1) / 166f
    val colors = Array(componentX * componentY) { i ->
      if (i == 0) {
        val colorEnc = Base83.decode83(blurHash, 2, 6)
        Utils.decodeDc(colorEnc)
      } else {
        val from = 4 + i * 2
        val colorEnc = Base83.decode83(blurHash, from, from + 2)
        Utils.decodeAc(colorEnc, maxAc * punch)
      }
    }

    return composePixels(pixelWriter, width, height, componentX, componentY, colors, useCache)
  }

  private fun <T : Any> composePixels(
    pixelWriter: PixelWriter<T>,
    width: Int,
    height: Int,
    componentX: Int,
    componentY: Int,
    colors: Array<FloatArray>,
    useCache: Boolean,
  ): T {
    val calculateCosX = !useCache || !cacheCosinesX.containsKey(width * componentX)
    val cosinesX = getArrayForCosinesX(calculateCosX, width, componentX)
    val calculateCosY = !useCache || !cacheCosinesY.containsKey(height * componentY)
    val cosinesY = getArrayForCosinesY(calculateCosY, height, componentY)
    for (y in 0 until height) {
      for (x in 0 until width) {
        var r = 0f
        var g = 0f
        var b = 0f
        for (j in 0 until componentY) {
          for (i in 0 until componentX) {
            val cosX = cosinesX.getCos(calculateCosX, i, componentX, x, width)
            val cosY = cosinesY.getCos(calculateCosY, j, componentY, y, height)
            val basis = (cosX * cosY).toFloat()
            val color = colors[j * componentX + i]
            r += color[0] * basis
            g += color[1] * basis
            b += color[2] * basis
          }
        }

        pixelWriter.write(
          x = x,
          y = y,
          width = width,
          red = Utils.linearToSrgb(r),
          green = Utils.linearToSrgb(g),
          blue = Utils.linearToSrgb(b),
        )
      }
    }

    return pixelWriter.get()
  }

  private fun getArrayForCosinesY(calculate: Boolean, height: Int, numCompY: Int) = when {
    calculate -> DoubleArray(height * numCompY).also { cacheCosinesY[height * numCompY] = it }
    else -> cacheCosinesY[height * numCompY]!!
  }

  private fun getArrayForCosinesX(calculate: Boolean, width: Int, numCompX: Int) = when {
    calculate -> DoubleArray(width * numCompX).also { cacheCosinesX[width * numCompX] = it }
    else -> cacheCosinesX[width * numCompX]!!
  }

  private fun DoubleArray.getCos(
    calculate: Boolean,
    x: Int,
    numComp: Int,
    y: Int,
    size: Int,
  ): Double {
    val index = x + numComp * y
    if (calculate) {
      this[index] = cos(PI * y * x / size)
    }

    return this[index]
  }

  internal fun encode(
    pixelReader: PixelReader,
    width: Int,
    height: Int,
    componentX: Int,
    componentY: Int,
  ): String {
    require(componentX in 1..9 && componentY in 1..9) { "Blur Hash must have components between 1 and 9" }

    val factors = Array(componentX * componentY) { FloatArray(3) }

    for (y in 0 until componentY) {
      for (x in 0 until componentX) {
        val normalisation = if (x == 0 && y == 0) 1f else 2f
        applyBasisFunction(
          pixelReader,
          width,
          height,
          normalisation,
          x,
          y,
          factors,
          y * componentX + x,
        )
      }
    }

    val hash = CharArray(1 + 1 + 4 + 2 * (factors.size - 1)) // size flag + max AC + DC + 2 * AC components
    val sizeFlag = (componentX - 1 + (componentY - 1) * 9)
    Base83.encode83(sizeFlag, 1, hash, 0)

    val maximumValue: Float

    if (factors.size > 1) {
      val actualMaximumValue = Utils.max(factors, 1, factors.size)
      val quantisedMaximumValue = floor((actualMaximumValue * 166f - 0.5f).coerceIn(0f, 82f))
      maximumValue = (quantisedMaximumValue + 1) / 166
      Base83.encode83(quantisedMaximumValue.roundToInt(), 1, hash, 1)
    } else {
      maximumValue = 1f
      Base83.encode83(0, 1, hash, 1)
    }

    val dc = factors[0]
    Base83.encode83(encodeDc(dc), 4, hash, 2)

    for (i in 1 until factors.size) {
      Base83.encode83(encodeAc(factors[i], maximumValue), 2, hash, 6 + 2 * (i - 1))
    }

    return hash.concatToString()
  }
}
