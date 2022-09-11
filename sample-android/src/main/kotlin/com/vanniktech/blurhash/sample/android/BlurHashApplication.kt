package com.vanniktech.blurhash.sample.android

import android.app.Application
import com.vanniktech.blurhash.BlurHash
import timber.log.Timber

class BlurHashMainApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    Timber.plant(Timber.DebugTree())
  }

  override fun onLowMemory() {
    super.onLowMemory()
    BlurHash.clearCache()
  }
}
