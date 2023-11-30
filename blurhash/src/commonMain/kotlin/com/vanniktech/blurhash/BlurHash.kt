package com.vanniktech.blurhash

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object BlurHash {
  /**
   * Clears calculations stored in memory cache.
   * The cache is not big, but will increase when many image sizes are used,
   * if the app needs memory it is recommended to clear it.
   */
  fun clearCache()
}
