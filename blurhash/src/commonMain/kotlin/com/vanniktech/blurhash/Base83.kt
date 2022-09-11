package com.vanniktech.blurhash

internal object Base83 {
  val CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz#$%*+,-.:;=?@[]^_{|}~".toCharArray()

  internal fun encode83(
    value: Int,
    length: Int,
    buffer: CharArray,
    offset: Int,
  ) {
    var exp = 1
    var i = 1
    while (i <= length) {
      val digit = (value / exp % CHARS.size)
      buffer[offset + length - i] = CHARS[digit]
      i++
      exp *= CHARS.size
    }
  }

  fun decode83(value: String, from: Int = 0, to: Int = value.length): Int {
    var result = 0
    val chars = value.toCharArray()
    for (i in from until to) {
      result = result * CHARS.size + CHARS.indexOf(chars[i])
    }
    return result
  }
}
