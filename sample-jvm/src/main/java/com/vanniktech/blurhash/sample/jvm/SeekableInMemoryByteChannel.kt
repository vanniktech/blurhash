/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.vanniktech.blurhash.sample.jvm

import org.jcodec.common.io.SeekableByteChannel
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.ClosedChannelException
import java.util.concurrent.atomic.AtomicBoolean

internal class SeekableInMemoryByteChannel(
  private var data: ByteArray,
) : SeekableByteChannel {
  private val closed = AtomicBoolean()
  private var position = 0
  private var size = data.size

  override fun position() = position.toLong()

  override fun setPosition(newPosition: Long) = apply {
    ensureOpen()
    if (newPosition < 0L || newPosition > Int.MAX_VALUE) {
      throw IOException("Position has to be in range 0.. " + Int.MAX_VALUE)
    }
    position = newPosition.toInt()
  }

  override fun size() = size.toLong()

  override fun truncate(newSize: Long) = apply {
    require(!(newSize < 0L || newSize > Int.MAX_VALUE)) { "Size has to be in range 0.. " + Int.MAX_VALUE }

    if (size > newSize) {
      size = newSize.toInt()
    }

    if (position > newSize) {
      position = newSize.toInt()
    }
  }

  override fun read(dst: ByteBuffer): Int {
    ensureOpen()
    var wanted = dst.remaining()
    val possible = size - position
    if (possible <= 0) {
      return -1
    }
    if (wanted > possible) {
      wanted = possible
    }
    dst.put(data, position, wanted)
    position += wanted
    return wanted
  }

  override fun close() = closed.set(true)

  override fun isOpen() = !closed.get()

  override fun write(src: ByteBuffer): Int {
    ensureOpen()
    var wanted = src.remaining()
    val possibleWithoutResize = size - position
    if (wanted > possibleWithoutResize) {
      val newSize = position + wanted
      if (newSize < 0) { // Overflow.
        resize(Int.MAX_VALUE)
        wanted = Int.MAX_VALUE - position
      } else {
        resize(newSize)
      }
    }
    src[data, position, wanted]
    position += wanted
    if (size < position) {
      size = position
    }
    return wanted
  }

  private fun resize(newLength: Int) {
    var len = data.size
    if (len <= 0) {
      len = 1
    }

    if (newLength < NAIVE_RESIZE_LIMIT) {
      while (len < newLength) {
        len = len shl 1
      }
    } else { // Avoid overflow.
      len = newLength
    }

    data = data.copyOf(len)
  }

  private fun ensureOpen() {
    if (!isOpen) {
      throw ClosedChannelException()
    }
  }

  companion object {
    private const val NAIVE_RESIZE_LIMIT = Int.MAX_VALUE shr 1
  }
}
