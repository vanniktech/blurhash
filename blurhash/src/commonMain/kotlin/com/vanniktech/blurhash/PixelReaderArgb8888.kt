package com.vanniktech.blurhash

class PixelReaderArgb8888(
  private val pixels: IntArray,
  private val width: Int,
) : PixelReader {
  override fun readRed(x: Int, y: Int): Int = pixels[y * width + x] shr 16 and 0xff

  override fun readGreen(x: Int, y: Int): Int = pixels[y * width + x] shr 8 and 0xff

  override fun readBlue(x: Int, y: Int): Int = pixels[y * width + x] and 0xff
}
