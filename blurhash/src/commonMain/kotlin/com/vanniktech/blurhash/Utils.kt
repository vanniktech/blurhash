package com.vanniktech.blurhash

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.withSign

internal object Utils {
  private fun srgbToLinear(value: Int): Float {
    val v = value / 255f
    return if (v <= 0.04045f) {
      v / 12.92f
    } else {
      ((v + 0.055f) / 1.055f).pow(2.4f)
    }
  }

  internal fun linearToSrgb(value: Float): Int {
    val v = value.coerceIn(0f, 1f)
    return if (v <= 0.0031308f) {
      (v * 12.92f * 255f + 0.5f).toInt()
    } else {
      ((1.055f * v.pow(1 / 2.4f) - 0.055f) * 255 + 0.5f).toInt()
    }
  }

  private fun signPow(value: Float, exp: Float) = abs(value).pow(exp).withSign(value)

  internal fun max(
    values: Array<FloatArray>,
    from: Int,
    endExclusive: Int,
  ): Float {
    var result = Float.NEGATIVE_INFINITY
    for (i in from until endExclusive) {
      for (j in values[i].indices) {
        val value = values[i][j]
        if (value > result) {
          result = value
        }
      }
    }
    return result
  }

  internal fun encodeAc(
    value: FloatArray,
    maximumValue: Float,
  ): Int {
    val quantR = floor((signPow(value[0] / maximumValue, 0.5f) * 9f + 9.5f).coerceIn(0f, 18f))
    val quantG = floor((signPow(value[1] / maximumValue, 0.5f) * 9f + 9.5f).coerceIn(0f, 18f))
    val quantB = floor((signPow(value[2] / maximumValue, 0.5f) * 9f + 9.5f).coerceIn(0f, 18f))
    return (quantR * 19 * 19 + quantG * 19 + quantB).roundToInt()
  }

  internal fun decodeAc(value: Int, maxAc: Float): FloatArray {
    val r = value / (19 * 19)
    val g = (value / 19) % 19
    val b = value % 19
    return floatArrayOf(
      signPow((r - 9) / 9f, 2f) * maxAc,
      signPow((g - 9) / 9f, 2f) * maxAc,
      signPow((b - 9) / 9f, 2f) * maxAc,
    )
  }

  internal fun applyBasisFunction(
    pixelReader: PixelReader,
    width: Int,
    height: Int,
    normalisation: Float,
    i: Int,
    j: Int,
    factors: Array<FloatArray>,
    index: Int,
  ) {
    var r = 0f
    var g = 0f
    var b = 0f

    for (x in 0 until width) {
      for (y in 0 until height) {
        val basis = (normalisation * cos(PI * x * i / width) * cos(PI * y * j / height)).toFloat()
        r += basis * srgbToLinear(pixelReader.readRed(x = x, y = y))
        g += basis * srgbToLinear(pixelReader.readGreen(x = x, y = y))
        b += basis * srgbToLinear(pixelReader.readBlue(x = x, y = y))
      }
    }

    val scale = 1f / (width * height)
    factors[index][0] = r * scale
    factors[index][1] = g * scale
    factors[index][2] = b * scale
  }

  internal fun encodeDc(value: FloatArray): Int {
    val r = linearToSrgb(value[0])
    val g = linearToSrgb(value[1])
    val b = linearToSrgb(value[2])
    return (r shl 16) + (g shl 8) + b
  }

  internal fun decodeDc(colorEnc: Int): FloatArray {
    val r = (colorEnc shr 16) and 255
    val g = (colorEnc shr 8) and 255
    val b = colorEnc and 255
    return floatArrayOf(srgbToLinear(r), srgbToLinear(g), srgbToLinear(b))
  }
}
