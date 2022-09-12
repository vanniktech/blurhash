package com.vanniktech.blurhash.sample.jvm

import com.vanniktech.blurhash.BlurHash
import java.io.File
import javax.imageio.ImageIO

fun main() {
  // So it's executable both in IntelliJ and on Terminal.
  val directory = File("sample-jvm/images/").takeIf { it.exists() } ?: File("images/")

  // Sample image.
  val input = directory.resolve("blueberries.jpg")
  val image = ImageIO.read(input)

  // Blur hashing.
  val blurHash = BlurHash.encode(
    bufferedImage = image,
    componentX = 5,
    componentY = 4,
  )

  println("Generating blur hash for:")
  println(input.absolutePath)

  println()
  println("Yielded:")
  println(blurHash)

  val blurred = BlurHash.decode(
    blurHash = blurHash,
    width = image.width,
    height = image.height,
  )

  // Create blurred version.
  val output = directory.resolve("blueberries-blurred.png")
  ImageIO.write(blurred!!, "png", output)
  println()
  println("A generated blurred image can be found here:")
  println(output.absolutePath)
}
