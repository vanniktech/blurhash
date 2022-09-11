package com.vanniktech.blurhash

import kotlin.test.Test
import kotlin.test.assertEquals

class Base83Test {
  @Test fun singleDigits() {
    for (i in 0..82) {
      val expected = Base83.CHARS.concatToString(i, i + 1)

      assertEquals(
        message = "$i encodes",
        expected = expected,
        actual = encode83(i, 1),
      )
    }
  }

  @Test fun encode0000() {
    assertEquals(
      expected = "0000",
      actual = encode83(0, 4),
    )
  }

  @Test fun encode0001() {
    assertEquals(
      expected = "0001",
      actual = encode83(1, 4),
    )
  }

  @Test fun encode0010() {
    assertEquals(
      expected = "0010",
      actual = encode83(83, 4),
    )
  }

  @Test fun encode0011() {
    assertEquals(
      expected = "0011",
      actual = encode83((83 + 1), 4),
    )
  }

  @Test fun encode00X0() {
    assertEquals(
      expected = "00~0",
      actual = encode83((83 * 82), 4),
    )
  }

  @Test fun encode0100() {
    assertEquals(
      expected = "0100",
      actual = encode83((83 * 83), 4),
    )
  }

  @Test fun encode00XX() {
    assertEquals(
      expected = "00~~",
      actual = encode83((83 * 82 + 82), 4),
    )
  }

  @Test fun decode0XXX() {
    assertEquals(
      expected = (82 + 82 * 83 + 82 * 83 * 83),
      actual = Base83.decode83("0~~~"),
    )
  }

  private fun encode83(
    value: Int,
    length: Int,
  ): String {
    val buffer = CharArray(length)
    Base83.encode83(value, length, buffer, 0)
    return buffer.concatToString()
  }
}
