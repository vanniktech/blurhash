package com.vanniktech.blurhash.compose.sample.android

import android.app.Application
import com.vanniktech.blurhash.BlurHash
import timber.log.Timber

class BlurHashComposeApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    Timber.plant(Timber.DebugTree())
  }

  override fun onLowMemory() {
    super.onLowMemory()
    BlurHash.clearCache()
  }
}
