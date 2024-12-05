package com.vanniktech.blurhash

interface PixelReader {
  /** Returns the red components of the pixel at the [x]-[y] coordinate. */
  fun readRed(x: Int, y: Int): Int

  /** Returns the green components of the pixel at the [x]-[y] coordinate. */
  fun readGreen(x: Int, y: Int): Int

  /** Returns the blue components of the pixel at the [x]-[y] coordinate. */
  fun readBlue(x: Int, y: Int): Int
}
