plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.jetbrainsCompose)
  alias(libs.plugins.kotlinMultiplatform)
  id("app.cash.licensee")
  id("me.tylerbwong.gradle.metalava")
  id("com.vanniktech.maven.publish")
}

licensee {
  allow("Apache-2.0")
}

metalava {
  filename.set("api/current.txt")
}

kotlin {
  applyDefaultHierarchyTemplate()

  androidTarget {
    publishLibraryVariants("release")
  }
  jvm()
  jvmToolchain(11)
  iosX64 {
    compilerOptions {
      freeCompilerArgs.add("-Xexport-kdoc")
    }
  }
  iosArm64 {
    compilerOptions {
      freeCompilerArgs.add("-Xexport-kdoc")
    }
  }
  iosSimulatorArm64 {
    compilerOptions {
      freeCompilerArgs.add("-Xexport-kdoc")
    }
  }

  sourceSets {
    commonMain.dependencies {
      api(project(":blurhash"))
      api(compose.ui)
    }

    val nonAndroidMain by creating {
      dependsOn(commonMain.get())

      dependencies {
        implementation(libs.skiko)
      }
    }

    jvmMain.get().dependsOn(nonAndroidMain)
    iosX64Main.get().dependsOn(nonAndroidMain)
    iosArm64Main.get().dependsOn(nonAndroidMain)
    iosSimulatorArm64Main.get().dependsOn(nonAndroidMain)
  }
}

android {
  namespace = "com.vanniktech.blurhash.compose"

  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.minSdk.get().toInt()
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  resourcePrefix = "blurhash_compose_"
}
