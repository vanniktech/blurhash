plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

kotlin {
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

android {
  namespace = "com.vanniktech.blurhash.sample.android"

  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    applicationId = "com.vanniktech.blurhash.sample.android"
    vectorDrawables.useSupportLibrary = true
    minSdk = libs.versions.minSdk.get().toInt()
    targetSdk = libs.versions.targetSdk.get().toInt()
    versionCode = 1
    versionName = project.property("VERSION_NAME").toString()

    vectorDrawables.useSupportLibrary = true

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildFeatures {
    viewBinding = true
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      isShrinkResources = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}

dependencies {
  implementation(project(":blurhash"))
  implementation(libs.timber)
  implementation(libs.material)
}

dependencies {
  debugImplementation(libs.leakcanary.android)
}
