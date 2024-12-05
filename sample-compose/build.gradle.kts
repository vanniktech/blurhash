import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.jetbrainsCompose)
  alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
  androidTarget {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
    }
  }

  jvm("desktop")

  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64(),
  ).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(project(":blurhash-compose"))
      implementation(compose.components.resources)
      implementation(compose.material3)
      implementation(compose.runtime)
    }

    androidMain.dependencies {
      implementation(libs.androidx.activity.compose)
      implementation(libs.timber)
    }

    val desktopMain by getting
    desktopMain.dependencies {
      implementation(compose.desktop.currentOs)

      val osName = System.getProperty("os.name")
      val targetOs = when {
        osName == "Mac OS X" -> "macos"
        osName.startsWith("Win") -> "windows"
        osName.startsWith("Linux") -> "linux"
        else -> error("Unsupported OS: $osName")
      }

      val targetArch = when (val osArch = System.getProperty("os.arch")) {
        "x86_64", "amd64" -> "x64"
        "aarch64" -> "arm64"
        else -> error("Unsupported arch: $osArch")
      }

      val target = "$targetOs-$targetArch"
      implementation("org.jetbrains.skiko:skiko-awt-runtime-$target:${libs.skiko.get().version}")
    }
  }
}

android {
  namespace = "com.vanniktech.blurhash.compose.sample.android"
  compileSdk = libs.versions.compileSdk.get().toInt()

  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].res.srcDirs("src/androidMain/res")
  sourceSets["main"].resources.srcDirs("src/commonMain/resources")

  defaultConfig {
    applicationId = "com.vanniktech.blurhash.compose.sample.android"
    vectorDrawables.useSupportLibrary = true
    minSdk = libs.versions.minSdk.get().toInt()
    targetSdk = libs.versions.targetSdk.get().toInt()
    versionCode = 1
    versionName = project.property("VERSION_NAME").toString()

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
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

  buildFeatures {
    compose = true
  }

  dependencies {
    debugImplementation(libs.leakcanary.android)
  }
}

compose.desktop {
  application {
    mainClass = "com.vanniktech.blurhash.compose.sample.desktop.MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "com.vanniktech.blurhash.compose.sample.desktop"
    }
  }
}
