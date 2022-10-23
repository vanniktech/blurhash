plugins {
  application
  id("com.dropbox.dependency-guard")
  id("org.jetbrains.kotlin.jvm")
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
