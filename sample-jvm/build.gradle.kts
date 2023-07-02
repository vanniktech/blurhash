plugins {
  application
  id("org.jetbrains.kotlin.jvm")
}

kotlin {
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

dependencies {
  implementation(project(":blurhash"))
}

dependencies {
  testImplementation(libs.kotlin.test.junit)
}

application {
  mainClass.set("com.vanniktech.blurhash.sample.jvm.BlurHashJvmKt")
}
