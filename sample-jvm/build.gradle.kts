plugins {
  application
  id("org.jetbrains.kotlin.jvm")
}

dependencies {
  implementation("org.jcodec:jcodec-javase:0.2.5")
  implementation(project(":blurhash"))
}

dependencies {
  testImplementation(libs.kotlin.test.junit)
}

application {
  mainClass.set("com.vanniktech.blurhash.sample.jvm.BlurHashJvmKt")
}
