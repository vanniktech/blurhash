package com.vanniktech.blurhash

import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals

class BlurHashTest {
  @Test fun lorikeet() {
    assert(
      onBlurhaBlurHash = "UFDS:O_LNs#pVrMyX6Vu9]RRw[OXOZxaxWNH",
      expectedBlurHash = "UFDcT@_LNs#pVrIVX6Vu9]RRw[OXOZxaxWNH",
      name = "lorikeet.jpg",
      componentX = 4,
      componentY = 4,
    )
  }

  @Test fun black() {
    assert(
      expectedBlurHash = "U00000fQfQfQfQfQfQfQfQfQfQfQfQfQfQfQ",
      name = "black.png",
      componentX = 4,
      componentY = 4,
    )
  }

  @Test fun small1x1() {
    assert(
      onBlurhaBlurHash = "U1TSUA?bfQ?b~qj[fQj[fQfQfQfQ~qj[fQj[",
      expectedBlurHash = "U~TSUA~q~q~q~q~q~q~q~q~q~q~q~q~q~q~q",
      name = "small1x1.png",
      componentX = 4,
      componentY = 4,
    )
  }

  @Test fun white() {
    assert(
      onBlurhaBlurHash = "U1TSUA?bfQ?b~qj[fQj[fQfQfQfQ~qj[fQj[",
      expectedBlurHash = "U2TSUA~qfQ~q~qj[fQj[fQfQfQfQ~qj[fQj[",
      name = "white.png",
      componentX = 4,
      componentY = 4,
    )
  }

  @Test fun website1() {
    assert(
      expectedBlurHash = "LEHV6nWB2yk8pyo0adR*.7kCMdnj",
      name = "website1.jpg",
      componentX = 4,
      componentY = 3,
    )
  }

  @Test fun website2() {
    assert(
      onBlurhaBlurHash = "LGF5?xYk^6#M@-5c,1J5@[or[Q6.",
      expectedBlurHash = "LGFFaXYk^6#M@-5c,1Ex@@or[j6o",
      name = "website2.jpg",
      componentX = 4,
      componentY = 3,
    )
  }

  @Test fun website3() {
    assert(
      onBlurhaBlurHash = "L6PZfSi_.AyE_3t7t7R**0o#DgR4",
      expectedBlurHash = "L6Pj0^nh.AyE?vt7t7R**0o#DgR4",
      name = "website3.jpg",
      componentX = 4,
      componentY = 3,
    )
  }

  @Test fun website4() {
    assert(
      onBlurhaBlurHash = "LKN]Rv%2Tw=w]~RBVZRi};RPxuwH",
      expectedBlurHash = "LKO2?V%2Tw=^]~RBVZRi};RPxuwH",
      name = "website4.jpg",
      componentX = 4,
      componentY = 3,
    )
  }

  private fun assert(
    name: String,
    expectedBlurHash: String,
    componentX: Int,
    componentY: Int,
    /** The blur hash on https://blurha.sh/ - for quite a few images it's slightly different */
    @Suppress("UNUSED_PARAMETER") onBlurhaBlurHash: String? = null,
  ) {
    val file = File("images/$name")
    val bufferedImage = ImageIO.read(file)
    assertEquals(
      expected = expectedBlurHash,
      actual = BlurHash.encode(bufferedImage, componentX, componentY),
    )

    val image = BlurHash.decode(
      blurHash = expectedBlurHash,
      width = bufferedImage.width,
      height = bufferedImage.height,
    )

    val fileName = file.nameWithoutExtension + "-blurred.png"
    val resolve = file.parentFile.resolve(fileName)
    assertEquals(true, ImageIO.write(image!!, "png", resolve))
  }
}
