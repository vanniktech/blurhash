package com.vanniktech.blurhash

/** Counterpart of [PixelReader]. */
interface PixelWriter<T : Any> {
  fun write(x: Int, y: Int, width: Int, red: Int, green: Int, blue: Int)

  fun get(): T // TODO(nik) remove this together with the generic?
}
