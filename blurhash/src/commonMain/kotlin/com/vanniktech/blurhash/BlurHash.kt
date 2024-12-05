package com.vanniktech.blurhash

const val DEFAULT_PUNCH = 1f

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object BlurHash {
  /**
   * Clears calculations stored in memory cache.
   * The cache is not big, but will increase when many image sizes are used,
   * If the app needs memory, it is recommended to clear it.
   */
  fun clearCache()

  /**
   * Returns the average sRGB color for the given [blurHash] in respect to its [punch].
   */
  fun averageColor(blurHash: String, punch: Float = DEFAULT_PUNCH): Int?
}
