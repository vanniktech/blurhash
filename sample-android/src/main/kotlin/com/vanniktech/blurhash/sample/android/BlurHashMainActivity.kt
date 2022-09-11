package com.vanniktech.blurhash.sample.android

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vanniktech.blurhash.BlurHash
import com.vanniktech.blurhash.sample.android.databinding.ActivityMainBinding

class BlurHashMainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // Sample image.
    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.blueberries)
    binding.original.setImageBitmap(bitmap)
    binding.original.layoutParams.height = bitmap.height
    binding.original.layoutParams.width = bitmap.width

    // Blur hashing.
    val blurHash = BlurHash.encode(bitmap, componentX = 5, componentY = 4)
    binding.blurHash.text = blurHash

    // Create blurred version.
    // We don't need to create a Bitmap in its full size.
    // Let Android scale it up for us as scaling is cheaper than generating a larger image.
    binding.blurred.setImageBitmap(
      BlurHash.decode(
        blurHash = blurHash,
        width = bitmap.width / 4,
        height = bitmap.height / 4,
      ),
    )
    binding.blurred.layoutParams.height = bitmap.height
    binding.blurred.layoutParams.width = bitmap.width
  }
}
