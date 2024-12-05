package com.vanniktech.blurhash

class PixelWriterArgb8888(
  width: Int,
  height: Int,
) : PixelWriter<IntArray> {
  private val pixels = IntArray(width * height)

  override fun write(
    x: Int,
    y: Int,
    width: Int,
    red: Int,
    green: Int,
    blue: Int,
  ) {
    pixels[x + width * y] = 0xFF000000.toInt() or (red shl 16) or (green shl 8) or blue
  }

  override fun get() = pixels
}
