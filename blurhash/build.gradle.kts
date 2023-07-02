import org.jetbrains.kotlin.gradle.plugin.mpp.BitcodeEmbeddingMode
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework

plugins {
  id("org.jetbrains.dokka")
  id("org.jetbrains.kotlin.multiplatform")
  id("org.jetbrains.kotlin.native.cocoapods")
  id("com.android.library")
  id("org.jetbrains.kotlin.plugin.parcelize")
  id("me.tylerbwong.gradle.metalava")
  id("com.vanniktech.maven.publish")
  id("app.cash.licensee")
  id("app.cash.paparazzi")
}

licensee {
  allow("Apache-2.0")
}

metalava {
  filename.set("api/current.txt")
  sourcePaths.setFrom("src/commonMain", "src/androidMain", "src/iosMain", "src/jvmMain")
}

kotlin {
  android {
    publishLibraryVariants("release")
  }
  jvm()
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64(),
  ).forEach {
    it.binaries.all {
      if (this is Framework) {
        isStatic = true
        embedBitcode(if ("YES" == System.getenv("ENABLE_BITCODE")) BitcodeEmbeddingMode.BITCODE else BitcodeEmbeddingMode.MARKER)
      }
    }
  }

  targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
    compilations["main"].kotlinOptions.freeCompilerArgs += "-Xexport-kdoc"
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(libs.kotlin.test.common)
        implementation(libs.kotlin.test.annotations.common)
      }
    }

    val androidMain by getting {
      dependencies {
      }
    }

    val androidUnitTest by getting {
      dependencies {
        implementation(libs.kotlin.test.junit)
      }
    }

    val jvmMain by getting {
      dependencies {
      }
    }

    val jvmTest by getting {
      dependencies {
        implementation(libs.kotlin.test.junit)
      }
    }

    val iosX64Main by getting
    val iosArm64Main by getting
    val iosSimulatorArm64Main by getting
    val iosMain by creating {
      dependsOn(commonMain)
      iosX64Main.dependsOn(this)
      iosArm64Main.dependsOn(this)
      iosSimulatorArm64Main.dependsOn(this)
    }

    val iosX64Test by getting
    val iosArm64Test by getting
    val iosSimulatorArm64Test by getting
    val iosTest by creating {
      dependsOn(commonTest)
      iosX64Test.dependsOn(this)
      iosArm64Test.dependsOn(this)
      iosSimulatorArm64Test.dependsOn(this)
    }
  }

  cocoapods {
    summary = "BlurHash support for iOS, Android and JVM via Kotlin Multiplatform"
    homepage = "https://github.com/vanniktech/blurhash"
    license = "MIT"
    name = "BlurHash"
    authors = "Niklas Baudy"
    version = project.property("VERSION_NAME").toString()
  }
}

android {
  namespace = "com.vanniktech.blurhash"

  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.minSdk.get().toInt()
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  resourcePrefix = "blurhash_"
}
