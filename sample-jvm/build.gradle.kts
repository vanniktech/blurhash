plugins {
  application
  id("com.dropbox.dependency-guard")
  id("org.jetbrains.kotlin.jvm")
}

kotlin {
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of("11"))
  }
}

dependencies {
  implementation(project(":blurhash"))
}

dependencies {
  testImplementation(libs.kotlin.test.junit)
}

dependencyGuard {
  configuration("runtimeClasspath")
}

application {
  mainClass.set("com.vanniktech.blurhash.sample.jvm.BlurHashJvmKt")
}
